package parksoffice.ojtcommunity.dto.member;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원 정보 업데이트에 사용되는 DTO.
 * <p>
 * 클라이언트로부터 username과 password만 전달받으며,
 * 이 외의 필드는 업데이트하지 않는다.
 * </p>
 */
@Getter
@Setter
public class UpdateMemberDto {

    @NotBlank(message = "사용자 이름은 필수입니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 4, message = "비밀번호는 최소 4자리 이상이어야 합니다.")
    private String password;
}
