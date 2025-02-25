package parksoffice.ojtcommunity.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
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

    /**
     * 신규 게시글 작성 폼을 표시하는 "board/create" 뷰를 반환한다.
     * URL 예시: /board/new/?id=male
     *
     * @param boardCode 쿼리 파라미터 'id'에 해당하는 게시판 코드
     * @param model Thymeleaf 모델 객체
     * @return 게시글 작성 폼 뷰 이름
     */
    @GetMapping("/new")
    public String showCreateForm(@RequestParam("id") String boardCode, Model model) {
        Post post = Post.builder().build();
        model.addAttribute("post", post);
        model.addAttribute("boardCode", boardCode);
        log.info("Displaying create post form for board code: {}", boardCode);
        return "board/create";
    }

    /**
     * 신규 게시글 작성 요청을 처리한다.
     * 유효성 검증 실패 시 작성 폼으로 되돌아간다.
     * URL 예시: /board/new/?id=male
     *
     * @param boardCode 쿼리 파라미터 'id'에 해당하는 게시판 코드
     * @param post 작성할 게시글 엔티티 (폼 데이터를 바인딩)
     * @param bindingResult 유효성 검증 결과
     * @return 게시글 목록 페이지 리다이렉트 또는 작성 폼 뷰 이름
     */
    @PostMapping("/new")
    public String createPost(@RequestParam("id") String boardCode,
                             @ModelAttribute("post") @Valid Post post,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Post creation failed for board code: {} due to validation errors", boardCode);
            return "board/create";
        }
        postService.registerPost(post);
        log.info("Created post with title: {} for board code: {}", post.getTitle(), boardCode);
        return "redirect:/board/lists?id=" + boardCode;
    }
    
}
