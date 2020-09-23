package posmy.interview.boot.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import posmy.interview.boot.model.Users;
import posmy.interview.boot.security.JwtRequest;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	@DisplayName("non member unable to invoke all API")
	void givenNonMember_whenInvokeAny_thenReturnUnauthorize() {
		HttpHeaders headers = authentication("nonmember");

		// get
		ResponseEntity<Users> userResponse = restTemplate.exchange("/users/get/member", HttpMethod.GET,
				new HttpEntity<>(headers), Users.class);

		assertThat(userResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(UNAUTHORIZED);

		// delete
		ResponseEntity<Users> userTwoResponse = restTemplate.exchange("/users/delete/member", HttpMethod.DELETE,
				new HttpEntity<>(headers), Users.class);
		assertThat(userTwoResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(UNAUTHORIZED);

		// add
		Users newUser = new Users();
		newUser.setName("new-user");
		newUser.setPassword("password");
		newUser.setUserId("newuser");
		headers.add("Content-Type", "application/json");

		HttpEntity<Users> requestEntity = new HttpEntity<>(newUser, headers);
		ResponseEntity<String> userThreeResponse = restTemplate.exchange("/users/add", HttpMethod.POST, requestEntity,
				String.class);
		assertThat(userThreeResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(UNAUTHORIZED);
	}

	@Test
	@DisplayName("member able to see own record")
	void givenMember_whenGetOwn_thenReturnOk() {

		HttpHeaders headers = authentication("member");
		ResponseEntity<Users> userResponse = restTemplate.exchange("/users/get/member", HttpMethod.GET,
				new HttpEntity<>(headers), Users.class);

		assertThat(userResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);

		Users user = userResponse.getBody();
		assertEquals("john", user.getName());
		assertEquals("member", user.getUserId());
	}

	@Test
	@DisplayName("member not able to see other record")
	void givenMember_whenGetOther_thenReturnUnauthorized() {
		HttpHeaders headers = authentication("member");
		ResponseEntity<Users> userResponse = restTemplate.exchange("/users/get/librarian", HttpMethod.GET,
				new HttpEntity<>(headers), Users.class);

		assertThat(userResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(UNAUTHORIZED);
	}

	@Test
	@DisplayName("librarian able to see all record")
	void givenLibrarian_whenGetOther_thenReturnOk() {
		HttpHeaders headers = authentication("librarian");
		ResponseEntity<Users> userResponse = restTemplate.exchange("/users/get/member", HttpMethod.GET,
				new HttpEntity<>(headers), Users.class);

		assertThat(userResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);

		Users user = userResponse.getBody();
		assertEquals("john", user.getName());
		assertEquals("member", user.getUserId());
	}

	@Test
	@DisplayName("member not able to add record")
	void givenMember_whenAdd_thenReturnUnauthorized() {
		HttpHeaders headers = authentication("member");
		// add
		Users newUser = new Users();
		newUser.setName("new-user");
		newUser.setPassword("password");
		newUser.setUserId("newuser");
		headers.add("Content-Type", "application/json");

		HttpEntity<Users> requestEntity = new HttpEntity<>(newUser, headers);
		ResponseEntity<String> userTwoResponse = restTemplate.exchange("/users/add", HttpMethod.POST, requestEntity,
				String.class);
		assertThat(userTwoResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(UNAUTHORIZED);
	}

	@Test
	@DisplayName("librarian able to add record")
	void givenLibrarian_whenAdd_thenReturnOK() {
		HttpHeaders headers = authentication("librarian");
		// add
		Users newUser = new Users();
		newUser.setName("new-user");
		newUser.setPassword("password");
		newUser.setUserId("newuser");
		headers.add("Content-Type", "application/json");

		HttpEntity<Users> requestEntity = new HttpEntity<>(newUser, headers);
		ResponseEntity<String> userResponse = restTemplate.exchange("/users/add", HttpMethod.POST, requestEntity,
				String.class);
		assertThat(userResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);

		headers = authentication("librarian");
		ResponseEntity<Users> userTwoResponse = restTemplate.exchange("/users/get/newuser", HttpMethod.GET,
				new HttpEntity<>(headers), Users.class);

		assertThat(userTwoResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);
		Users user = userTwoResponse.getBody();
		assertEquals("new-user", user.getName());
		assertEquals("newuser", user.getUserId());

		//ensure new-user can authenticate
		headers = authentication("newuser");
	}

	@Test
	@DisplayName("member not able to update record")
	void givenMember_whenUpdate_thenReturnUnauthorized() {
		HttpHeaders headers = authentication("member");
		ResponseEntity<Users> userResponse = restTemplate.exchange("/users/get/member", HttpMethod.GET,
				new HttpEntity<>(headers), Users.class);

		assertThat(userResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);

		Users user = userResponse.getBody();
		assertEquals("john", user.getName());
		assertEquals("member", user.getUserId());
		user.setName("abc");

		HttpEntity<Users> requestEntity = new HttpEntity<>(user, headers);
		ResponseEntity<String> userTwoResponse = restTemplate.exchange("/users/update", HttpMethod.POST, requestEntity,
				String.class);
		assertThat(userTwoResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(UNAUTHORIZED);
	}

	@Test
	@DisplayName("librarian able to update record")
	void givenLibrarian_whenUpdate_thenReturnOK() {
		HttpHeaders headers = authentication("librarian");
		ResponseEntity<Users> userResponse = restTemplate.exchange("/users/get/member", HttpMethod.GET,
				new HttpEntity<>(headers), Users.class);

		assertThat(userResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);

		Users user = userResponse.getBody();
		assertEquals("john", user.getName());
		assertEquals("member", user.getUserId());
		user.setName("abc");

		HttpEntity<Users> requestEntity = new HttpEntity<>(user, headers);
		ResponseEntity<String> userTwoResponse = restTemplate.exchange("/users/update", HttpMethod.POST, requestEntity,
				String.class);
		assertThat(userResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);

		HttpHeaders headersNew = authentication("librarian");
		ResponseEntity<Users> userThreeResponse = restTemplate.exchange("/users/get/member", HttpMethod.GET,
				new HttpEntity<>(headersNew), Users.class);

		assertThat(userTwoResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);
		Users updatedUser = userThreeResponse.getBody();
		assertEquals("abc", updatedUser.getName());
		assertEquals("member", updatedUser.getUserId());
	}

	@Test
	@DisplayName("member able to delete himself")
	void givenMember_whenDeleteOwn_thenReturnOk() {
		HttpHeaders headers = authentication("member");

		// delete
		ResponseEntity<Users> userThreeResponse = restTemplate.exchange("/users/delete/member", HttpMethod.DELETE,
				new HttpEntity<>(headers), Users.class);
		assertThat(userThreeResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);
		
		headers = authentication("librarian");
		ResponseEntity<Users> userResponse = restTemplate.exchange("/users/get/member", HttpMethod.GET,
				new HttpEntity<>(headers), Users.class);

		assertThat(userResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(NO_CONTENT);
	}

	@Test
	@DisplayName("member not able to delete other")
	void givenMember_whenDeleteOther_thenReturnUnauthorized() {
		HttpHeaders headers = authentication("member");

		// delete
		ResponseEntity<Users> userThreeResponse = restTemplate.exchange("/users/delete/librarian", HttpMethod.DELETE,
				new HttpEntity<>(headers), Users.class);
		assertThat(userThreeResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(UNAUTHORIZED);
	}

	@Test
	@DisplayName("librarian able to delete other")
	void givenLibrarian_whenDeleteOther_thenReturnOK() {
		HttpHeaders headers = authentication("librarian");

		// delete
		ResponseEntity<Users> userThreeResponse = restTemplate.exchange("/users/delete/member", HttpMethod.DELETE,
				new HttpEntity<>(headers), Users.class);
		assertThat(userThreeResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);

		ResponseEntity<Users> userResponse = restTemplate.exchange("/users/get/member", HttpMethod.GET,
				new HttpEntity<>(headers), Users.class);

		assertThat(userResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(NO_CONTENT);
	}

	private HttpHeaders updateHeaderWithToken(String token) {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + token);
		return headers;
	}

	private String getJWTToken(ResponseEntity<String> response) {
		String jsonString = response.getBody();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			String token = jsonObject.get("token").toString();
			return token;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	private HttpHeaders authentication(String username) {
		JwtRequest jwtRequest = new JwtRequest();
		jwtRequest.setUsername(username);
		jwtRequest.setPassword("password");

		ResponseEntity<String> response = restTemplate.postForEntity("/authenticate", jwtRequest, String.class);
		assertThat(response).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);
		String token = getJWTToken(response);
		HttpHeaders headers = updateHeaderWithToken(token);
		return headers;
	}
}
