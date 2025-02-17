package parksoffice.ojtcommunity.domain.board;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import parksoffice.ojtcommunity.domain.common.BaseEntity;
import parksoffice.ojtcommunity.domain.member.Member;

/**
 * 게시글(Post) 엔티티 클래스
 *
 * <p>게시판의 개별 게시글을 나타내며, 제목, 내용, 작성자, 조회수, 추천수 필드를 포함한다.</p>
 * <p>또한, {@link BaseEntity} 를 상속받아 기본 키(id), 생성일(createdAt), 수정일(updatedAt)필드를 자동으로 상속받는다.</p>
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
@Table(name = "posts")
public class Post extends BaseEntity { // 게시글 엔티티

    /**
     * 게시글 제목
     * <p>필수 입력 필드</p>
     */
    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    /**
     * 게시글 본문
     * <p>필수 입력 필드, 긴 텍스트를 저장하기 위해 {@code @Lob} 어노테이션을 사용한다.</p>
     */
    @NotBlank(message = "내용은 필수입니다.")
    @Lob // JPA에서 큰 데이터(텍스트 또는 바이너리 데이터)를 저장할 때 사용하는 어노테이션
    private String content;

    /**
     * 게시글 작성자
     * <p>작성자는 회원(Member) 엔티티와 다대일(N:1) 관계를 가진다.</p>
     */
    @ManyToOne(fetch = FetchType.LAZY) // Member 데이터를 필요할 때만 조회하여 성능 최적화
    @JoinColumn(name = "member_id", nullable = false)
    private Member author;

    /**
     * 게시글 조회수
     * <p>기본값은 0이며, 사용자가 게시글을 조회할 때마다 증가한다.</p>
     */
    @Builder.Default // Lombok의 @Builder와 함께 사용할 때, 기본값이 의도대로 설정되도록 하기 위해 필요
    @Column(nullable = false)
    private int viewCount = 0;

    /**
     * 게시글 추천수
     * <p>기본값은 0이며, 사용자가 게시글을 추천할 때마다 증가한다.</p>
     */
    @Builder.Default
    @Column(nullable = false)
    private int recommendationCount = 0;
}
