package parksoffice.ojtcommunity.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import parksoffice.ojtcommunity.domain.board.Board;
import parksoffice.ojtcommunity.exception.BoardNotFoundException;
import parksoffice.ojtcommunity.repository.board.BoardRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoardServiceTest {

    @Mock
    BoardRepository boardRepository;

    @InjectMocks
    private BoardService boardService;

    private Board board1;
    private Board board2;

    @BeforeEach
    public void setup() {
        // 예시 Board 객체 생성 (빌더 사용)
        board1 = Board.builder()
                .code("male")
                .name("자기소개(남)")
                .description("남자가 본인을 소개하는 게시판")
                .build();

        board2 = Board.builder()
                .code("female")
                .name("자기소개(여)")
                .description("여자가 본인을 소개하는 게시판")
                .build();
    }

    @Test
    public void testGetAllBoards() {
        // given : repository에서 두 개의 Board를 반환하도록 설정
        List<Board> boards = Arrays.asList(board1, board2);
        when(boardRepository.findAll()).thenReturn(boards);

        // when : 전체 게시판 조회 메서드 호출
        List<Board> result = boardService.getAllBoards();

        // then : 결과가 null이 아니고, 목록의 크기가 2개인지 확인
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(boardRepository, times(1)).findAll();
    }

    @Test
    public void testGetBoardByCode_Success() {
        // given : boardRepository.findByCode("male")가 board1을 반환하도록 설정
        when(boardRepository.findByCode("male")).thenReturn(Optional.of(board1));

        // when : 게시판 코드 "male"로 조회
        Board result = boardService.getBoardByCode("male");

        // then : 결과가 null이 아니며, 코드가 "male"인지 확인
        assertNotNull(result);
        assertEquals("male", result.getCode());
        verify(boardRepository, times(1)).findByCode("male");
    }

    @Test
    public void testGetBoardByCode_NotFound() {
        // given : 존재하지 않는 게시판 코드를 조회할 경우 빈 Optional 반환
        when(boardRepository.findByCode("unknown")).thenReturn(Optional.empty());

        // when & then : 조회 시 BoardNotFoundException이 발생하는지 확인
        BoardNotFoundException exception = assertThrows(BoardNotFoundException.class, () -> {
            boardService.getBoardByCode("unknown");
        });
        assertEquals("Board not found with code: unknown", exception.getMessage());
        verify(boardRepository, times(1)).findByCode("unknown");
    }
}
