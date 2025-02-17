package parksoffice.ojtcommunity.domain.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import parksoffice.ojtcommunity.domain.common.BaseEntity;

/**
 * 회원(Member) 엔티티
 *
 * <p>사용자의 계정 정보를 저장하는 엔티티로, 사용자 이름과 비밀번호를 포함한다.</p>
 * <p>기본 키(id), 생성일(createdAt), 수정일(updatedAt) 필드는 {@link BaseEntity} 에서 상속받는다.</p>
 *
 * @author CRISPYTYPER
 * @see BaseEntity
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "members")
public class Member extends BaseEntity {

    /**
     * 사용자 이름
     * <p>중복될 수 없으며, 반드시 입력해야 한다.</p>
     */
    @NotBlank(message = "사용자 이름은 필수입니다.")
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * 사용자 비밀번호.
     * <p>비밀번호는 반드시 입력해야 하며, 보안을 위해 암호화하여 저장하는 것이 권장된다.</p>
     */
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Column(nullable = false)
    private String password;


    // 이후 상황에 따라 이메일, 프로필 이미지, 권한(Role) 등 필드 추가 가능.
}

