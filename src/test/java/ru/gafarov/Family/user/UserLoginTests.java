package ru.gafarov.Family.user;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserLoginTests {

    @Value("${test.username}") private String username;
    @Value("${test.password}") private String password;
    @Value("${test.phone}") private Long phone;
    @Value("${test.email}") private String email;

    @Autowired
    UserTests userTests;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void register(){
        String bearerToken=null;
        try {
            bearerToken = userTests.login(username, password);
        } catch (Exception e){
            System.out.println("Удалять некого");
            System.out.println(username+password+phone+email);
        }
        finally {
            userTests.deleteRegisteredUser(bearerToken);
            userTests.register(username, email, phone, password);
            System.out.println("юзер создан");
        }

    }

    @Test
    void loginTest() throws Exception{
        this.mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\""+username+"\", \"password\":\""+password+"\"}")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("token")))
                .andExpect(content().string(containsString(username)));
    }

    @AfterEach
    public void deleteRegisteredUser(){ // Удалить тестового юзера после теста

        String bearerToken=null;
        try {
            bearerToken = userTests.login(username, password);
        } catch (Exception e){
            System.out.println("Удалять некого");
            System.out.println(username+password+phone+email);
        }
        finally {
            userTests.deleteRegisteredUser(bearerToken);
        }
    }

}
