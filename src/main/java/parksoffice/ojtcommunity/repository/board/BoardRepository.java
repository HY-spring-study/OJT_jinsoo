package parksoffice.ojtcommunity.repository.board;

import org.springframework.data.jpa.repository.JpaRepository;
import parksoffice.ojtcommunity.domain.board.Board;

import java.util.List;
import java.util.Optional;

/**
 * BoardRepository 인터페이스
 *
 * <p>
 *     Board 엔티티에 대한 데이터 접근 기능을 제공하며,
 *     Spring Data JPA가 런타임에 자동으로 구현체를 생성한다.
 * </p>
 *
 * <p>
 *     제네릭 타입 매개변수:<br>
 *     - Board: 관리할 엔티티 타입<br>
 *     - Long: Board 엔티티의 기본 키 타입<br>
 * </p>
 *
 * @author CRISPYTYPER
 */
public interface BoardRepository extends JpaRepository<Board, Long> {

    /**
     * 게시판 이름(name)이 정확하게 일치하는 Board 엔티티를 반환한다.
     *
     * @param name 검색할 게시판 이름
     * @return 해당 이름을 가진 Board 엔티티 (Optional)
     */
    Optional<Board> findByName(String name);

    /**
     * 게시판 코드(code)가 정확하게 일치하는 Board 엔티티를 반환한다.
     *
     * @param code 검색할 게시판 코드
     * @return 해당 코드를 가진 Board 엔티티 (Optional)
     */
    Optional<Board> findByCode(String code);

    /**
     * 게시판 이름(name)에 특정 키워드가 포함된 Board 엔티티 목록을 반환한다.
     *
     * @param keyword 검색할 키워드
     * @return 키워드를 포함하는 게시판 목록
     */
    List<Board> findByNameContaining(String keyword);

    /**
     * 게시판 설명(description)에 특정 키워드가 포함된 Board 엔티티 목록을 반환한다.
     *
     * @param keyword 검색할 키워드
     * @return 키워드를 포함하는 게시판 설명을 가진 Board 목록
     */
    List<Board> findByDescriptionContaining(String keyword);

    /**
     * 게시판 코드(code)에 특정 키워드가 포함된 Board 엔티티 목록을 반환한다.
     *
     * @param keyword 검색할 키워드
     * @return 키워드를 포함하는 게시판 코드를 가진 Board 목록
     */
    List<Board> findByCodeContaining(String keyword);
}
