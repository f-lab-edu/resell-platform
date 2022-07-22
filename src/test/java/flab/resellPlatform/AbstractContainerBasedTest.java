package flab.resellPlatform;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;

public abstract class AbstractContainerBasedTest {
    static final String REDIS_IMAGE = "redis:6-alpine";
    static final GenericContainer REDIS_CACHE_CONTAINER;
    static final GenericContainer REDIS_SESSION_CONTAINER;

    static final String MYSQL_IMAGE = "mysql:8.0";
    private static GenericContainer MYSQL_CONTAINER;

    static {
        REDIS_CACHE_CONTAINER = new GenericContainer<>(REDIS_IMAGE)
                .withExposedPorts(6379)
                .withReuse(true);
        REDIS_CACHE_CONTAINER.start();

        REDIS_SESSION_CONTAINER = new GenericContainer<>(REDIS_IMAGE)
                .withExposedPorts(6379)
                .withReuse(true);
        REDIS_SESSION_CONTAINER.start();

        String PATH = "testcontainers/database/";

        MYSQL_CONTAINER = new GenericContainer(
                new ImageFromDockerfile()
                        .withFileFromClasspath("Dockerfile", PATH + "Dockerfile")
                        .withFileFromClasspath("scripts", PATH + "scripts"))
                .withEnv("MYSQL_ROOT_PASSWORD", "root")
                .withEnv("MYSQL_USER", "resell_platform")
                .withEnv("MYSQL_PASSWORD", "resell1234")
                .withReuse(true)
                .withExposedPorts(3306);

        MYSQL_CONTAINER.start();

    }

    @DynamicPropertySource
    public static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.session.redis.master.host", REDIS_SESSION_CONTAINER::getHost);
        registry.add("spring.session.redis.master.port", () -> "" + REDIS_SESSION_CONTAINER.getMappedPort(6379));

        registry.add("spring.cache.redis.master.host", REDIS_CACHE_CONTAINER::getHost);
        registry.add("spring.cache.redis.master.port", () -> "" + REDIS_CACHE_CONTAINER.getMappedPort(6379));

        registry.add("spring.datasource.url", () -> "jdbc:mysql://" + MYSQL_CONTAINER.getHost() +":" + MYSQL_CONTAINER.getMappedPort(3306) + "/resell_platform_v1");
    }

}
