package parksoffice.ojtcommunity.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import parksoffice.ojtcommunity.domain.board.Post;
import parksoffice.ojtcommunity.service.PostService;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final PostService postService;

    /**
     * 게시판 코드에 해당하는 게시글 목록을 조회하여 "board/lists" 뷰를 반환한다.
     * URL 예시: /board/lists/?id=male
     *
     * @param boardCode 쿼리 파라미터 'id'에 해당하는 게시판 코드 (예: "male", "female")
     * @param model Thymeleaf 모델 객체
     * @return 게시글 목록 뷰 이름
     */
    @GetMapping("/lists")
    public String listBoardPosts(@RequestParam("id") String boardCode, Model model) {
        List<Post> posts = postService.getPostsByBoardCode(boardCode);
        model.addAttribute("posts", posts);
        model.addAttribute("boardCode", boardCode);
        log.info("Listing posts for board code: {}", boardCode);
        return "board/lists";
    }

    @GetMapping("/view")
    public String viewBoardPost(@RequestParam("id") String boardCode,
                                @RequestParam("no") Long postId,
                                Model model) {
        Post post = postService.getPostById(postId);
        // 조회된 게시글의 Board 코드가 요청된 board 코드가 일치하는지 확인한다.
        if(!post.getBoard().getCode().equalsIgnoreCase(boardCode)) {
            log.warn("Board code mismatch: post board code {} vs request board code {}",
                    post.getBoard().getCode(), boardCode);
            // 오류 메시지를 URL 파라미터로 전달하며, 오류 페이지로 리다이렉트
            return "redirect:/error?message=Board%20code%20mismatch";
        }
        model.addAttribute("post", post);
        model.addAttribute("boardCode", boardCode);
        log.info("Viewing post with id: {} on board code: {}", postId, boardCode);
        return "board/view";
    }
}
