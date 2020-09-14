package posmy.interview.boot.security;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import posmy.interview.boot.model.Users;

@Component
public class JwtUtil implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final long JWT_TOKEN_VALIDITY = 5 * 60 * 60;

	private static final String USERROLE = "userRole";

	// preferable store in database
	private static final String secret = "secret-api";

	// retrieve username from jwt token
	public String getUserNameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	// retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	// retrieve userrole from jwt token
	public String getUserRoleFromToken(String token) {
		return getAllClaimsFromToken(token).get(USERROLE).toString();
	}

	// generate token for user
	public String generateToken(UserDetails userDetails, Users currentUser) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(USERROLE, currentUser.getUserRole() != null ? currentUser.getUserRole().getLabel() : "");
		return doGenerateToken(claims, userDetails.getUsername());
	}

	// validate token
	public boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUserNameFromToken(token);

		// validate token's userId and db's userId
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	// for retrieving any information from token we will need the secret key
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
	}

	// check if the token has expired
	private boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	// while creating the token -
	// 1. Define claims of the token, like Issuer, Expiration, Subject, and the ID
	// 2. Sign the JWT using the HS512 algorithm and secret key.
	// 3. According to JWS Compact
	// Serialization(https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.1)
	// compaction of the JWT to a URL-safe string
	private String doGenerateToken(Map<String, Object> claims, String subject) {
		return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

}
