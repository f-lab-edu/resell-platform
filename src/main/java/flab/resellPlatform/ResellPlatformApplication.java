package flab.resellPlatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import(ApplicationConfig.class)
@SpringBootApplication
public class ResellPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResellPlatformApplication.class, args);
	}

}
