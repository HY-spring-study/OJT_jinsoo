package parksoffice.ojtcommunity.domain.board;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import parksoffice.ojtcommunity.domain.common.BaseEntity;

/**
 * 게시판(Board) 엔티티
 *
 * <p>자유게시판, 유머게시판 등 게시판의 기본 정보를 저장한다.</p>
 * <p>이 엔티티는 단방향 관계로 구현되어 있으며, 게시글(Post) 엔티티에서 {@code @ManyToOne}으로 Board를 참조한다.</p>
 * <p>
 *     기본 키(id), 생성일(createdAt), 수정일(updatedAt) 필드는 {@link BaseEntity} 에서 상속받는다.
 *     게시판 이름(name)은 한 번 생성되면 변경되지 않는 불변 필드로 관리된다.
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
@Table(name = "boards")
public class Board extends BaseEntity {

    /**
     * 게시판 이름
     * <p>자유게시판, 유머게시판 등 고유한 이름을 가진다.</p>
     */
    @NotBlank(message = "게시판 이름은 필수입니다.")
    @Column(unique = true, nullable = false)
    private String name;

    /**
     * 게시판 설명
     * <p>게시판의 성격이나 설명을 담는다.</p>
     */
    @Setter
    @Column(length = 500)
    private String description;
}
