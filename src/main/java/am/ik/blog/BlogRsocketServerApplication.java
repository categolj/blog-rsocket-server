package am.ik.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BlogRsocketServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(BlogRsocketServerApplication.class, args);
	}
}
