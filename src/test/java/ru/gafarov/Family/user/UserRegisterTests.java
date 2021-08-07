package ru.gafarov.Family.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.gafarov.Family.dto.userDto.UserDto;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class UserRegisterTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	UserTests userTests;

	@Value("${test.username}") private String username;
	@Value("${test.password}") private String password;
	@Value("${test.phone}") private Long phone;
	@Value("${test.email}") private String email;
	private static Long id;

	@BeforeEach
	public void deleteRegisteredUser(){ // Удалить тестового юзера перед тестом, если он есть

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

	@Test // регистрация
	public void register() throws Exception {
		MvcResult result = this.mockMvc.perform(post("/api/v1/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"username\":\"" + username + "\", " +
						"\"phone\":" + phone + ", " +
						"\"email\":\"" + email + "\"," +
						"\"password\":\"" + password + "\"}")
				.accept(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(content().string(containsString(username)))
				.andReturn();

		String userJSON = result.getResponse().getContentAsString();
		System.out.println(userJSON);
		ObjectMapper mapper = new ObjectMapper();
		UserDto userDto = mapper.readValue(userJSON, UserDto.class);
		UserRegisterTests.id = userDto.getId();

	}

	@AfterEach
	public void deleteRegisteredUserAfter(){ // Удалить тестового юзера после теста

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
