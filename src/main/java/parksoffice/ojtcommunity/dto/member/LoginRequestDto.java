package parksoffice.ojtcommunity.dto.member;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 요청을 위한 DTO
 * 사용자 이름과 비밀번호를 전달받는다.
 */
@Getter
@Setter
public class LoginRequestDto {

    @NotBlank(message = "사용자 이름은 필수입니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
