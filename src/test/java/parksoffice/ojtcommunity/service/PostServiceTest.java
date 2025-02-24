package parksoffice.ojtcommunity.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import parksoffice.ojtcommunity.domain.board.Board;
import parksoffice.ojtcommunity.domain.board.Post;
import parksoffice.ojtcommunity.domain.member.Member;
import parksoffice.ojtcommunity.dto.board.UpdatePostDto;
import parksoffice.ojtcommunity.exception.AlreadyRecommendedException;
import parksoffice.ojtcommunity.exception.PostNotFoundException;
import parksoffice.ojtcommunity.repository.board.PostRecommendationRepository;
import parksoffice.ojtcommunity.repository.board.PostRepository;
import parksoffice.ojtcommunity.repository.member.MemberRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostRecommendationRepository postRecommendationRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private PostService postService;

    /**
     * 신규 게시글 등록이 정상적으로 동작하는지 확인한다.
     */
    @Test
    void testRegisterPost_Success() {
        // given: 신규 게시글 객체 생성
        Board board = Board.builder()
                .name("Free Board")
                .description("자유게시판")
                .build();
        Member author = Member.builder()
                .id(1L)
                .username("author")
                .password("pass")
                .build();
        Post newPost = Post.builder()
                .title("Test Title")
                .content("Test Content")
                .author(author)
                .board(board)
                .build();

        when(postRepository.save(newPost)).thenReturn(newPost);

        // when: registerPost 호출
        Post savedPost = postService.registerPost(newPost);

        // then: 반환된 게시글 검증
        assertNotNull(savedPost);
        assertEquals("Test Title", savedPost.getTitle());
        verify(postRepository, times(1)).save(newPost);
    }

    /**
     * 게시글 ID로 조회 성공 시, 해당 게시글을 반환한다.
     */
    @Test
    void testGetPostById_Success() {
        // given: 게시글 객체 생성 및 조회 결과 설정
        Post post = Post.builder()
                .title("Test Title")
                .content("Test Content")
                .author(Member.builder().id(1L).username("author").password("pass").build())
                .board(Board.builder().name("Free Board").description("자유게시판").build())
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // when: getPostById 호출
        Post result = postService.getPostById(1L);

        // then: 반환된 게시글 검증
        assertNotNull(result);
        assertEquals("Test Title", result.getTitle());
        verify(postRepository, times(1)).findById(1L);
    }

    /**
     * 게시글 ID 조회 시, 게시글이 없으면 PostNotFoundException 발생한다.
     */
    @Test
    void testGetPostById_NotFound() {
        // given: 게시글 조회 결과가 빈 Optional
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        // then: PostNotFoundException 발생 검증
        assertThrows(PostNotFoundException.class, () -> postService.getPostById(1L));
        verify(postRepository, times(1)).findById(1L);
    }

    /**
     * 게시글 제목 검색 시, 키워드를 포함하는 게시글 목록을 반환한다.
     */
    @Test
    void testSearchPostByTitle() {
        // given: "Test"를 포함하는 제목의 게시글 리스트 생성
        Post post1 = Post.builder().title("Test Title One").content("Content 1").build(); // 검색 조건 충족
        Post post2 = Post.builder().title("Another Title").content("Content 2").build(); // 검색 조건 미충족
        List<Post> posts = Collections.singletonList(post1); // 불변 리스트(요소 1개)로, 요소를 추가/삭제할 필요가 없을 때 사용하면 좋음
        when(postRepository.findByTitleContaining("Test")).thenReturn(posts);

        // when: searchPostsByTitle 호출
        List<Post> result = postService.searchPostsByTitle("Test");

        // then: 결과 검증
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository, times(1)).findByTitleContaining("Test");
    }

    /**
     * 게시글 내용 검색 시, 키워드를 포함하는 게시글 목록을 반환한다.
     */
    @Test
    void testSearchPostByContent() {
        // given: "Sample"을 포함하는 내용의 게시글 리스트 생성
        Post post1 = Post.builder().title("Title").content("Sample Content").build(); // 검색 조건 충족
        Post post2 = Post.builder().title("Title").content("Simple Content").build(); // 검색 조건 미충족

        when(postRepository.findByContentContaining("Sample")).thenReturn(Collections.singletonList(post1)); // 불변 리스트(요소 1개)로, 요소를 추가/삭제할 필요가 없을 때 사용하면 좋음

        // when: searchPostByContent 호출
        List<Post> result = postService.searchPostsByContent("Sample");

        // then: 결과 리스트 검증
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(postRepository, times(1)).findByContentContaining("Sample");
    }

    /**
     * 게시글 업데이트가 성공적으로 수행되어 제목과 본문이 변경된다.
     */
    @Test
    void testUpdatePost_Success() {
        // given:
        // 기존 게시글 객체 생성
        Post existingPost = Post.builder()
                .title("Old Title")
                .content("Old Content")
                .author(Member.builder().id(1L).username("author").password("pass").build())
                .board(Board.builder().name("Free Board").description("자유게시판").build())
                .build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(existingPost));

        // 업데이트용 DTO 생성
        UpdatePostDto updatePostDto = new UpdatePostDto();
        updatePostDto.setTitle("New Title");
        updatePostDto.setContent("New Content");

        when(postRepository.save(existingPost)).thenReturn(existingPost);

        // when: updatePost 호출
        Post updatedPost = postService.updatePost(1L, updatePostDto);

        // then: 업데이트 된 게시글 검증
        assertNotNull(updatedPost);
        assertEquals("New Title", updatedPost.getTitle());
        assertEquals("New Content", updatedPost.getContent());
        verify(postRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(existingPost);
    }

    /**
     * 게시글 업데이트 시, 해당 게시글이 없으면 PostNotFoundException을 발생시킨다.
     */
    @Test
    void testUpdatePost_NotFound() {
        // given: 게시글 조회 결과가 빈 Optional
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        UpdatePostDto updatePostDto = new UpdatePostDto();
        updatePostDto.setTitle("New Title");
        updatePostDto.setContent("New Content");

        // then: PostNotFoundException 발생 검증
        assertThrows(PostNotFoundException.class, () -> postService.updatePost(1L, updatePostDto));
        verify(postRepository, times(1)).findById(1L);
    }

    /**
     * 게시글 삭제가 성공적으로 수행된다.
     */
    @Test
    void testDeletePostById_Success() {
        // given: 게시글 존재함을 시뮬레이션
        when(postRepository.existsById(1L)).thenReturn(true);

        // when: deletePostById 호출
        postService.deletePostById(1L);

        // then: 존재 여부 확인 및 삭제 호출 검증
        verify(postRepository, times(1)).existsById(1L);
        verify(postRepository, times(1)).deleteById(1L);
    }

    /**
     * 게시글 삭제 시, 게시글이 존재하지 않으면 PostNotFoundException을 발생시킨다.
     */
    @Test
    void testDeletePostById_NotFound() {
        // given: 게시글이 존재하지 않음을 시뮬레이션
        when(postRepository.existsById(1L)).thenReturn(false);

        // then: PostNotFoundException 발생 검증
        assertThrows(PostNotFoundException.class, () -> postService.deletePostById(1L));
        verify(postRepository, times(1)).existsById(1L);
        verify(postRepository, never()).deleteById(anyLong());
    }

    /**
     * 게시글 추천이 성공적으로 처리되어, 해당 게시글의 추천 컬렉션에 추천 객체가 추가된다.
     */
    @Test
    void testRecommendPost_Success() {
        // given:
        // 게시글 객체 생성
        Board board = Board.builder().name("Free Board").description("자유게시판").build();
        Member author = Member.builder().id(1L).username("author").password("pass").build();
        Post post = Post.builder()
                .title("Test Title")
                .content("Test Content")
                .author(author)
                .board(board)
                .build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // 중복 추천이 없는 상황을 시뮬레이션
        when(postRecommendationRepository.existsByPostIdAndMemberId(1L, 2L)).thenReturn(false);

        // memberRepository.getReferenceById()를 통한 회원 참조(프록시) 획득
        Member memberRef = Member.builder().id(2L).build();
        when(memberRepository.getReferenceById(2L)).thenReturn(memberRef);

        // when: 추천 처리 메서드 호출
        postService.recommendPost(1L, 2L);

        // then: 게시글 조회, 중복 추천 확인, 회원 참조 획득, 그리고 게시글 저장이 올바르게 호출되었는지 검증
        verify(postRepository, times(1)).findById(1L);
        verify(postRecommendationRepository, times(1)).existsByPostIdAndMemberId(1L, 2L);
        verify(memberRepository, times(1)).getReferenceById(2L);
        verify(postRepository, times(1)).save(post);

        // 게시글의 추천 컬렉션에 추천 객체가 추가되었음을 확인 (추천 수가 1이어야 함)
        assertEquals(1, post.getRecommendations().size());
    }

    /**
     * 같은 회원이 이미 추천한 경우 AlreadyRecommendedException을 발생시킨다.
     */
    @Test
    void testRecommendPost_AlreadyRecommended() {
        // given: 게시글 객체 생성
        Board board = Board.builder().name("Free Board").description("자유게시판").build();
        Member author = Member.builder().id(1L).username("author").password("pass").build();
        Post post = Post.builder()
                .title("Test Title")
                .content("Test Content")
                .author(author)
                .board(board)
                .build();
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        // 중복 추천 상황: 이미 추천한 회원이 있음
        when(postRecommendationRepository.existsByPostIdAndMemberId(1L, 2L)).thenReturn(true);

        // then: 추천 처리 시 AlreadyRecommendedException 발생 검증
        assertThrows(AlreadyRecommendedException.class, () -> postService.recommendPost(1L, 2L));
        verify(postRepository, times(1)).findById(1L);
        verify(postRecommendationRepository, times(1)).existsByPostIdAndMemberId(1L, 2L);
        verify(memberRepository, never()).getReferenceById(anyLong());
        verify(postRepository, never()).save(any(Post.class));
    }

}
