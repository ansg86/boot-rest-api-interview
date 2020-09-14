package posmy.interview.boot.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import posmy.interview.boot.enumeration.BOOK_STATUS;
import posmy.interview.boot.exception.BookIncorrectStatusException;
import posmy.interview.boot.exception.BookNotFoundException;
import posmy.interview.boot.exception.UserNotFoundException;
import posmy.interview.boot.model.Books;
import posmy.interview.boot.repository.BookRepository;

@SpringBootTest
public class BookServiceTest {

	@TestConfiguration
    static class BookServiceImplContextConfiguration {
 
        @Bean
        public BookService bookService() {
            return new BookServiceImpl();
        }
    }
 
    @Autowired
    private BookService bookService;
 
    @MockBean
    private BookRepository bookRepository;
 
    
    @Test
    public void givenBookExists_whenDeleteBook_thenDeleteOk() throws BookNotFoundException, BookIncorrectStatusException {
    	Books book = new Books();
    	book.setId(1L);
    	book.setName("newbook");
    	book.setStatus(BOOK_STATUS.AVAILABLE);
    	
    	Optional<Books> bookOptional = Optional.of(book);
    	when(bookRepository.findById(1L)).thenReturn(bookOptional);   	
    	bookService.deleteBook(1L);
    	
    	verify(bookRepository, times(1)).delete(book);
    }
    
    @Test
    public void givenBookExistsAndNotAvailable_whenDeleteBook_thenReturnFalse() throws BookNotFoundException, BookIncorrectStatusException {
    	Books book = new Books();
    	book.setId(1L);
    	book.setName("newbook");
    	book.setStatus(BOOK_STATUS.BORROWED);
    	
    	Optional<Books> bookOptional = Optional.of(book);
    	when(bookRepository.findById(1L)).thenReturn(bookOptional);   	
    	Assertions.assertThrows(BookIncorrectStatusException.class, () -> {
    		bookService.deleteBook(1L);
    	});
    	
    	
    	verify(bookRepository, times(0)).delete(book);
    }
    
    @Test
    public void givenBookNotExists_whenDeleteBook_thenThrowException() throws BookNotFoundException {
    	Optional<Books> bookOptional = Optional.empty();
    	when(bookRepository.findById(1L)).thenReturn(bookOptional);   	
    	
    	Assertions.assertThrows(BookNotFoundException.class, () -> {
    		bookService.deleteBook(1L);
    	});	
    }
    
    @Test
    public void givenValidId_whenReadBook_thenReadOk() throws BookNotFoundException, BookIncorrectStatusException {
    	Books book = new Books();
    	book.setId(1L);
    	book.setName("newbook");
    	book.setStatus(BOOK_STATUS.AVAILABLE);

    	Optional<Books> bookOptional = Optional.of(book);
    	when(bookRepository.findById(1L)).thenReturn(bookOptional);   
    	bookService.readBook(book.getId());
    	
    	verify(bookRepository, times(1)).save(book);
    }
    
    @Test
    public void givenInvalidId_whenReadBook_thenThrowException() throws BookNotFoundException, BookIncorrectStatusException {
    	Optional<Books> bookOptional = Optional.empty();
    	when(bookRepository.findById(1L)).thenReturn(bookOptional);   
    	
    	Assertions.assertThrows(BookNotFoundException.class, () -> {
    		bookService.readBook(1L);
    	});
    }
    
    @Test
    public void givenBorrowedBook_whenReadBook_thenThrowException() throws BookNotFoundException, BookIncorrectStatusException {
    	Books book = new Books();
    	book.setId(1L);
    	book.setName("newbook");
    	book.setStatus(BOOK_STATUS.BORROWED);

    	Optional<Books> bookOptional = Optional.of(book);
    	when(bookRepository.findById(1L)).thenReturn(bookOptional);   
   
    	Assertions.assertThrows(BookIncorrectStatusException.class, () -> {
    		bookService.readBook(book.getId());
    	});
    	
    	verify(bookRepository, times(0)).save(book);
    }
    
    @Test
    public void givenValidId_whenReturnBook_thenReturnOk() throws BookNotFoundException, BookIncorrectStatusException {
    	Books book = new Books();
    	book.setId(1L);
    	book.setName("newbook");
    	book.setStatus(BOOK_STATUS.BORROWED);

    	Optional<Books> bookOptional = Optional.of(book);
    	when(bookRepository.findById(1L)).thenReturn(bookOptional);   
    	bookService.returnBook(book.getId());
    	
    	verify(bookRepository, times(1)).save(book);
    }
    
    @Test
    public void givenInvalidId_whenReturnBook_thenThrowException() throws BookNotFoundException, BookIncorrectStatusException {
    	Optional<Books> bookOptional = Optional.empty();
    	when(bookRepository.findById(1L)).thenReturn(bookOptional);   
    	
    	Assertions.assertThrows(BookNotFoundException.class, () -> {
    		bookService.returnBook(1L);
    	});
    }
    
    @Test
    public void givenAvailableBook_whenReturnBook_thenThrowException() throws BookNotFoundException, BookIncorrectStatusException {
    	Books book = new Books();
    	book.setId(1L);
    	book.setName("newbook");
    	book.setStatus(BOOK_STATUS.AVAILABLE);

    	Optional<Books> bookOptional = Optional.of(book);
    	when(bookRepository.findById(1L)).thenReturn(bookOptional);   
   
    	Assertions.assertThrows(BookIncorrectStatusException.class, () -> {
    		bookService.returnBook(book.getId());
    	});
    	
    	verify(bookRepository, times(0)).save(book);
    }
}
