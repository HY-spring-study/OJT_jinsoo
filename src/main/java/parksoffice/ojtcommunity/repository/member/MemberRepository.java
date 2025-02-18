package parksoffice.ojtcommunity.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;
import parksoffice.ojtcommunity.domain.member.Member;

import java.util.List;
import java.util.Optional;

/**
 * MemberRepository 인터페이스
 *
 * <p>
 *     Member 엔티티에 대한 데이터 접근 기능을 제공한다.
 *     Spring Data JPA가 런타임에 자동으로 구현체를 생성한다.
 * </p>
 *
 * <p>
 *     제네릭 타입 매개변수:<br>
 *     - Member: 관리할 엔티티 타입<br>
 *     - Long: Member 엔티티의 기본 키 타입<br>
 * </p>
 *
 * @author CRISPYTYPER
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 사용자 이름(username)이 정확히 일치하는 회원 정보를 반환한다.
     *
     * @param username 검색할 사용자 이름
     * @return 해당 사용자 이름을 가진 회원 (Optional)
     */
    Optional<Member> findByUsername(String username);

    /**
     * 사용자 이름(username)에 특정 키워드가 포함된 회원 목록을 반환한다.
     *
     * @param keyword 검색할 키워드
     * @return 키워드를 포함하는 회원 목록
     */
    List<Member> findByUsernameContaining(String keyword);
}
