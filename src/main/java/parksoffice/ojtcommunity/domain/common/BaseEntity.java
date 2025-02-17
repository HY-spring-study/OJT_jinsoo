package parksoffice.ojtcommunity.domain.common;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * 공통 엔티티 클래스
 * <p>
 * 모든 엔티티에서 공통적으로 사용하는 기본 필드(id, createdAt, updatedAt)를 제공하는 추상 클래스.
 * JPA의 {@code @MappedSuperclass}를 사용하여 테이블로 생성되지 않고, 상속받는 엔티티에서 해당 필드를 상속받아 사용한다.
 * </p>
 *
 * @author CRISPYTYPER
 */
@MappedSuperclass // 이 클래스를 직접 테이블로 만들지 않고, 자식 엔티티가 필드만 상속받도록 함
@Getter
public abstract class BaseEntity { // 공통 필드를 가진 부모 클래스. abstract class로, 독립적으로 객체 생성 불가능

    /**
     * 기본 키(PK)
     * <p>자동 증가 전략(default: AUTO)을 사용하여 ID를 생성함.</p>
     */
    @Id
    @GeneratedValue //아직 DB를 설정하지 않아서 default인 AUTO로 둠
    private Long id; // 모든 엔티티에서 기본적으로 필요한 PK

    /**
     * 생성일 (INSERT 시 자동 설정)
     * <p>엔티티가 처음 저장될 때 현재 시간을 자동으로 설정함.</p>
     */
    private LocalDateTime createdAt; // 생성일

    /**
     * 수정일 (UPDATE 시 자동 설정)
     * <p>엔티티가 수정될 때 현재 시간을 자동으로 업데이트함.</p>
     */
    private LocalDateTime updatedAt; // 수정일

    /**
     * 엔티티가 처음 저장되기 전에 실행되는 메서드
     * <p>createdAt 값을 현재 시간으로 자동 설정함.</p>
     */
    @PrePersist // 엔티티 INSERT 전에 실행됨
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 엔티티가 수정되기 전에 실행되는 메서드
     * <p>updatedAt 값을 현재 시간으로 자동 설정함.</p>
     */
    @PreUpdate // 엔티티 UPDATE 전에 실행됨
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
