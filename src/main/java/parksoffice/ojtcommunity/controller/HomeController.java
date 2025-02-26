package parksoffice.ojtcommunity.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import parksoffice.ojtcommunity.domain.board.Board;
import parksoffice.ojtcommunity.service.BoardService;

import java.util.List;

/**
 * HomeController
 *
 * <p>
 *     루트 URL("/")에 대한 요청을 처리.
 * </p>
 *
 * @author CRISPYTYPER
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final BoardService boardService;

    /**
     * 메인 페이지를 반환한다.
     * <p>
     *     전체 게시판 목록(예: "자기소개(남)", "자기소개(여)" 등)을 조회하여 모델에 추가하고,
     *     로그인 컴포넌트와 함께 메인 페이지 뷰를 렌더링한다.
     * </p>
     *
     * @param model Thymeleaf 모델 객체
     * @return 메인 페이지 뷰 이름 ("index")
     */
    @GetMapping("/")
    public String home(Model model) {
        List<Board> boards = boardService.getAllBoards();
        model.addAttribute("boards", boards);

        // Thymeleaf layout의 head 영역에 전달할 동적 변수들 추가
        model.addAttribute("pageTitle", "메인 페이지");
        model.addAttribute("pageDescription", "메인 페이지입니다.");

        log.info("Loaded {} boards", boards.size());
        return "index";
    }
}
