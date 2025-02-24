package parksoffice.ojtcommunity.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * HomeController
 *
 * <p>
 *     루트 URL("/")에 대한 요청을 처리하며, 간단한 텍스트 응답과 로그 메시지를 출력하여 매핑이 올바르게 작동하는지 확인한다.
 * </p>
 *
 * @author CRISPYTYPER
 */
@RestController
@Slf4j
public class HomeController {
    @GetMapping("/")
    public String home() {
        log.info("HomeController: '/' endpoint has been accessed.");
        return "This is the home page.";
    }
}
