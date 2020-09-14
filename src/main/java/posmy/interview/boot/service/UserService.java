package posmy.interview.boot.service;

import posmy.interview.boot.exception.UserNotFoundException;
import posmy.interview.boot.model.Users;

public interface UserService {

	public void deleteUser(String userId) throws UserNotFoundException;

	public void addOrUpdateUser(Users user);

	public Users getUser(String userId) throws UserNotFoundException;
}
