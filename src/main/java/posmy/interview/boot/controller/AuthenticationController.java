package posmy.interview.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import posmy.interview.boot.model.Users;
import posmy.interview.boot.repository.UserRepository;
import posmy.interview.boot.security.JwtRequest;
import posmy.interview.boot.security.JwtResponse;
import posmy.interview.boot.security.JwtUserDetailService;
import posmy.interview.boot.security.JwtUtil;


@RestController
public class AuthenticationController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private JwtUserDetailService jwtUserDetailService;
	
	@Autowired
	private UserRepository userRepository;

	/****
	 * This endpoint get authenticationRequest, compose of 'username' and 'password', and perform
	 * 1. authenticationManager.authenticate -> calling JwtUserDetailService
	 * 2. If unauthorized, will invoke JwtAuthenticationEntryPoint, commence method which return unauthorized
	 * 3. If authorized, will utilize on jwtUtil to generate token.
	 * 4. Return jwtToken to user
	 * 
	 * 
	 * @param authenticationRequest
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/authenticate")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {
		authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
		UserDetails userDetails = jwtUserDetailService.loadUserByUsername(authenticationRequest.getUsername());
		Users currentUser = userRepository.findById(authenticationRequest.getUsername()).orElseThrow(() -> new UsernameNotFoundException("User not found with userId: " + authenticationRequest.getUsername()));
		String token = jwtUtil.generateToken(userDetails, currentUser);
		return ResponseEntity.ok(new JwtResponse(token));
	}

	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}
}
