package posmy.interview.boot.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import posmy.interview.boot.exception.UserNotFoundException;
import posmy.interview.boot.model.Users;
import posmy.interview.boot.repository.UserRepository;

@SpringBootTest
public class UserServiceTest {

	@InjectMocks
	private UserServiceImpl userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Test
	public void givenUserExists_whenDeleteUser_thenDeleteOk() throws UserNotFoundException {
		Users user = new Users();
		user.setName("testuser");
		user.setPassword("password");
		user.setUserId("testuser");

		Optional<Users> userOptional = Optional.of(user);
		when(userRepository.findById("testuser")).thenReturn(userOptional);
		userService.deleteUser("testuser");

		verify(userRepository, times(1)).delete(user);
	}

	@Test
	public void givenUserNotExists_whenDeleteUser_thenThrowUserNotFoundException() throws UserNotFoundException {

		Optional<Users> userOptional = Optional.empty();
		when(userRepository.findById("testuser")).thenReturn(userOptional);

		Assertions.assertThrows(UserNotFoundException.class, () -> {
			userService.deleteUser("testuser");
		});
	}

	@Test
	public void givenUsersObject_whenAddUser_thenAddOk() throws UserNotFoundException {
		Users user = new Users();
		user.setName("testuser");
		user.setPassword("password");
		user.setUserId("testuser");

		userService.addOrUpdateUser(user);

		verify(passwordEncoder, times(1)).encode(ArgumentMatchers.anyString());
		verify(userRepository, times(1)).save(user);
	}

	@Test
	public void givenUserExists_whenGetUser_thenReturnOk() throws UserNotFoundException {
		Users user = new Users();
		user.setName("testuser");
		user.setPassword("password");
		user.setUserId("testuser");

		Optional<Users> userOptional = Optional.of(user);
		when(userRepository.findById("testuser")).thenReturn(userOptional);
		userService.getUser("testuser");

		verify(userRepository, times(1)).findById(user.getUserId());
	}

	@Test
	public void givenUserNotExists_whenGetUser_thenThrowUserNotFoundException() throws UserNotFoundException {

		Optional<Users> userOptional = Optional.empty();
		when(userRepository.findById("testuser")).thenReturn(userOptional);

		Assertions.assertThrows(UserNotFoundException.class, () -> {
			userService.getUser("testuser");
		});
	}
}
