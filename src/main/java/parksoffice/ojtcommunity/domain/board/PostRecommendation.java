package parksoffice.ojtcommunity.domain.board;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import parksoffice.ojtcommunity.domain.common.BaseEntity;
import parksoffice.ojtcommunity.domain.member.Member;

/**
 * 게시글 추천(PostRecommendation) 엔티티
 * <p>
 * 각 회원이 게시글에 추천을 한 기록을 저장하며, (post, member) 조합에 대해 unique 제약 조건을 둔다.
 * 이를 통해 각 회원은 한 게시글에 대해 최대 한 번 추천할 수 있다.
 * </p>
 */
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 엔티티는 반드시 no args 생성자를 가져야 한다. (외부에서 임의로 호출하지 못하도록 함)
@AllArgsConstructor
@SuperBuilder
@Table(name = "post_recommendations", uniqueConstraints = @UniqueConstraint(columnNames = {"post_id", "member_id"}))
public class PostRecommendation extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
