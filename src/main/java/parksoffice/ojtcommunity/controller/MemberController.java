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
import parksoffice.ojtcommunity.domain.member.Member;
import parksoffice.ojtcommunity.dto.member.LoginRequestDto;
import parksoffice.ojtcommunity.exception.DuplicateMemberException;
import parksoffice.ojtcommunity.service.MemberService;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원 가입 폼을 표시하는 뷰("members/create")를 반환한다.
     *
     * @param model Thymeleaf 모델 객체
     * @return 회원 가입 폼 뷰 이름
     */
    @GetMapping("/new")
    public String showRegistrationForm(Model model) {
        // 노-인자 생성자가 protected이므로, 빌더를 사용하여 새로운 Member 인스턴스를 생성한다.
        Member member = Member.builder().build();
        model.addAttribute("member", member);

        // Thymeleaf layout의 head 영역에 전달할 동적 변수들 추가
        model.addAttribute("pageTitle", "회원가입 페이지");
        model.addAttribute("pageDescription", "회원가입 페이지입니다.");

        log.info("Displaying member registration form");
        return "members/create";
    }

    /**
     * 회원 가입 요청을 처리한다.
     * 유효성 검증에 실패하면 가입 폼으로 되돌아간다.
     * 회원가입 성공 후 메인 페이지("/")로 리다이렉트한다.
     *
     * @param member 등록할 회원 엔티티 (폼 데이터 바인딩)
     * @param bindingResult 유효성 검증 결과
     * @return 로그인 페이지로 리다이렉트 또는 가입 폼 뷰 이름
     */
    @PostMapping("/new")
    public String registerMember(@ModelAttribute("member") @Valid Member member,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            log.warn("Member registration failed: {}", bindingResult.getAllErrors());
            return "members/create";
        }
        try {
            memberService.registerMember(member);
        } catch (DuplicateMemberException e) {
            // 이미 가입된 username인 경우, 경고 메시지를 플래시 속성으로 전달하고 회원가입 페이지로 리다이렉트
            redirectAttributes.addFlashAttribute("warningMessage", e.getMessage());
            return "redirect:/members/new";
        }
        log.info("Registered new member with username: {}", member.getUsername());
        // 회원가입 성공 후 로그인 페이지("/members/login")로 리다이렉트
        return "redirect:/members/login";
    }

    /**
     * 로그인 폼을 표시하는 뷰("members/login")를 반환한다.
     *
     * @param model Thymeleaf 모델 객체
     * @return 로그인 폼 뷰 이름
     */
    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("loginRequestDto", new LoginRequestDto());

        // Thymeleaf layout의 head 영역에 전달할 동적 변수들 추가
        model.addAttribute("pageTitle", "로그인 페이지");
        model.addAttribute("pageDescription", "로그인 페이지 입니다.");

        log.info("Displaying login form");
        return "members/login";
    }

    /**
     * 로그인 요청을 처리한다.
     * 유효성 검증 실패 시 로그인 폼으로 되돌아가며, 로그인 성공 시 세션에 로그인한 회원 정보를 저장하고 메인 페이지("/")로 리다이렉트 한다.
     *
     * @param loginRequestDto (username, password)
     * @param bindingResult   유효성 검증 결과
     * @param session         HTTP 세션
     * @return 로그인 성공 시 메인 페이지 리다이렉트, 실패 시 로그인 폼 뷰로 이동
     */
    @PostMapping("/login")
    public String login(@ModelAttribute("loginRequestDto") @Valid LoginRequestDto loginRequestDto,
                        BindingResult bindingResult,
                        HttpSession session) {
        if (bindingResult.hasErrors()) {
            log.warn("Login validation errors: {}", bindingResult.getAllErrors());
            return "members/login";
        }
        try {
            Member member = memberService.login(loginRequestDto.getUsername(), loginRequestDto.getPassword());
            session.setAttribute("loggedInMember", member);
            log.info("Member logged in: {}", member.getUsername());
            return "redirect:/";
        } catch (Exception e) {
            bindingResult.reject("loginError", "사용자 이름 또는 비밀번호를 확인해주세요."); // 특정 에러 코드를 추가하여 폼 검증 오류를 발생시키는 역할
            log.warn("Login failed for username: {}", loginRequestDto.getUsername());
            return "members/login";
        }
    }

    /**
     * 로그아웃 요청을 처리한다.
     * 세션을 무효화시키고, 메인 페이지("/")로 리다이렉트 한다.
     *
     * @param session HTTP 세션
     * @return 메인 페이지로 리다이렉트
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        log.info("User logged out successfully");
        return "redirect:/";
    }

    /**
     * 특정 회원의 상세 정보를 조회하여 "members/view" 뷰를 반환한다.
     *
     * @param id 조회할 회원의 식별자
     * @param model Thymeleaf 모델 객체
     * @return 회원 상세 페이지 뷰 이름
     */
    @GetMapping("/view/{id}")
    public String viewMember(@PathVariable Long id, Model model) {
        Member member = memberService.getMemberById(id);
        model.addAttribute("member", member);

        // Thymeleaf layout의 head 영역에 전달할 동적 변수들 추가
        model.addAttribute("pageTitle", member.getUsername() + "의 정보");
        model.addAttribute("pageDescription", "특정 회원의 정보를 조회하는 페이지입니다.");

        log.info("Viewing member with id: {}", id);
        return "members/view";
    }
}
