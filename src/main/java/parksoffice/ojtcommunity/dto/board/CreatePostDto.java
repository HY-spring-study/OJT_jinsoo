package parksoffice.ojtcommunity.dto.board;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 게시글 작성 요청에 사용되는 DTO
 * <p>
 *     사용자가 입력한 제목과 내용을 전달받는다
 * </p>
 */
@Getter
@Setter
public class CreatePostDto {
    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;
}
