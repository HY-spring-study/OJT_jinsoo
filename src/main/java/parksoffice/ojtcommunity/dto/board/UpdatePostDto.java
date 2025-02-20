package parksoffice.ojtcommunity.dto.board;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 게시글 업데이트에 사용되는 DTO
 * <p>
 * 클라이언트로부터 제목과 본문만 전달받아 업데이트 한다.
 * (작성자, 조회수, 추천수 등은 업데이트 대상이 아니다.)
 * </p>
 */
@Getter
@Setter
public class UpdatePostDto {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;
}
