package flab.resellPlatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ResellPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResellPlatformApplication.class, args);
	}

}
