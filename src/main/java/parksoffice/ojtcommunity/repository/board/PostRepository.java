package parksoffice.ojtcommunity.repository.board;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import parksoffice.ojtcommunity.domain.board.Post;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PostRepository 인터페이스
 *
 * <p>
 *     Post 엔티티에 대한 데이터 접근 기능을 제공한다.
 *     Spring Data JPA가 런타임에 자동으로 구현체를 생성한다.
 * </p>
 *
 * <p>
 *     제네릭 타입 매개변수:<br>
 *     - Post: 관리할 엔티티 타입<br>
 *     - Long: Post 엔티티의 기본 키 타입<br>
 * </p>
 *
 * @author CRISPYTYPER
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 제목에 특정 키워드가 포함된 게시글 목록을 반환한다.
     *
     * @param keyword 검색 키워드
     * @return 키워드를 포함하는 게시글 목록
     */
    List<Post> findByTitleContaining(String keyword);

    /**
     * 게시판 코드(board.code)가 정확하게 일치하는 게시글 목록을 반환한다.
     *
     * @param code 게시판 코드 (예: "male", "female")
     * @return 해당 게시판 코드에 속한 게시글 목록
     */
    List<Post> findByBoard_Code(String code);

    /**
     * 본문에 특정 키워드가 포함된 게시글 목록을 반환한다.
     *
     * @param keyword 검색 키워드
     * @return 키워드를 포함하는 게시글 목록
     */
    List<Post> findByContentContaining(String keyword);

    /**
     * 작성자 이름으로 해당되는 게시글 목록을 반환한다.(부분 일치)
     * (Member 엔티티의 username 필드를 기준으로 부분 일치 검색)
     *
     * @param keyword 검색 키워드
     * @return "키워드를 포함하는 작성자 이름"을 가진 게시글 목록
     */
    List<Post> findByAuthor_UsernameContaining(String keyword);

    /**
     * 생성일(createdAt) 기준 내림차순으로 정렬된 모든 게시글 목록을 반환한다.
     *
     * @return 최신 게시글부터 정렬된 게시글 목록
     */
    List<Post> findAllByOrderByCreatedAtDesc();

    /**
     * 생성일(createdAt) 기준 오름차순으로 정렬된 모든 게시글 목록을 반환한다.
     *
     * @return 오래된 게시글부터 정렬된 게시글 목록
     */
    List<Post> findAllByOrderByCreatedAtAsc();

    /**
     * 조회수가 높은 게시글부터 순서대로 게시글 목록을 반환한다.
     *
     * @return 조회수가 많은 게시글부터 정렬된 게시글 목록
     */
    List<Post> findAllByOrderByViewCountDesc();

    /**
     * 추천수가 높은 게시글부터 순서대로 게시글 목록을 반환한다.
     *
     * <p>
     *     추천수는 Post 엔티티의 추천 정보(recommendations) 컬렉션의 크기로 계산된다.
     *     LEFT JOIN과 GROUP BY를 사용하여 추천 수 기준으로 정렬한다.
     * </p>
     *
     * @return 추천수가 많은 게시글부터 정렬된 게시글 목록
     */
    @Query("SELECT p FROM Post p LEFT JOIN p.recommendations r GROUP BY p ORDER BY COUNT(r) DESC")
    List<Post> findAllByOrderByRecommendationCountDesc();

    /**
     * 특정 사용자가 작성한 게시글 목록을 최신순으로 반환한다.
     *
     * @param username 사용자(작성자) 이름
     * @return 해당 사용자가 작성한 게시글(최신 날짜부터 정렬) 목록
     */
    List<Post> findByAuthor_UsernameOrderByCreatedAtDesc(String username);

    /**
     * 특정 기간 사이에 작성된 게시글 목록을 반환한다.
     *
     * @param start 시작 일시 (포함)
     * @param end   종료 일시 (포함)
     * @return 지정한 기간 내에 작성된 게시글 목록
     */
    List<Post> findAllByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

}
