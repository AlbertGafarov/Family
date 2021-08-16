package ru.gafarov.Family.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.gafarov.Family.dto.userDto.AuthenticationRequestDto;
import ru.gafarov.Family.dto.userDto.UserCreateDto;
import ru.gafarov.Family.dto.userDto.UserTokenDto;

import java.net.URI;

@Service
class UserTests {

	@Autowired
	RestTemplate restTemplate;

	public void register(String username, String email, Long phone, String password){ //регистрация тестового пользователя

		UserCreateDto userCreateDto = UserCreateDto.builder()
				.username(username)
				.email(email)
				.phone(phone)
				.password(password)
				.build();
		System.out.println(userCreateDto);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity<UserCreateDto> httpEntity = new HttpEntity<>(userCreateDto, headers);
		ResponseEntity<UserCreateDto> response = restTemplate
				.exchange(URI.create("http://localhost:8071/api/v1/register")
						, HttpMethod.POST
						, httpEntity
						, new ParameterizedTypeReference<>() {
						});
		UserCreateDto newUserCreateDto = response.getBody();
		assert newUserCreateDto != null;

	}

	public void deleteRegisteredUser(String bearerToken){ // Удалить тестового пользователя

		if(bearerToken!=null) {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", bearerToken);
			HttpEntity<Object> httpEntity = new HttpEntity<>(null, headers);
			ResponseEntity<Object> response = restTemplate.
					exchange(URI.create("http://localhost:8071/api/v1/users")
							, HttpMethod.DELETE
							, httpEntity
							, Object.class);
			assert response.getStatusCode() == HttpStatus.OK;
		}
	}

	public String login(String username, String password){ //Авторизация тестового пользователя
		AuthenticationRequestDto authenticationRequestDto = new AuthenticationRequestDto(username,password);
		ResponseEntity<UserTokenDto> responseEntity = restTemplate.
				exchange(URI.create("http://localhost:8071/api/v1/auth/login")
						, HttpMethod.POST
						, new HttpEntity<>(authenticationRequestDto)
						, new ParameterizedTypeReference<>() {});

		System.out.println(responseEntity.getBody());
		UserTokenDto userTokenDto = responseEntity.getBody();
		assert userTokenDto != null;
		return  "Bearer_" + userTokenDto.getToken();
	}
}
