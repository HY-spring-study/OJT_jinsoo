package parksoffice.ojtcommunity.exception;

/**
 * 이미 추천한 게시글을 동일한 회원이 또 추천하려고 할 때 발생하는 예외이다.
 */
public class AlreadyRecommendedException extends RuntimeException {
    public AlreadyRecommendedException(String message) {
        super(message);
    }
}
