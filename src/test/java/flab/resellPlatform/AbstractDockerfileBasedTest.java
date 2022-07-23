package flab.resellPlatform;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractDockerfileBasedTest {
    /*
     * @Testcontainers 어노테이션은 @Container가 붙은 타입을 찾아냄. 해당 필드 즉 컨테이너는 관리 대상이됨.
     * 최초 테스트가 시작하기 전 관리되고 있는 컨테이너들은 start()가 호출됨. 마지막 테스트가 끝나면 stop()이 호출됨.
     * 출처 : https://www.testcontainers.org/test_framework_integration/junit_5/#extension
     */
    @Container
    static GenericContainer MYSQL_CONTAINER;
    @Container
    static final GenericContainer REDIS_CACHE_CONTAINER;
    @Container
    static final GenericContainer REDIS_SESSION_CONTAINER;

    static final int MYSQL_DEFAULT_PORT = 3306;
    static final int REDIS_DEFAULT_PORT = 6379;

    static final String CACHE_PATH = "testcontainers/cache/";
    static final String DB_PATH = "testcontainers/database/";
    static final String SESSION_PATH = "testcontainers/session/";

    static {
        REDIS_CACHE_CONTAINER = new GenericContainer(
                new ImageFromDockerfile()
                        .withFileFromClasspath("Dockerfile", CACHE_PATH + "Dockerfile"))
                .withReuse(true)
                /*
                * 기능 : 도커 컨테이너가 localhost:randomPort와 매핑될 포트를 선정
                *
                * 도커 컨테이너가 올라가서 외부에 Exposed 될 포트를 선정해야함.
                * 이를 선정하지 않으면 Testcontainer가 어떤 포트로 localhost:randomPort와 매핑시킬지 알 수가 없음.
                * 그 결과 해당 도커 컨테이너는 외부와 고립된채로 띄워짐. (withExposedPort를 사용하지 않으면 도커 컨테이너가 외부와 고립된다는 뜻과 동일)
                *
                * docker-compose는 작성된 도커 컨테이너들을 하나의 네트워크로 자동으로 묶어주지만, 현재는 docker-compose를 사용하지 않기 때문에
                * 도커 컨테이너를 어디서든 접근할 수 있게 만들어야함. 그래야 spring이 접근할 수 있기 때문. localhost:randomPort와 서버의 기본 포트를 묶어주면
                * 해당 도커 컨테이너는 localhost:randomPort로 접근할 수 있고, 이에 spring 또한 접근할 수 있게됨.
                 */
                .withExposedPorts(REDIS_DEFAULT_PORT);

        REDIS_SESSION_CONTAINER = new GenericContainer(
                new ImageFromDockerfile()
                        .withFileFromClasspath("Dockerfile", SESSION_PATH + "Dockerfile"))
                .withReuse(true)
                .withExposedPorts(REDIS_DEFAULT_PORT);

        MYSQL_CONTAINER = new GenericContainer(
                new ImageFromDockerfile()
                        .withFileFromClasspath("Dockerfile", DB_PATH + "Dockerfile")
                        .withFileFromClasspath("scripts", DB_PATH + "scripts"))
                .withEnv("MYSQL_ROOT_PASSWORD", "root")
                .withEnv("MYSQL_USER", "resell_platform")
                .withEnv("MYSQL_PASSWORD", "resell1234")
                .withReuse(true)
                .withExposedPorts(MYSQL_DEFAULT_PORT);
    }

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        /*
        * Q. How DynamicPropertySource works?
        *
        * -
         */
        registry.add("spring.session.redis.master.host", REDIS_SESSION_CONTAINER::getHost);
        registry.add("spring.session.redis.master.port", () -> "" + REDIS_SESSION_CONTAINER.getMappedPort(REDIS_DEFAULT_PORT));

        registry.add("spring.cache.redis.master.host", REDIS_CACHE_CONTAINER::getHost);
        registry.add("spring.cache.redis.master.port", () -> "" + REDIS_CACHE_CONTAINER.getMappedPort(REDIS_DEFAULT_PORT));

        registry.add("spring.datasource.url", () -> "jdbc:mysql://" + MYSQL_CONTAINER.getHost() +":" + MYSQL_CONTAINER.getMappedPort(3306) + "/resell_platform_v1");
    }

}
