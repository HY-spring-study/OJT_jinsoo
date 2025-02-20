package parksoffice.ojtcommunity.domain.board;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import parksoffice.ojtcommunity.domain.common.BaseEntity;
import parksoffice.ojtcommunity.domain.member.Member;

import java.util.ArrayList;
import java.util.List;

/**
 * 게시글(Post) 엔티티 클래스
 *
 * <p>게시판의 개별 게시글을 나타내며, 제목, 내용, 작성자, 조회수, 추천수 필드를 포함한다.</p>
 * <p>또한, {@link BaseEntity} 를 상속받아 기본 키(id), 생성일(createdAt), 수정일(updatedAt)필드를 자동으로 상속받는다.</p>
 *
 * <p>
 *     업데이트가 가능한 필드(제목, 본문)에는 setter를 제공하며,
 *     작성자(author), 게시판(board), 조회수(viewCount), 추천수(recommendationCount)는 등록 후 외부에서 직접 수정되지 않도록 캡슐화한다.
 *     조회수와 추천수는 전용 도메인 메서드를 통해 변경한다.
 * </p>
 *
 * @author CRISPYTYPER
 * @see BaseEntity
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 엔티티는 반드시 no args 생성자를 가져야 한다. (외부에서 임의로 호출하지 못하도록 함)
@AllArgsConstructor
@SuperBuilder
@Table(name = "posts")
public class Post extends BaseEntity { // 게시글 엔티티

    /**
     * 게시글 제목
     * <p>필수 입력 필드이며, 업데이트가 가능하다.</p>
     */
    @NotBlank(message = "제목은 필수입니다.")
    @Setter
    private String title;

    /**
     * 게시글 본문
     * <p>필수 입력 필드, 긴 텍스트를 저장하기 위해 {@code @Lob} 어노테이션을 사용하며, 업데이트가 가능하다.</p>
     */
    @NotBlank(message = "내용은 필수입니다.")
    @Lob // JPA에서 큰 데이터(텍스트 또는 바이너리 데이터)를 저장할 때 사용하는 어노테이션
    @Setter
    private String content;

    /**
     * 게시글 작성자
     * <p>
     *      작성자는 회원(Member) 엔티티와 다대일(N:1) 관계를 가진다.
     *      등록시에만 설정되고, 이후 변경되면 안된다.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY) // Member 데이터를 필요할 때만 조회하여 성능 최적화
    @JoinColumn(name = "member_id", nullable = false)
    private Member author;

    /**
     * 게시글이 속한 게시판
     * <p>
     *     게시글은 하나의 게시판(Board)에 속한다. (단방향 연관관계)
     *     등록시에만 설정되고, 이후 변경되면 안된다.
     * </p>
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    /**
     * 게시글 조회수
     * <p>
     *     기본값은 0이며, 사용자가 게시글을 조회할 때마다 증가시키는 로직은
     *     {@link #incrementViewCount()} 메서드를 통해 처리하며, 외부에서 직접 수정되면 안 된다.
     * </p>
     */
    @Builder.Default // Lombok의 @Builder와 함께 사용할 때, 기본값이 의도대로 설정되도록 하기 위해 필요
    @Column(nullable = false)
    private int viewCount = 0;

    /**
     * 게시글 추천 정보 리스트
     * <p>
     * 이 컬렉션은 해당 게시글에 대한 추천 정보를 제공하며,
     * cascade 옵션과 orphanRemoval 옵션을 통해 게시글이 삭제될 때 연관된 추천 정보도 함께 삭제된다.
     * 각 회원은 한 게시글에 대해 최대 한 번 추천할 수 있도록 unique 제약 조건은 PostRecommendation에서 관리된다.
     * </p>
     */
    // cascade = CascadeType.ALL
    // Post 엔티티를 저장(persist), 수정(merge), 삭제(remove)할 때, 이와 연관된 모든 PostRecommendation 엔티티에도 동일한 작업을 자동으로 수행한다.
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostRecommendation> recommendations = new ArrayList<>();

    //== 도메인 메서드 ==//

    /**
     * 게시글 조회수를 1 증가시킨다
     */
    public void incrementViewCount() {
        this.viewCount++;
    }

    /**
     * 새로운 추천(PostRecommendation)을 추가한다.
     * <p>
     *     이 메서드는 추천 정보를 외부에서 직접 수정하는 것을 방지하고,
     *     추천 컬렉션에 추천 객체를 추가하는 방식으로 추천수를 관리한다.
     * </p>
     *
     * @param recommendation 추가할 추천 정보
     */
    public void addRecommendation(PostRecommendation recommendation) {
        this.recommendations.add(recommendation);
    }

    /**
     * 현재 게시글의 추천수를 반환한다.
     *
     * @return 추천수 (추천 정보 컬렉션의 크기)
     */
    public int getRecommendationCount() {
        return recommendations.size();
    }
}
