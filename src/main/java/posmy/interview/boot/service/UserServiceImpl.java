package posmy.interview.boot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import posmy.interview.boot.exception.UserNotFoundException;
import posmy.interview.boot.model.Users;
import posmy.interview.boot.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	private final PasswordEncoder passwordEncoder;
	
	@Autowired
	public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void deleteUser(String userId) throws UserNotFoundException {
		Users user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("This user does not exists"));
		userRepository.delete(user);
	}

	@Override
	public void addOrUpdateUser(Users user) {
		//ensure password is encoded before save to db
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userRepository.save(user);
	}

	@Override
	public Users getUser(String userId) throws UserNotFoundException {

		Users user = userRepository.findById(userId)
				.orElseThrow(() -> new UserNotFoundException("This user does not exists"));

		return user;
	}
}
