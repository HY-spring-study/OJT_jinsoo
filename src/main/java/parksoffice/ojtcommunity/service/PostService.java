package parksoffice.ojtcommunity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import parksoffice.ojtcommunity.domain.board.Post;
import parksoffice.ojtcommunity.domain.board.PostRecommendation;
import parksoffice.ojtcommunity.domain.member.Member;
import parksoffice.ojtcommunity.dto.board.UpdatePostDto;
import parksoffice.ojtcommunity.exception.AlreadyRecommendedException;
import parksoffice.ojtcommunity.exception.PostNotFoundException;
import parksoffice.ojtcommunity.repository.board.PostRecommendationRepository;
import parksoffice.ojtcommunity.repository.board.PostRepository;
import parksoffice.ojtcommunity.repository.member.MemberRepository;

import java.util.List;

@Service
@Transactional // 기본적으로 쓰기 작업에 대해 트랜잭션을 적용한다.
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostRecommendationRepository postRecommendationRepository;
    private final MemberRepository memberRepository;

    /**
     * 신규 게시글 등록
     * <p>
     *     전달받은 게시글 엔티티를 저장소에 저장하고, 저장된 게시글 엔티티를 반환한다.
     * </p>
     *
     * @param post 등록할 게시글 엔티티
     * @return 저장된 게시글 엔티티
     */
    public Post registerPost(Post post) {
        return postRepository.save(post);
    }

    /**
     * 게시글 ID로 단건 조회를 수행
     * <p>
     *     해당 ID를 가진 게시글이 존재하면 이를 반환하며, 없을 경우 PostNotFoundException을 발생시킨다.
     * </p>
     *
     * @param id 조회할 게시글의 식별자(id)
     * @return 조회된 게시글 엔티티
     * @throws PostNotFoundException 게시글이 없을 경우
     */
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));
    }

    /**
     * 게시글 제목에 특정 키워드가 포함된 게시글 목록을 조회
     * <p>
     *     검색 결과가 없을 경우 빈 리스트를 반환한다.
     * </p>
     *
     * @param keyword 검색할 키워드
     * @return 키워드를 포함하는 게시글 목록
     */
    @Transactional(readOnly = true)
    public List<Post> searchPostsByTitle(String keyword) {
        return postRepository.findByTitleContaining(keyword);
    }

    /**
     * 게시글 내용에 특정 키워드가 포함된 게시글 목록을 조회
     * <p>
     *     검색 결과가 없을 경우 빈 리스트를 반환한다.
     * </p>
     *
     * @param keyword 검색할 키워드
     * @return 키워드를 포함하는 게시글 목록
     */
    @Transactional(readOnly = true)
    public List<Post> searchPostsByContent(String keyword) {
        return postRepository.findByContentContaining(keyword);
    }

    /**
     * 게시글 정보를 업데이트한다.
     * 주어진 게시글 ID로 기존 게시글을 조회한 후, 제목과 본문을 업데이트한다.
     * 만약 해당 게시글이 존재하지 않으면 PostNotFoundException을 발생시킨다.
     *
     * @param id 업데이트할 게시글의 식별자
     * @param updatePostDto 업데이트할 게시글 dto (제목과 본문)
     * @return 업데이트된 게시글 엔티티
     * @throws PostNotFoundException 게시글이 존재하지 않을 경우
     */
    public Post updatePost(Long id, UpdatePostDto updatePostDto) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));

        // 업데이트 대상 필드만 수정 (작성자, 조회수, 추천수 등은 변경하지 않음)
        existingPost.setTitle(updatePostDto.getTitle());
        existingPost.setContent(updatePostDto.getContent());

        return postRepository.save(existingPost);
    }

    /**
     * 게시글을 삭제한다
     * 삭제 전 게시글 존재 여부를 확인하여, 없으면 PostNotFoundException을 발생시킨다.
     *
     * @param postId 삭제할 게시글의 식별자
     * @throws PostNotFoundException 게시글이 존재하지 않을 경우
     */
    public void deletePostById(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostNotFoundException("Post not found with id: " + postId);
        }
        postRepository.deleteById(postId);
    }

    /**
     * 게시글에 대한 추천을 처리한다
     * 각 회원은 한 게시글에 대해 한 번만 추천할 수 있다.
     *
     * @param postId 추천할 게시글의 식별자
     * @param memberId 추천하는 회원의 식별자
     * @throws AlreadyRecommendedException 이미 추천한 회원인 경우
     * @throws PostNotFoundException 게시글이 존재하지 않을 경우
     */
    public void recommendPost(Long postId, Long memberId) {
        // 게시글 조회 (없으면 예외 발생)
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + postId));

        // 같은 회원이 이미 추천했는지 확인
        if (postRecommendationRepository.existsByPostIdAndMemberId(postId, memberId)) {
            throw new AlreadyRecommendedException("User has already recommended this post.");
        }

        // memberRepository.getReferenceById()를 사용하여, DB 조회 없이 Member의 참조 객체(프록시)를 획득한다.
        Member memberRef = memberRepository.getReferenceById(memberId);

        // 새로운 추천 객체를 생성한다.
        PostRecommendation recommendation = PostRecommendation.builder()
                .post(post)
                .member(memberRef)
                .build();

        // 생성한 추천 객체를 게시글의 추천 컬렉션에 추가한다.
        post.addRecommendation(recommendation);

        // 게시글을 저장한다. cascade 옵션에 의해 Post의 자식에 해당하는 추천 정보도 함께 저장된다.
        postRepository.save(post);
    }

}
