package posmy.interview.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import posmy.interview.boot.enumeration.ROLE_LABEL;
import posmy.interview.boot.exception.UnauthorizedException;
import posmy.interview.boot.model.Users;
import posmy.interview.boot.security.JwtUtil;
import posmy.interview.boot.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController extends BaseController {

	private final UserService userService;

	private final JwtUtil jwtUtil;
	
	@Autowired
	public UserController(UserService userService, JwtUtil jwtUtil) {
		this.userService = userService;
		this.jwtUtil = jwtUtil;
	}

	@GetMapping(value = "/get/{userId}")
	public ResponseEntity<Users> getUser(@PathVariable("userId") String userId,
			@RequestHeader("Authorization") String token) throws Exception {
		AuthUserObject authUserObject = generateAuthUserObject(token);
		ensureUserWithRole(authUserObject);
		// only can get own user for MEMBERS, LIBRARIAN can get all user
		if (ROLE_LABEL.MEMBER.name().equals(authUserObject.getRole())) {
			if (!userId.equals(authUserObject.getUserId())) {
				throw new UnauthorizedException("you are not allowed to perform this action");
			}
		}
		Users user = userService.getUser(userId);
		return ResponseEntity.ok(user);
	}

	@PostMapping(value = "/add")
	public ResponseEntity<?> addUser(@RequestBody Users user, @RequestHeader("Authorization") String token)
			throws Exception {
		AuthUserObject authUserObject = generateAuthUserObject(token);
		ensureUserWithRole(authUserObject);
		// only librarian can do this
		if (!ROLE_LABEL.LIBRARIAN.name().equals(authUserObject.getRole())) {
			throw new UnauthorizedException("you are not allowed to perform this action");
		}
		userService.addOrUpdateUser(user);
		return ResponseEntity.ok().build();
	}

	@PostMapping(value = "/update")
	public ResponseEntity<?> updateUser(@RequestBody Users user, @RequestHeader("Authorization") String token)
			throws Exception {
		AuthUserObject authUserObject = generateAuthUserObject(token);
		ensureUserWithRole(authUserObject);
		// only librarian can do this
		if (!ROLE_LABEL.LIBRARIAN.name().equals(authUserObject.getRole())) {
			throw new UnauthorizedException("you are not allowed to perform this action");
		}
		userService.addOrUpdateUser(user);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping(value = "/delete/{userId}")
	public ResponseEntity<?> delete(@PathVariable("userId") String userId, @RequestHeader("Authorization") String token)
			throws Exception {
		AuthUserObject authUserObject = generateAuthUserObject(token);
		ensureUserWithRole(authUserObject);
		// only librarian can delete any user, member can delete itself only
		if (ROLE_LABEL.MEMBER.name().equals(authUserObject.getRole())) {
			if (!userId.equals(authUserObject.getUserId())) {
				throw new UnauthorizedException("you are not allowed to perform this action");
			}
		}
		userService.deleteUser(userId);
		return ResponseEntity.ok().build();
	}

	private AuthUserObject generateAuthUserObject(String token) {
		if (token.startsWith("Bearer ")) {
			token = token.substring(7);
		}

		AuthUserObject authUserObject = AuthUserObject.builder().userId(jwtUtil.getUserNameFromToken(token))
				.role(jwtUtil.getUserRoleFromToken(token)).build();
		return authUserObject;
	}
}
