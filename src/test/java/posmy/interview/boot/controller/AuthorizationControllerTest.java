package posmy.interview.boot.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import posmy.interview.boot.security.JwtRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.HttpStatus.OK;;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class AuthorizationControllerTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	@DisplayName("Invalid credential user")
	void givenInvalidCredential_whenAuthorized_thenReturnUnauthorized() {
		JwtRequest jwtRequest = new JwtRequest();
		jwtRequest.setUsername("member");
		jwtRequest.setPassword("password1");

		ResponseEntity<String> response = restTemplate.postForEntity("/authenticate", jwtRequest, String.class);

		assertThat(response).extracting(ResponseEntity::getStatusCode).isEqualTo(UNAUTHORIZED);
	}

	@Test
	@DisplayName("Valid credential user")
	void givenValidCredential_whenAuthorized_thenReturnOk() {
		JwtRequest jwtRequest = new JwtRequest();
		jwtRequest.setUsername("member");
		jwtRequest.setPassword("password");

		ResponseEntity<String> response = restTemplate.postForEntity("/authenticate", jwtRequest, String.class);

		assertThat(response).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);
	}
}
