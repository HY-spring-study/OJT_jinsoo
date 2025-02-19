package parksoffice.ojtcommunity.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import parksoffice.ojtcommunity.domain.member.Member;
import parksoffice.ojtcommunity.repository.member.MemberRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito의 Mock 객체 초기화 및 해제를 자동으로 관리합(JUnit 5)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository; // MemberRepository를 목(mock) 객체로 생성한다.

    @InjectMocks
    private MemberService memberService; // MemberRepository 목 객체를 주입받아 MemberService의 인스턴스를 생성한다.

    @Test
    void testRegisterMember_Success() {
        // given: 테스트 시나리오 설정
        // 신규 회원 객체를 빌더 패턴을 이용하여 생성한다.
        Member newMember = Member.builder()
                .username("testUser")
                .password("testPass")
                .build();

        // 중복 회원이 없음을 시뮬레이션하기 위해, findByUser("testUser") 호출 시 Optional.empty()를 반환하도록 설정한다.
        when(memberRepository.findByUsername("testUser")).thenReturn(Optional.empty());
        // save 호출 시, 전달된 회원 객체를 받아 '저장 후' id가 부여되었다고 가정한 새 Member 객체를 반환하도록 설정한다.
        // 테스트 코드에서는 가정만 하지만, 실제로는 JPA가 save()를 호출 후 엔티티에 id를 자동으로 할당한다.
        when(memberRepository.save(newMember)).thenAnswer(invocation -> {
            // save() 호출 시 전달된 인자(Member 객체)를 가져온다.
            Member m = invocation.getArgument(0);
            // 실제 DB에서는 id가 부여되었지만, 테스트에서는 id 설정 없이 username과 password만 복사한 새 객체를 반환한다.
            return Member.builder()
                    .username(m.getUsername())
                    .password(m.getPassword())
                    .build();
        });

        // when: 테스트 대상 메서드 호출
        // registerMember() 메서드를 호출하여, 신규 회원 등록 과정을 실행한다.
        Member result = memberService.registerMember(newMember);

        // then: 예상 결과 검증
        // 결과 객체가 null이 아님을 확인한다.
        assertNotNull(result);
        // 등록된 회원의 username이 "testUser"와 일치하는지 검증한다.
        assertEquals("testUser", result.getUsername());
        // memberRepository.findByUsername("testUser")가 정확히 한 번 호출되었는지 검증한다.
        verify(memberRepository, times(1)).findByUsername("testUser");
        // memberRepository.save(newMember)도 정확히 한 번 호출되었는지 검증한다.
        verify(memberRepository, times(1)).save(newMember);
    }

    @Test
    void getMemberById() {
    }

    @Test
    void getMemberByUsername() {
    }

    @Test
    void searchMembersByUsername() {
    }

    @Test
    void updateMember() {
    }

    @Test
    void deleteMemberById() {
    }
}