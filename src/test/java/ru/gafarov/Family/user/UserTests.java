package ru.gafarov.Family.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.gafarov.Family.dto.userDto.AuthenticationRequestDto;
import ru.gafarov.Family.dto.userDto.UserRegisterDto;
import ru.gafarov.Family.dto.userDto.UserTokenDto;

import java.net.URI;

@Service
class UserTests {

	@Autowired
	RestTemplate restTemplate;

	public UserRegisterDto register(String username, String email, Long phone, String password){ //регистрация тестового пользователя

		UserRegisterDto userRegisterDto = new UserRegisterDto(username,email,phone,password);
		System.out.println(userRegisterDto);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		HttpEntity<UserRegisterDto> httpEntity = new HttpEntity<>(userRegisterDto, headers);
		ResponseEntity<UserRegisterDto> response = restTemplate
				.exchange(URI.create("http://localhost:8071/api/v1/register")
						, HttpMethod.POST
						, httpEntity
						, new ParameterizedTypeReference<UserRegisterDto>() {});
		UserRegisterDto newUserRegisterDto = response.getBody();
		assert newUserRegisterDto != null;
		return newUserRegisterDto;

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
