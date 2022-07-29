package flab.integration_test;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.Duration;

@Testcontainers
public abstract class AbstractDockerComposeBasedTest {

    static final int MYSQL_DEFAULT_PORT = 3306;
    static final int REDIS_DEFAULT_PORT = 6379;
    static final String DB_SCHEMA = "v_1";

    static DockerComposeContainer dockerComposeContainer;

    static {
        dockerComposeContainer =
                new DockerComposeContainer(new File("docker-compose-test.yml"))
                        .withExposedService("redissession", REDIS_DEFAULT_PORT,
                                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
                        .withExposedService("rediscache", REDIS_DEFAULT_PORT,
                                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
                        .withExposedService("database", MYSQL_DEFAULT_PORT,
                                Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)));

        dockerComposeContainer.start();
    }


    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        /*
         * Q. How DynamicPropertySource works?
         * A. 업데이트 예정
         */
        registry.add("spring.session.redis.master.host", () -> dockerComposeContainer.getServiceHost("redissession", REDIS_DEFAULT_PORT));
        registry.add("spring.session.redis.master.port", () -> dockerComposeContainer.getServicePort("redissession", REDIS_DEFAULT_PORT));

        registry.add("spring.cache.redis.master.host", () -> dockerComposeContainer.getServiceHost("rediscache", REDIS_DEFAULT_PORT));
        registry.add("spring.cache.redis.master.port", () -> dockerComposeContainer.getServicePort("rediscache", REDIS_DEFAULT_PORT));

        registry.add("spring.datasource.url", () ->
                "jdbc:mysql://" +
                        dockerComposeContainer.getServiceHost("database", MYSQL_DEFAULT_PORT) +
                        ":" +
                        dockerComposeContainer.getServicePort("database", MYSQL_DEFAULT_PORT) +
                        "/" + DB_SCHEMA);
    }
}
