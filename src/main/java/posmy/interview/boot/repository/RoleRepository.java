package posmy.interview.boot.repository;

import org.springframework.data.repository.CrudRepository;

import posmy.interview.boot.model.Roles;

public interface RoleRepository extends CrudRepository<Roles, Long> {

}
