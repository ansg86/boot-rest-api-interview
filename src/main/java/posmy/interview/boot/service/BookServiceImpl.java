package posmy.interview.boot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import posmy.interview.boot.enumeration.BOOK_STATUS;
import posmy.interview.boot.exception.BookIncorrectStatusException;
import posmy.interview.boot.exception.BookNotFoundException;
import posmy.interview.boot.model.Books;
import posmy.interview.boot.repository.BookRepository;

@Service
public class BookServiceImpl implements BookService {

	private final BookRepository booksRepository;
	
	@Autowired
	public BookServiceImpl(BookRepository booksRepository) {
		this.booksRepository = booksRepository;
	}

	@Override
	public void addOrUpdateBook(Books book) {
		booksRepository.save(book);
	}

	@Override
	public void deleteBook(Long id) throws BookNotFoundException, BookIncorrectStatusException {

		Books book = booksRepository.findById(id)
				.orElseThrow(() -> new BookNotFoundException("This book does not exists"));
		// borrowed book not possible to be delete
		if (BOOK_STATUS.BORROWED.equals(book.getStatus())) {
			throw new BookIncorrectStatusException("This book is in invalid state");
		}

		booksRepository.delete(book);
	}

	@Override
	public Books readBook(Long id) throws BookNotFoundException, BookIncorrectStatusException {
		Books book = booksRepository.findById(id)
				.orElseThrow(() -> new BookNotFoundException("This book does not exists"));
		// borrowed book cannot be re-borrow again
		if (BOOK_STATUS.BORROWED.equals(book.getStatus())) {
			throw new BookIncorrectStatusException("This book is in invalid state");
		}

		book.setStatus(BOOK_STATUS.BORROWED);
		booksRepository.save(book);
		return book;
	}

	@Override
	public void returnBook(Long id) throws BookNotFoundException, BookIncorrectStatusException {
		Books book = booksRepository.findById(id)
				.orElseThrow(() -> new BookNotFoundException("This book does not exists"));
		// available book cannot be return again
		if (BOOK_STATUS.AVAILABLE.equals(book.getStatus())) {
			throw new BookIncorrectStatusException("This book is in invalid state");
		}

		book.setStatus(BOOK_STATUS.AVAILABLE);
		booksRepository.save(book);
	}
}
