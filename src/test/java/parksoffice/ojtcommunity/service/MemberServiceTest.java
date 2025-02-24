package parksoffice.ojtcommunity.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import parksoffice.ojtcommunity.domain.member.Member;
import parksoffice.ojtcommunity.dto.member.UpdateMemberDto;
import parksoffice.ojtcommunity.exception.DuplicateMemberException;
import parksoffice.ojtcommunity.exception.MemberNotFoundException;
import parksoffice.ojtcommunity.repository.member.MemberRepository;

import java.util.Arrays;
import java.util.List;
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

        // 중복 회원이 없음을 시뮬레이션하기 위해, findByUsername("testUser") 호출 시 Optional.empty()를 반환하도록 설정한다.
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
    void testRegisterMember_Duplicate() {
        // given
        // 신규 회원 객체를 빌더 패턴을 이용하여 생성한다.(기존에 있는 멤버와 username이 중복됨)
        Member newMember = Member.builder()
                .username("duplicateUser")
                .password("pass123")
                .build();

        // 기존에 repository에 존재하고 있었던 회원 (신규 회원과 동일한 username을 이미 사용중)
        Member existingMember = Member.builder()
                .username("duplicateUser")
                .password("otherPass")
                .build();

        // findByUsername("duplicateUser") 호출 시 existingMember를 Optional로 감싸서 반환하도록 설정한다.
        when(memberRepository.findByUsername("duplicateUser")).thenReturn(Optional.of(existingMember));

        // then
        // memberService.registerMember(newMember)를 호출하면 DuplicateMemberException 예외가 발생해야한다.
        // 해당 예외가 발생하지 않으면 test fail
        assertThrows(DuplicateMemberException.class, () -> memberService.registerMember(newMember));

        // memberRepository.findByUsername("duplicateUser")가 정확히 한 번 호출되었는지 검증한다.
        verify(memberRepository, times(1)).findByUsername("duplicateUser");
        // memberRepository.save(any(Member.class))가 단 한번도 호출되지 않았음을 검증한다.
        // any(Member.class) → save()가 호출되었다면, 어떤 Member 객체가 인자로 들어왔든 상관없이 검증
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    void testGetMemberById_Success() {
        // given
        // ID가 1L인 회원 객체를 빌더 패턴을 이용하여 생성한다.
        Member member = Member.builder()
                .username("testUser")
                .password("testPass")
                .build();

        // findById(1L) 호출 시 member를 Optional로 감싸서 반환하도록 설정한다.
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        // when
        // memberService.getMemberById(1L)를 호출하여 회원 정보를 조회한다.
        Member result = memberService.getMemberById(1L);

        // then
        // 반환된 결과가 null이 아니어야 한다.
        assertNotNull(result);
        // 반환된 회원의 username이 "testUser"인지 검증한다.
        assertEquals("testUser", result.getUsername());
        // memberRepository.findById(1L)가 정확히 한 번 호출되었는지 검증한다.
        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    void testGetMemberById_NotFound() {
        // given
        // ID가 2L인 회원이 존재하지 않는 상황을 가정하고,
        // findById(2L) 호출 시 Optional.empty()를 반환하도록 설정한다.
        when(memberRepository.findById(2L)).thenReturn(Optional.empty());

        // then
        // memberService.getMemberById(2L)를 호출하면 MemberNotFoundException 예외가 발생해야 한다.
        // 해당 예외가 발생하지 않으면 테스트 실패.
        assertThrows(MemberNotFoundException.class, () -> memberService.getMemberById(2L));

        // memberRepository.findById(2L)가 정확히 한 번 호출되었는지 검증한다.
        verify(memberRepository, times(1)).findById(2L);
    }

    @Test
    void testGetMemberByUsername_Success() {
        // given
        // username이 "testUser"인 회원 객체를 빌더 패턴을 이용하여 생성한다.
        Member member = Member.builder()
                .username("testUser")
                .password("testPass")
                .build();

        // findByUsername("testUser") 호출 시 member를 Optional로 감싸서 반환하도록 설정한다.
        when(memberRepository.findByUsername("testUser")).thenReturn(Optional.of(member));

        // when
        // memberService.getMemberByUsername("testUser")를 호출하여 회원 정보를 조회한다.
        Member result = memberService.getMemberByUsername("testUser");

        // then
        // 반환된 결과가 null이 아니어야 한다.
        assertNotNull(result);
        // 반환된 회원의 username이 "testUser"인지 검증한다.
        assertEquals("testUser", result.getUsername());
        // memberRepository.findByUsername("testUser")가 정확히 한 번 호출되었는지 검증한다.
        verify(memberRepository, times(1)).findByUsername("testUser");
    }

    @Test
    void testGetMemberByUsername_NotFound() {
        // given
        // username이 "nonexistent"인 회원이 존재하지 않는 상황을 가정하고,
        // findByUsername("nonexistent") 호출 시 Optional.empty()를 반환하도록 설정한다.
        when(memberRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // then
        // memberService.getMemberByUsername("nonexistent")를 호출하면 MemberNotFoundException 예외가 발생해야 한다.
        // 해당 예외가 발생하지 않으면 테스트 실패.
        assertThrows(MemberNotFoundException.class, () -> memberService.getMemberByUsername("nonexistent"));

        // memberRepository.findByUsername("nonexistent")가 정확히 한 번 호출되었는지 검증한다.
        verify(memberRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testSearchMembersByUsername() {
        // given
        // "test"라는 키워드를 포함하는 두 개의 회원 객체를 생성한다.
        Member member1 = Member.builder().username("testUser1").password("pass1").build();
        Member member2 = Member.builder().username("testUser2").password("pass2").build();

        // 회원 목록을 리스트에 추가한다.
        List<Member> members = Arrays.asList(member1, member2);

        // findByUsernameContaining("test") 호출 시 members 리스트를 반환하도록 설정한다.
        when(memberRepository.findByUsernameContaining("test")).thenReturn(members);

        // when
        // memberService.searchMembersByUsername("test")를 호출하여 키워드를 포함하는 회원 목록을 검색한다.
        List<Member> result = memberService.searchMembersByUsername("test");

        // then
        // 반환된 결과가 null이 아니어야 한다.
        assertNotNull(result);
        // 반환된 회원 목록의 크기가 2개인지 검증한다.
        assertEquals(2, result.size());
        // memberRepository.findByUsernameContaining("test")가 정확히 한 번 호출되었는지 검증한다.
        verify(memberRepository, times(1)).findByUsernameContaining("test");
    }

    @Test
    void testUpdateMember_Success() {
        // given
        // ID가 1L인 기존 회원 객체를 생성하고, findById(1L) 호출 시 해당 회원을 반환하도록 설정한다.
        Member existingMember = Member.builder()
                .username("oldUser")
                .password("oldPass")
                .build();
        when(memberRepository.findById(1L)).thenReturn(Optional.of(existingMember));

        // 회원 정보 변경을 위한 DTO 객체를 생성한다.
        UpdateMemberDto updateMemberDto = new UpdateMemberDto();
        updateMemberDto.setUsername("newUser");
        updateMemberDto.setPassword("newPass");

        // 기존 회원 객체를 저장할 때 동일한 객체를 반환하도록 설정한다.
        when(memberRepository.save(existingMember)).thenReturn(existingMember);

        // when
        // memberService.updateMember(1L, updateMemberDto)를 호출하여 회원 정보를 업데이트한다.
        Member updated = memberService.updateMember(1L, updateMemberDto);

        // then
        // 반환된 결과가 null이 아니어야 한다.
        assertNotNull(updated);
        // 변경된 회원의 username이 "newUser"인지 검증한다.
        assertEquals("newUser", updated.getUsername());
        // 변경된 회원의 password가 "newPass"인지 검증한다.
        assertEquals("newPass", updated.getPassword());
        // memberRepository.findById(1L)가 정확히 한 번 호출되었는지 검증한다.
        verify(memberRepository, times(1)).findById(1L);
        // memberRepository.save(existingMember)가 정확히 한 번 호출되었는지 검증한다.
        verify(memberRepository, times(1)).save(existingMember);
    }

    @Test
    void testUpdateMember_NotFound() {
        // given
        // ID가 1L인 회원이 존재하지 않는 상황을 가정하고,
        // findById(1L) 호출 시 Optional.empty()를 반환하도록 설정한다.
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        // 회원 정보 변경을 위한 DTO 객체를 생성한다.
        UpdateMemberDto updateMemberDto = new UpdateMemberDto();
        updateMemberDto.setUsername("newUser");
        updateMemberDto.setPassword("newPass");

        // then
        // memberService.updateMember(1L, updateMemberDto)를 호출하면 MemberNotFoundException 예외가 발생해야 한다.
        assertThrows(MemberNotFoundException.class, () -> memberService.updateMember(1L, updateMemberDto));

        // memberRepository.findById(1L)가 정확히 한 번 호출되었는지 검증한다.
        verify(memberRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteMemberById_Success() {
        // given
        // ID가 1L인 회원이 존재하는 상황을 가정하고,
        // existsById(1L) 호출 시 true를 반환하도록 설정한다.
        when(memberRepository.existsById(1L)).thenReturn(true);

        // when
        // memberService.deleteMemberById(1L)를 호출하여 해당 회원을 삭제한다.
        memberService.deleteMemberById(1L);

        // then
        // memberRepository.existsById(1L)가 정확히 한 번 호출되었는지 검증한다.
        verify(memberRepository, times(1)).existsById(1L);
        // memberRepository.deleteById(1L)가 정확히 한 번 호출되었는지 검증한다.
        verify(memberRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteMemberById_NotFound() {
        // given
        // ID가 1L인 회원이 존재하지 않는 상황을 가정하고,
        // existsById(1L) 호출 시 false를 반환하도록 설정한다.
        when(memberRepository.existsById(1L)).thenReturn(false);

        // then
        // memberService.deleteMemberById(1L)를 호출하면 MemberNotFoundException 예외가 발생해야 한다.
        assertThrows(MemberNotFoundException.class, () -> memberService.deleteMemberById(1L));

        // memberRepository.existsById(1L)가 정확히 한 번 호출되었는지 검증한다.
        verify(memberRepository, times(1)).existsById(1L);
        // memberRepository.deleteById(anyLong())가 호출되지 않았음을 검증한다.
        verify(memberRepository, never()).deleteById(anyLong());
    }
}