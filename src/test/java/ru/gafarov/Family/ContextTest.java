package ru.gafarov.Family;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.gafarov.Family.rest.AuthenticationRestControllerV1;
import ru.gafarov.Family.rest.RegisterRestControllerV1;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class ContextTest {

    @Autowired
    AuthenticationRestControllerV1 authenticationRestController;

    @Autowired
    RegisterRestControllerV1 registerRestController;

    @Test
    void contextLoads() {
        assertThat(authenticationRestController).isNotNull();
        assertThat(registerRestController).isNotNull();
    }
}
