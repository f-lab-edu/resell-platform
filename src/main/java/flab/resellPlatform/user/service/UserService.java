package flab.resellPlatform.user.service;

import flab.resellPlatform.user.User;

public interface UserService {
    String join(User userInfo);
    String findId(User userInfo);
    String findPassword(User userInfo);
}
