package posmy.interview.boot.repository;

import org.springframework.data.repository.CrudRepository;

import posmy.interview.boot.model.Users;

public interface UserRepository extends CrudRepository<Users, String> {

}
