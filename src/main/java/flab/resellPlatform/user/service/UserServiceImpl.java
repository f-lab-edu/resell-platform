package flab.resellPlatform.user.service;

import flab.resellPlatform.user.User;
import flab.resellPlatform.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Override
    public String join(User userInfo) {
        System.out.println("in UserServiceImpl");
        userRepository.save(userInfo);
        return null;
    }

    @Override
    public String findId(User userInfo) {
        return null;
    }

    @Override
    public String findPassword(User userInfo) {
        return null;
    }
}
