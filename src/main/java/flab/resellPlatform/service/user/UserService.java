package flab.resellPlatform.service.user;

import flab.resellPlatform.domain.user.UserDTO;

public interface UserService {
    String join(UserDTO userInfo);
    String findId(UserDTO userInfo);
    String findPassword(UserDTO userInfo);
}
