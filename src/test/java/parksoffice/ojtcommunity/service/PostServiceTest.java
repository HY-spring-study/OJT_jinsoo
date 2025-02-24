package parksoffice.ojtcommunity.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import parksoffice.ojtcommunity.domain.board.Board;
import parksoffice.ojtcommunity.domain.board.Post;
import parksoffice.ojtcommunity.domain.member.Member;
import parksoffice.ojtcommunity.repository.board.PostRecommendationRepository;
import parksoffice.ojtcommunity.repository.board.PostRepository;
import parksoffice.ojtcommunity.repository.member.MemberRepository;

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

}
