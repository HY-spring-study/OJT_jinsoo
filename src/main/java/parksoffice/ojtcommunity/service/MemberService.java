package parksoffice.ojtcommunity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import parksoffice.ojtcommunity.domain.member.Member;
import parksoffice.ojtcommunity.dto.member.UpdateMemberDto;
import parksoffice.ojtcommunity.exception.DuplicateMemberException;
import parksoffice.ojtcommunity.exception.MemberNotFoundException;
import parksoffice.ojtcommunity.exception.PasswordNotCorrectException;
import parksoffice.ojtcommunity.repository.member.MemberRepository;

import java.util.List;
import java.util.Optional;

/**
 * MemberService 클래스
 * <p>
 * 회원(Member) 관련 비즈니스 로직을 처리한다.
 * </p>
 *
 * <p>
 * 주요 기능은 다음과 같다:
 * <ul>
 *   <li>신규 회원 등록 (& 중복 회원 검증)</li>
 *   <li>회원 ID, 회원 이름에 따른 단건 조회 (존재하지 않을 경우 예외 발생)</li>
 *   <li>회원 이름 검색: 키워드에 포함된 회원 목록 반환 (검색 결과가 없을 경우 빈 리스트 반환)</li>
 *   <li>회원 정보 업데이트: 업데이트용 DTO를 통해 필요한 필드만 갱신</li>
 *   <li>회원 삭제: 삭제 전에 회원 존재 여부 확인</li>
 * </ul>
 * </p>
 *
 * @author CRISPYTYPER
 */
@Service
@Transactional // 기본적으로 쓰기 작업에 대해 트랜잭션을 적용함.
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 신규 회원 등록
     * (실제 서비스에서는 비밀번호 암호화 등 추가 로직 적용)
     *
     * @param member 등록할 회원 엔티티
     * @return 저장된 회원 엔티티
     */
    public Member registerMember(Member member) {
        validateDuplicateMember(member); // 중복 회원 검증

        return memberRepository.save(member);
    }

    /**
     * 회원 ID로 조회 (읽기 전용)
     * 회원이 존재하지 않으면 MemberNotFoundException을 발생시킨다.
     *
     * @param id 회원 식별자
     * @return 조회된 회원 엔티티
     * @throws MemberNotFoundException 회원을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public Member getMemberById(Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + id));
    }

    /**
     * 회원 이름으로 회원 조회 (읽기 전용)
     * 회원이 존재하지 않으면 MemberNotFoundException을 발생시킨다.
     *
     * @param username 조회할 회원 이름
     * @return 조회된 회원 엔티티
     * @throws MemberNotFoundException 회원을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public Member getMemberByUsername(String username) {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with username: " + username));
    }

    /**
     * 사용자 이름에 특정 키워드가 포함된 회원 목록 조회 (읽기 전용)
     *
     * @param keyword 검색할 키워드
     * @return 키워드를 포함하는 회원 목록.(회원이 없을 경우 빈 리스트 출력)
     */
    @Transactional(readOnly = true)
    public List<Member> searchMembersByUsername(String keyword) {

        return memberRepository.findByUsernameContaining(keyword);
    }

    /**
     * 회원 정보 업데이트.
     * 회원이 존재하지 않으면 MemberNotFoundException을 발생시킨다.
     *
     * @param id              업데이트할 회원의 식별자
     * @param updateMemberDto 회원 정보 업데이트용 DTO (username, password)
     * @return 업데이트된 회원 엔티티
     * @throws MemberNotFoundException 해당 회원이 없을 경우
     */
    public Member updateMember(Long id, UpdateMemberDto updateMemberDto) {
        Member existingMember = memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + id));

        // 필요한 필드 업데이트 (여기서는 username과 password만 갱신)
        existingMember.setUsername(updateMemberDto.getUsername());
        existingMember.setPassword(updateMemberDto.getPassword());

        return memberRepository.save(existingMember);
    }

    /**
     * 회원 삭제
     * 삭제 전 회원 존재 여부를 확인하여, 없으면 MemberNotFoundException을 발생시킨다.
     *
     * @param id 삭제할 회원의 식별자
     * @throws MemberNotFoundException 해당 회원이 없을 경우
     */
    public void deleteMemberById(Long id) {
        if (!memberRepository.existsById(id)) {
            throw new MemberNotFoundException("Member not found with id: " + id);
        }
        memberRepository.deleteById(id);
    }

    /**
     * 중복 회원 검증
     *
     * <p>
     * 주어진 회원 객체의 username으로 회원을 검색하여,
     * 해당 username을 가진 회원이 존재하면 DuplicateMemberException을 발생시킨다.
     * </p>
     *
     * @param member 중복 검증을 수행할 회원 엔티티
     * @throws DuplicateMemberException 이미 해당 username을 가진 회원이 존재할 경우
     */
    private void validateDuplicateMember(Member member) {
        Optional<Member> foundMember = memberRepository.findByUsername(member.getUsername());
        if (foundMember.isPresent()) {
            throw new DuplicateMemberException("Already existing member with username: " + member.getUsername());
        }
    }

    /**
     * 사용자 로그인
     *
     * <p>
     * 주어진 username으로 회원을 조회한 후, 입력된 password와 비교하여 로그인을 수행한다.
     * </p>
     *
     * @param username 로그인할 사용자 이름
     * @param password 로그인할 사용자 비밀번호
     * @return 로그인된 회원 엔티티
     * @throws MemberNotFoundException 사용자 이름이 존재하지 않을 경우
     * @throws PasswordNotCorrectException 비밀번호가 일치하지 않을 경우
     */
    @Transactional(readOnly = true)
    public Member login(String username, String password) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with username: " + username));
        if (!member.getPassword().equals(password)) {
            throw new PasswordNotCorrectException("Password not correct");
        }
        return member;
    }
}
