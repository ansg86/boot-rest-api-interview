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

import posmy.interview.boot.enumeration.BOOK_STATUS;
import posmy.interview.boot.model.Books;
import posmy.interview.boot.security.JwtRequest;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookControllerTest {

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	@DisplayName("non member unable to invoke all API")
	void givenNonMember_whenInvokeAny_thenReturnUnauthorize() {
		HttpHeaders headers = authentication("nonmember");

		// get
		ResponseEntity<Books> bookResponse = restTemplate.exchange("/books/get/1", HttpMethod.GET,
				new HttpEntity<>(headers), Books.class);

		assertThat(bookResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(UNAUTHORIZED);

		// return
		bookResponse = restTemplate.exchange("/books/return/1", HttpMethod.POST, new HttpEntity<>(headers), Books.class);

		assertThat(bookResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(UNAUTHORIZED);

		// delete
		bookResponse = restTemplate.exchange("/books/delete/1", HttpMethod.DELETE, new HttpEntity<>(headers),
				Books.class);
		assertThat(bookResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(UNAUTHORIZED);

		// add
		Books newBook = new Books();
		newBook.setId(123L);
		newBook.setName("new-book");
		newBook.setStatus(BOOK_STATUS.AVAILABLE);
		headers.add("Content-Type", "application/json");

		HttpEntity<Books> requestEntity = new HttpEntity<>(newBook, headers);
		bookResponse = restTemplate.exchange("/books/add", HttpMethod.POST, requestEntity, Books.class);
		assertThat(bookResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(UNAUTHORIZED);
	}

	@Test
	@DisplayName("member able to get/read and return book")
	void givenMember_whenGetBook_thenReturnOk() {

		HttpHeaders headers = authentication("member");
		ResponseEntity<Books> bookResponse = restTemplate.exchange("/books/get/1", HttpMethod.GET,
				new HttpEntity<>(headers), Books.class);

		assertThat(bookResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);

		Books book = bookResponse.getBody();
		assertEquals("story-one", book.getName());
		assertEquals(1L, book.getId());

		bookResponse = restTemplate.exchange("/books/return/1", HttpMethod.POST, new HttpEntity<>(headers), Books.class);

		assertThat(bookResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);
	}

	@Test
	@DisplayName("librarian able to get/read and return book")
	void givenLibrarian_whenGetBook_thenReturnOk() {

		HttpHeaders headers = authentication("librarian");
		ResponseEntity<Books> bookResponse = restTemplate.exchange("/books/get/1", HttpMethod.GET,
				new HttpEntity<>(headers), Books.class);

		assertThat(bookResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);

		Books book = bookResponse.getBody();
		assertEquals("story-one", book.getName());
		assertEquals(1L, book.getId());

		bookResponse = restTemplate.exchange("/books/return/1", HttpMethod.POST, new HttpEntity<>(headers), Books.class);

		assertThat(bookResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);
	}

	@Test
	@DisplayName("member not able to add record")
	void givenMember_whenAdd_thenReturnUnauthorized() {
		HttpHeaders headers = authentication("member");
		// add
		Books newBook = new Books();
		newBook.setId(123L);
		newBook.setName("new-book");
		newBook.setStatus(BOOK_STATUS.AVAILABLE);
		headers.add("Content-Type", "application/json");

		HttpEntity<Books> requestEntity = new HttpEntity<>(newBook, headers);
		ResponseEntity<Books> bookResponse = restTemplate.exchange("/books/add", HttpMethod.POST, requestEntity,
				Books.class);
		assertThat(bookResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(UNAUTHORIZED);
	}

	@Test
	@DisplayName("librarian able to add record")
	void givenLibrarian_whenAdd_thenReturnOK() {
		HttpHeaders headers = authentication("librarian");
		// add
		Books newBook = new Books();
		newBook.setId(123L);
		newBook.setName("new-book");
		newBook.setStatus(BOOK_STATUS.AVAILABLE);
		headers.add("Content-Type", "application/json");

		HttpEntity<Books> requestEntity = new HttpEntity<>(newBook, headers);
		ResponseEntity<Books> bookResponse = restTemplate.exchange("/books/add", HttpMethod.POST, requestEntity,
				Books.class);
		assertThat(bookResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);
	}

	@Test
	@DisplayName("member not able to update record")
	void givenMember_whenUpdate_thenReturnUnauthorized() {
		HttpHeaders headers = authentication("member");
		ResponseEntity<Books> bookResponse = restTemplate.exchange("/books/get/1", HttpMethod.GET,
				new HttpEntity<>(headers), Books.class);

		assertThat(bookResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);

		Books book = bookResponse.getBody();
		assertEquals("story-one", book.getName());
		assertEquals(1L, book.getId());
		book.setName("story-updated");

		HttpEntity<Books> requestEntity = new HttpEntity<>(book, headers);
		bookResponse = restTemplate.exchange("/books/update", HttpMethod.POST, requestEntity, Books.class);
		assertThat(bookResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(UNAUTHORIZED);
	}

	@Test
	@DisplayName("librarian able to update record")
	void givenLibrarian_whenUpdate_thenReturnOK() {
		HttpHeaders headers = authentication("librarian");
		ResponseEntity<Books> bookResponse = restTemplate.exchange("/books/get/1", HttpMethod.GET,
				new HttpEntity<>(headers), Books.class);

		assertThat(bookResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);

		Books book = bookResponse.getBody();
		assertEquals("story-one", book.getName());
		assertEquals(1L, book.getId());
		book.setName("story-updated");

		HttpEntity<Books> requestEntity = new HttpEntity<>(book, headers);
		bookResponse = restTemplate.exchange("/books/update", HttpMethod.POST, requestEntity, Books.class);
		assertThat(bookResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);

		bookResponse = restTemplate.exchange("/books/return/1", HttpMethod.POST, new HttpEntity<>(headers), Books.class);

		assertThat(bookResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);

		bookResponse = restTemplate.exchange("/books/get/1", HttpMethod.GET, new HttpEntity<>(headers), Books.class);

		assertThat(bookResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);

		book = bookResponse.getBody();
		assertEquals("story-updated", book.getName());
		assertEquals(1L, book.getId());
	}

	@Test
	@DisplayName("member not able to delete record")
	void givenMember_whenDelete_thenReturnUnauthorized() {
		HttpHeaders headers = authentication("member");

		ResponseEntity<Books> bookResponse = restTemplate.exchange("/books/delete/1", HttpMethod.DELETE,
				new HttpEntity<>(headers), Books.class);
		assertThat(bookResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(UNAUTHORIZED);
	}

	@Test
	@DisplayName("librarian able to delete record")
	void givenLibrarian_whenDelete_thenReturnOK() {
		HttpHeaders headers = authentication("librarian");

		ResponseEntity<Books> bookResponse = restTemplate.exchange("/books/delete/1", HttpMethod.DELETE,
				new HttpEntity<>(headers), Books.class);
		assertThat(bookResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(OK);

		bookResponse = restTemplate.exchange("/books/get/1", HttpMethod.GET, new HttpEntity<>(headers), Books.class);

		assertThat(bookResponse).extracting(ResponseEntity::getStatusCode).isEqualTo(NO_CONTENT);
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
