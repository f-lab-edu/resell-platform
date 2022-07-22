package flab.resellPlatform;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

public abstract class AbstractContainerBasedTest {
    static final int REDIS_DEFAULT_PORT = 6379;
    static final GenericContainer REDIS_CACHE_CONTAINER;
    static final GenericContainer REDIS_SESSION_CONTAINER;
    static final String CACHE_PATH = "testcontainers/cache/";
    static final String SESSION_PATH = "testcontainers/session/";

    static final int MYSQL_DEFAULT_PORT = 3306;
    static GenericContainer MYSQL_CONTAINER;
    static final String DB_PATH = "testcontainers/database/";


    static {
        REDIS_CACHE_CONTAINER = new GenericContainer(
                new ImageFromDockerfile()
                        .withFileFromClasspath("Dockerfile", CACHE_PATH + "Dockerfile"))
                .withReuse(true)
                .withExposedPorts(REDIS_DEFAULT_PORT);
        REDIS_CACHE_CONTAINER.start();

        REDIS_SESSION_CONTAINER = new GenericContainer(
                new ImageFromDockerfile()
                        .withFileFromClasspath("Dockerfile", SESSION_PATH + "Dockerfile"))
                .withReuse(true)
                .withExposedPorts(REDIS_DEFAULT_PORT);
        REDIS_SESSION_CONTAINER.start();

        MYSQL_CONTAINER = new GenericContainer(
                new ImageFromDockerfile()
                        .withFileFromClasspath("Dockerfile", DB_PATH + "Dockerfile")
                        .withFileFromClasspath("scripts", DB_PATH + "scripts"))
                .withEnv("MYSQL_ROOT_PASSWORD", "root")
                .withEnv("MYSQL_USER", "resell_platform")
                .withEnv("MYSQL_PASSWORD", "resell1234")
                .withReuse(true)
                .withExposedPorts(MYSQL_DEFAULT_PORT);
        MYSQL_CONTAINER.start();
    }

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.session.redis.master.host", REDIS_SESSION_CONTAINER::getHost);
        registry.add("spring.session.redis.master.port", () -> "" + REDIS_SESSION_CONTAINER.getMappedPort(REDIS_DEFAULT_PORT));

        registry.add("spring.cache.redis.master.host", REDIS_CACHE_CONTAINER::getHost);
        registry.add("spring.cache.redis.master.port", () -> "" + REDIS_CACHE_CONTAINER.getMappedPort(REDIS_DEFAULT_PORT));

        registry.add("spring.datasource.url", () -> "jdbc:mysql://" + MYSQL_CONTAINER.getHost() +":" + MYSQL_CONTAINER.getMappedPort(3306) + "/resell_platform_v1");
    }

}
