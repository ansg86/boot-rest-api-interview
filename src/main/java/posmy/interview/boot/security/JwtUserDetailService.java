package posmy.interview.boot.security;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import posmy.interview.boot.model.Users;
import posmy.interview.boot.repository.UserRepository;

@Service
public class JwtUserDetailService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Users user = userRepository.findById(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with userId: " + username));
		return new User(user.getUserId(), user.getPassword(), new ArrayList<>());
	}
}