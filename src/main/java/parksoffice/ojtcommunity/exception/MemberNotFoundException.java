package parksoffice.ojtcommunity.exception;

/**
 * 회원을 찾지 못했을 때 발생하는 예외입니다.
 */
public class MemberNotFoundException extends RuntimeException {
  public MemberNotFoundException(String message) {
    super(message);
  }
}
