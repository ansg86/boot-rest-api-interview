package posmy.interview.boot.service;

import posmy.interview.boot.exception.BookIncorrectStatusException;
import posmy.interview.boot.exception.BookNotFoundException;
import posmy.interview.boot.model.Books;

public interface BookService {

	public void addOrUpdateBook(Books book);
	
	public void deleteBook(Long id) throws BookNotFoundException, BookIncorrectStatusException;
	
	public Books readBook(Long id) throws BookNotFoundException, BookIncorrectStatusException;
	
	public void returnBook(Long id) throws BookNotFoundException, BookIncorrectStatusException;
}
