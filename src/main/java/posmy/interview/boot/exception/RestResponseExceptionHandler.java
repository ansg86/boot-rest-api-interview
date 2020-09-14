package posmy.interview.boot.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestResponseExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(value = { BookIncorrectStatusException.class, BookIncorrectStatusException.class })
	protected ResponseEntity<Object> handleBookIncorrectStatus(BookIncorrectStatusException ex, WebRequest request) {
		return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.CONFLICT, request);
	}
	
	@ExceptionHandler(value = { BookNotFoundException.class, BookNotFoundException.class })
	protected ResponseEntity<Object> handleBookNotFound(BookNotFoundException ex, WebRequest request) {
		return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}
	
	@ExceptionHandler(value = { UnauthorizedException.class, UnauthorizedException.class })
	protected ResponseEntity<Object> handleUnauthorized(UnauthorizedException ex, WebRequest request) {
		return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.UNAUTHORIZED, request);
	}
	
	@ExceptionHandler(value = { UserNotFoundException.class, UserNotFoundException.class })
	protected ResponseEntity<Object> handleUserNotFound(UserNotFoundException ex, WebRequest request) {
		return handleExceptionInternal(ex, null, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}
}
