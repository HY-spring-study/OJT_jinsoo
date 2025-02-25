package parksoffice.ojtcommunity.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import parksoffice.ojtcommunity.domain.member.Member;
import parksoffice.ojtcommunity.service.MemberService;

import java.util.List;

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
     * @return 메인 페이지로 리다이렉트 또는 가입 폼 뷰 이름
     */
    @PostMapping("/new")
    public String registerMember(@ModelAttribute("member") @Valid Member member,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            log.warn("Member registration failed: {}", bindingResult.getAllErrors());
            return "members/create";
        }
        memberService.registerMember(member);
        log.info("Registered new member with username: {}", member.getUsername());
        // 회원가입 성공 후 메인 페이지("/")로 리다이렉트
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
        log.info("Viewing member with id: {}", id);
        return "members/view";
    }
}
