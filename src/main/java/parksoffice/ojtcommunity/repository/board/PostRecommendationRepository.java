package parksoffice.ojtcommunity.repository.board;

import org.springframework.data.jpa.repository.JpaRepository;
import parksoffice.ojtcommunity.domain.board.PostRecommendation;

/**
 * PostRecommendationRepository 인터페이스
 *
 * <p>
 *     게시글 추천(PostRecommendation) 엔티티에 대한 데이터 접근 기능을 제공한다.
 *     각 게시글은 한 유저당 한번까지만 추천할 수 있다.
 *     어떤 유저가 어떤 게시글을 추천했는지에 대한 정보를 접근한다.(postId, memberId) 중복 없음.
 * </p>
 *
 * @see PostRecommendation
 */
public interface PostRecommendationRepository extends JpaRepository<PostRecommendation, Long> {

    /**
     * 주어진 게시글 ID와 회원 ID를 가진 추천 기록이 존재하는지 확인한다.
     *
     * <p>
     *     이 메서드는 한 회원이 동일한 게시글에 대해 중복 추천을 하는지 검사할 때 사용된다.
     *     게시글(postId)와 회원(memberId)의 조합에 대해 유니크 제약 조건이 설정되어 있으므로,
     *     해당 추천 기록이 이미 존재하면 true를 반환하고, 그렇지 않으면 false를 반환한다.
     * </p>
     *
     * @param postId   확인할 게시글의 식별자
     * @param memberId 확인할 회원의 식별자
     * @return 해당 게시글과 회원의 추천 기록이 존재하면 true, 없으면 false
     */
    boolean existsByPostIdAndMemberId(Long postId, Long memberId);
}
