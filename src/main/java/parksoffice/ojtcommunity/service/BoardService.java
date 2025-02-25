package parksoffice.ojtcommunity.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import parksoffice.ojtcommunity.domain.board.Board;
import parksoffice.ojtcommunity.exception.BoardNotFoundException;
import parksoffice.ojtcommunity.exception.MemberNotFoundException;
import parksoffice.ojtcommunity.repository.board.BoardRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    /**
     * 전체 게시판 목록을 조회하여 반환한다.
     *
     * @return 모든 Board 엔티티 목록
     */
    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    /**
     * 게시판 코드로 게시판 조회
     * 게시판이 존재하지 않으면 BoardNotFoundException을 발생시킨다.
     *
     * @param boardCode 조회할 게시판 코드
     * @return 조회된 게시판 엔티티
     * @throws BoardNotFoundException 게시판을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public Board getBoardByCode(String boardCode) {
        return boardRepository.findByCode(boardCode)
                .orElseThrow(() -> new BoardNotFoundException("Board not found with code: " + boardCode));
    }
}
