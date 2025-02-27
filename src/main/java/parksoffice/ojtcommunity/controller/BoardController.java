package parksoffice.ojtcommunity.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import parksoffice.ojtcommunity.domain.board.Board;
import parksoffice.ojtcommunity.domain.board.Post;
import parksoffice.ojtcommunity.domain.member.Member;
import parksoffice.ojtcommunity.dto.board.CreatePostDto;
import parksoffice.ojtcommunity.dto.board.UpdatePostDto;
import parksoffice.ojtcommunity.exception.AlreadyRecommendedException;
import parksoffice.ojtcommunity.service.BoardService;
import parksoffice.ojtcommunity.service.PostService;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final PostService postService;
    private final BoardService boardService;

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
        // Board 정보를 별도로 조회
        Board board = boardService.getBoardByCode(boardCode);
        List<Post> posts = postService.getPostsByBoardCode(boardCode);
        model.addAttribute("posts", posts);
        model.addAttribute("board", board);

        // Thymeleaf layout의 head 영역에 전달할 동적 변수들 추가
        model.addAttribute("pageTitle", board.getName());
        model.addAttribute("pageDescription", board.getDescription());

        log.info("Listing posts for board: {}", board.getName());
        return "board/lists";
    }

    /**
     * 특정 게시판과 게시글 번호에 해당하는 게시글 상세 정보를 조회하여 "board/view" 뷰를 반환한다.
     * URL 예시: /board/view/?id=male&no=6388256
     *
     * @param boardCode 쿼리 파라미터 'id'에 해당하는 게시판 코드
     * @param postId 쿼리 파라미터 'no'에 해당하는 게시글 번호
     * @param model Thymeleaf 모델 객체
     * @return 게시글 상세 뷰 이름
     */
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

        // Thymeleaf layout의 head 영역에 전달할 동적 변수들 추가
        model.addAttribute("pageTitle", post.getTitle() + "-" + post.getBoard().getName());
        model.addAttribute("pageDescription", "게시글 상세 페이지입니다.");

        log.info("Viewing post with id: {} on board code: {}", postId, boardCode);
        return "board/view";
    }

    /**
     * 신규 게시글 작성 폼을 표시하는 "board/createPost" 뷰를 반환한다.
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

        Board board = boardService.getBoardByCode(boardCode);

        log.info("Displaying create post form for board code: {}", boardCode);

        // Thymeleaf layout의 head 영역에 전달할 동적 변수들 추가
        model.addAttribute("pageTitle", "게시글 작성 - " + board.getName());
        model.addAttribute("pageDescription", "게시글 작성 페이지입니다.");

        return "board/createPost";
    }

    /**
     * 신규 게시글 작성 요청을 처리한다.
     * 유효성 검증 실패 시 작성 폼으로 되돌아간다.
     * URL 예시: /board/new/?id=male
     *
     * @param boardCode 쿼리 파라미터 'id'에 해당하는 게시판 코드
     * @param createPostDto 작성할 게시글 DTO (폼 데이터를 바인딩)
     * @param bindingResult 유효성 검증 결과
     * @return 게시글 목록 페이지 리다이렉트 또는 작성 폼 뷰 이름
     */
    @PostMapping("/new")
    public String createPost(@RequestParam("id") String boardCode,
                             @ModelAttribute("createPostDto") @Valid CreatePostDto createPostDto,
                             BindingResult bindingResult,
                             HttpSession session) {
        if (bindingResult.hasErrors()) {
            log.warn("Post creation failed for board code: {} due to validation errors", boardCode);
            return "board/createPost";
        }

        // 세션에서 로그인한 회원 정보를 가져온다.
        Member author = (Member) session.getAttribute("loggedInMember");
        if (author == null) {
            // 로그인하지 않은 상태라면 로그인 페이지로 리다이렉트
            log.warn("Member not logged in");
            return "redirect:/members/login";
        }

        // BoardService를 통해 boardCode에 해당하는 Board 객체를 조회한다.
        Board board = boardService.getBoardByCode(boardCode);

        // CreatePostDto를 Post엔티티로 변환하면서, author와 board를 설정한다.
        Post post = Post.builder()
                .title(createPostDto.getTitle())
                .content(createPostDto.getContent())
                .author(author)
                .board(board)
                .build();

        postService.registerPost(post);
        log.info("Created post with title: {} for board code: {}", post.getTitle(), boardCode);
        return "redirect:/board/lists?id=" + boardCode;
    }

    /**
     * 게시글 수정 폼을 표시하는 "board/edit" 뷰를 반환한다.
     * URL 예시: /board/edit/?id=male&no=6388256
     *
     * @param boardCode 쿼리 파라미터 'id'에 해당하는 게시판 코드
     * @param postId 쿼리 파라미터 'no'에 해당하는 게시글 번호
     * @param model Thymeleaf 모델 객체
     * @return 게시글 수정 폼 뷰 이름
     */
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") String boardCode,
                               @RequestParam("no") Long postId,
                               Model model) {
        Post post = postService.getPostById(postId);
        UpdatePostDto updatePostDto = new UpdatePostDto();
        updatePostDto.setTitle(post.getTitle());
        updatePostDto.setContent(post.getContent());
        model.addAttribute("postId", postId);
        model.addAttribute("updatePostDto", updatePostDto);
        model.addAttribute("boardCode", boardCode);

        // Thymeleaf layout의 head 영역에 전달할 동적 변수들 추가
        model.addAttribute("pageTitle", post.getTitle() + "-" + post.getBoard().getName());
        model.addAttribute("pageDescription", "게시글 수정 페이지입니다.");

        log.info("Displaying edit form for post id: {} on board code: {}", postId, boardCode);
        return "board/edit";
    }

    /**
     * 게시글 수정 요청을 처리한다.
     * 유효성 검증 실패 시 수정 폼으로 되돌아간다.
     * URL 예시: /board/edit/?id=male&no=6388256
     *
     * @param boardCode 쿼리 파라미터 'id'에 해당하는 게시판 코드
     * @param postId 쿼리 파라미터 'no'에 해당하는 게시글 번호
     * @param updatePostDto 수정할 게시글 데이터 (제목과 본문)
     * @param bindingResult 유효성 검증 결과
     * @return 게시글 상세 페이지로 리다이렉트 또는 수정 폼 뷰 이름
     */
    @PostMapping("/edit")
    public String updatePost(@RequestParam("id") String boardCode,
                             @RequestParam("no") Long postId,
                             @ModelAttribute("updatePostDto") @Valid UpdatePostDto updatePostDto,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Post update failed for post id: {} on board code: {} due to validation errors", postId, boardCode);
            return "board/edit";
        }
        postService.updatePost(postId, updatePostDto);
        log.info("Updated post with id: {} on board code: {}", postId, boardCode);
        return "redirect:/board/view?id=" + boardCode + "&no=" + postId;
    }

    /**
     * 게시글 삭제 요청을 처리하고, 해당 게시판의 게시글 목록 페이지로 리다이렉트한다.
     * URL 예시: /board/delete/?id=male&no=6388256
     *
     * @param boardCode 쿼리 파라미터 'id'에 해당하는 게시판 코드
     * @param postId 쿼리 파라미터 'no'에 해당하는 게시글 번호
     * @return 게시글 목록 페이지 리다이렉트 URL
     */
    @PostMapping("/delete")
    public String deletePost(@RequestParam("id") String boardCode,
                             @RequestParam("no") Long postId) {
        postService.deletePostById(postId);
        log.info("Deleted post with id: {} on board code: {}", postId, boardCode);
        return "redirect:/board/lists?id=" + boardCode;
    }

    /**
     * 게시글 추천 요청을 처리한다.
     * 각 회원은 한 게시글에 대해 한 번만 추천할 수 있다.
     * <p>
     *     실제 환경에서는 현재 로그인한 회원 정보를 사용하여 처리한다.
     *     이 예시에서는 HTTP 세션에 저장된 "loggedInMember"를 사용한다.
     * </p>
     * URL 예시: /board/recommend/?id=male&no=6388256
     *
     * @param boardCode 쿼리 파라미터 'id'에 해당하는 게시판 코드
     * @param postId 쿼리 파라미터 'no'에 해당하는 게시글 번호
     * @param redirectAttributes 리다이렉트 시 플래시 속성을 전달하기 위한 객체
     * @param session 현재 HTTP 세션 (로그인한 Member 객체가 "loggedInMember"로 저장됨)
     * @return 게시글 상세 페이지로 리다이렉트하는 URL
     */
    @PostMapping("/recommend")
    public String recommendPost(@RequestParam("id") String boardCode,
                                @RequestParam("no") Long postId,
                                RedirectAttributes redirectAttributes,
                                HttpSession session) {

        // 세션에서 로그인한 Member 객체를 가져옴
        Long memberId = ((Member) session.getAttribute("loggedInMember")).getId();

        try {
            postService.recommendPost(postId, memberId);
        } catch (AlreadyRecommendedException ex) {
            // 이미 추천한 경우 경고 메시지를 플래시 속성으로 전달하고 원래 게시글 조회 페이지로 리다이렉트
            redirectAttributes.addFlashAttribute("warningMessage", ex.getMessage());
            return "redirect:/board/view?id=" + boardCode + "&no=" + postId;
        }
        log.info("Post id: {} on board code: {} recommended by member id: {}", postId, boardCode, memberId);
        return "redirect:/board/view?id=" + boardCode + "&no=" + postId;
    }

}
