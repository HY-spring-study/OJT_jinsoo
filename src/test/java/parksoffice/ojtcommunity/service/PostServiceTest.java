package parksoffice.ojtcommunity.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import parksoffice.ojtcommunity.domain.board.Board;
import parksoffice.ojtcommunity.domain.board.Post;
import parksoffice.ojtcommunity.domain.member.Member;
import parksoffice.ojtcommunity.exception.PostNotFoundException;
import parksoffice.ojtcommunity.repository.board.PostRecommendationRepository;
import parksoffice.ojtcommunity.repository.board.PostRepository;
import parksoffice.ojtcommunity.repository.member.MemberRepository;

import java.util.Arrays;
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
        Post post1 = Post.builder().title("Test Title One").content("Content 1").build();
        Post post2 = Post.builder().title("Another Test Title").content("Content 2").build();
        List<Post> posts = Arrays.asList(post1, post2);
        when(postRepository.findByTitleContaining("Test")).thenReturn(posts);

        // when: searchPostsByTitle 호출
        List<Post> result = postService.searchPostsByTitle("Test");

        // then: 결과 검증
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(postRepository, times(1)).findByTitleContaining("Test");
    }

}
