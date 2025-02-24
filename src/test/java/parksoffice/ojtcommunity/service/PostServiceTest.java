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

}
