package posmy.interview.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import posmy.interview.boot.enumeration.ROLE_LABEL;
import posmy.interview.boot.exception.UnauthorizedException;
import posmy.interview.boot.model.Books;
import posmy.interview.boot.security.JwtUtil;
import posmy.interview.boot.service.BookService;

@RestController
@RequestMapping("/books")
public class BookController extends BaseController {

	@Autowired
	private BookService bookService;

	@Autowired
	private JwtUtil jwtUtil;

	@GetMapping(value = "/get/{id}")
	public ResponseEntity<Books> getBook(@PathVariable("id") Long bookId, @RequestHeader("Authorization") String token)
			throws Exception {
		AuthUserObject authUserObject = generateAuthUserObject(token);
		ensureUserWithRole(authUserObject);
		Books book = bookService.readBook(bookId);
		return ResponseEntity.ok(book);
	}

	@GetMapping(value = "/return/{id}")
	public ResponseEntity<?> returnBook(@PathVariable("id") Long bookId, @RequestHeader("Authorization") String token)
			throws Exception {
		AuthUserObject authUserObject = generateAuthUserObject(token);
		ensureUserWithRole(authUserObject);
		bookService.returnBook(bookId);
		return ResponseEntity.ok().build();
	}

	@PostMapping(value = "/add")
	public ResponseEntity<?> addBook(@RequestBody Books book, @RequestHeader("Authorization") String token)
			throws Exception {
		AuthUserObject authUserObject = generateAuthUserObject(token);
		ensureUserWithRole(authUserObject);
		// only librarian can do this
		if (!ROLE_LABEL.LIBRARIAN.name().equals(authUserObject.getRole())) {
			throw new UnauthorizedException("you are not allowed to perform this action");
		}

		bookService.addOrUpdateBook(book);
		return ResponseEntity.ok().build();
	}

	@PostMapping(value = "/update")
	public ResponseEntity<?> updateBook(@RequestBody Books book, @RequestHeader("Authorization") String token)
			throws Exception {
		AuthUserObject authUserObject = generateAuthUserObject(token);
		ensureUserWithRole(authUserObject);
		// only librarian can do this
		if (!ROLE_LABEL.LIBRARIAN.name().equals(authUserObject.getRole())) {
			throw new UnauthorizedException("you are not allowed to perform this action");
		}

		bookService.addOrUpdateBook(book);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping(value = "/delete/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") Long bookId, @RequestHeader("Authorization") String token)
			throws Exception {
		AuthUserObject authUserObject = generateAuthUserObject(token);
		ensureUserWithRole(authUserObject);
		// only librarian can do this
		if (!ROLE_LABEL.LIBRARIAN.name().equals(authUserObject.getRole())) {
			throw new UnauthorizedException("you are not allowed to perform this action");
		}

		bookService.deleteBook(bookId);
		return ResponseEntity.ok().build();
	}

	private AuthUserObject generateAuthUserObject(String token) {
		if (token.startsWith("Bearer ")) {
			token = token.substring(7);
		}
		AuthUserObject authUserObject = AuthUserObject.builder().userId(jwtUtil.getUserNameFromToken(token))
				.role(jwtUtil.getUserRoleFromToken(token)).build();
		return authUserObject;
	}
}
