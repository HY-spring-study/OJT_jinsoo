package parksoffice.ojtcommunity.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import parksoffice.ojtcommunity.domain.board.Board;
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
}
