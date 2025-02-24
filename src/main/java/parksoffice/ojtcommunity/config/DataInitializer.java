package parksoffice.ojtcommunity.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import parksoffice.ojtcommunity.domain.board.Board;
import parksoffice.ojtcommunity.repository.board.BoardRepository;

@Configuration
public class DataInitializer {

    /**
     * 애플리케이션 시작 시 Board 데이터를 초기화한다.
     * 등록된 게시판이 없으면 "자기소개(남)"과 "자기소개(여)"을 생성하여 저장한다.
     *
     * @param boardRepository BoardRepository 주입
     * @return CommandLineRunner 빈
     */
    @Bean
    public CommandLineRunner initBoards(BoardRepository boardRepository) {
        return args -> {
            // 데이터베이스에 등록된 게시판이 없으면 초기화 진행
            if (boardRepository.count() == 0) {
                Board maleBoard = Board.builder()
                        .name("자기소개(남)")
                        .description("남자가 본인을 소개하는 게시판")
                        .build();
                Board femaleBoard = Board.builder()
                        .name("자기소개(여)")
                        .description("여자가 본인을 소개하는 게시판")
                        .build();

                boardRepository.save(maleBoard);
                boardRepository.save(femaleBoard);
            }
        };
    }
}
