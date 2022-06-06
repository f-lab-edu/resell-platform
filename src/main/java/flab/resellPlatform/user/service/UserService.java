package flab.resellPlatform.user.service;

import flab.resellPlatform.user.domain.UserDTO;

public interface UserService {
    String join(UserDTO userInfo);
    String findId(UserDTO userInfo);
    String findPassword(UserDTO userInfo);
}
