package parksoffice.ojtcommunity.exception;

/**
 * 이미 해당 회원이 존재할 때 발생하는 예외이다.
 */
public class DuplicateMemberException extends RuntimeException {
    public DuplicateMemberException(String message) {
        super(message);
    }
}
