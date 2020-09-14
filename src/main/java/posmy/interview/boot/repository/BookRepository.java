package posmy.interview.boot.repository;

import org.springframework.data.repository.CrudRepository;

import posmy.interview.boot.model.Books;

public interface BookRepository extends CrudRepository<Books, Long> {

}
