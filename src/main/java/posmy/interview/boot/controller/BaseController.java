package posmy.interview.boot.controller;

import posmy.interview.boot.exception.UnauthorizedException;

public class BaseController {

	protected void ensureUserWithRole(AuthUserObject authUserObject) throws UnauthorizedException {
		if("".equals(authUserObject.getRole())) {
			throw new UnauthorizedException("you are not allowed to perform this action");
		}
	}
}
