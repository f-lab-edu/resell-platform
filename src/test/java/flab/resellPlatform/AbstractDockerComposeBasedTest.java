package flab.resellPlatform;

import org.junit.ClassRule;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;
import java.time.Duration;

@Testcontainers
public abstract class AbstractDockerComposeBasedTest {
    /*
     * @Testcontainers 어노테이션은 @Container가 붙은 타입을 찾아냄. 해당 필드 즉 컨테이너는 관리 대상이됨.
     * 최초 테스트가 시작하기 전 관리되고 있는 컨테이너들은 start()가 호출됨. 마지막 테스트가 끝나면 stop()이 호출됨.
     * 출처 : https://www.testcontainers.org/test_framework_integration/junit_5/#extension
     */

    static final int MYSQL_DEFAULT_PORT = 3306;
    static final int REDIS_DEFAULT_PORT = 6379;

    @Container
    public static DockerComposeContainer dockerComposeContainer =
            new DockerComposeContainer(new File("docker-compose-test.yml"))
                    .withExposedService("redissession", REDIS_DEFAULT_PORT,
                            Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
                    .withExposedService("rediscache", REDIS_DEFAULT_PORT,
                            Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)))
                    .withExposedService("database", MYSQL_DEFAULT_PORT,
                            Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30)));


    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        /*
         * Q. How DynamicPropertySource works?
         *
         * -
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
                        "/resell_platform_v1");
    }

}
