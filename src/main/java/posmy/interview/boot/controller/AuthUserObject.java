package posmy.interview.boot.controller;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AuthUserObject {

	private String userId;
	private String role;
}
