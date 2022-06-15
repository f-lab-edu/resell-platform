package flab.resellPlatform.service.user;

import flab.resellPlatform.domain.user.LoginInfo;
import flab.resellPlatform.domain.user.UserEntity;
import flab.resellPlatform.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    final UserRepository userRepository;
    final ModelMapper modelMapper;

    public Optional<LoginInfo> doLogin(LoginInfo loginInfo) {
        Optional<UserEntity> storedUserInfo = userRepository.findByUsername(loginInfo.getUsername());
        if (storedUserInfo.isEmpty()) {
            return Optional.empty();
        }
        UserEntity storedUserEntityInfo = storedUserInfo.get();
        if (!storedUserEntityInfo.getPassword().strip().equals(loginInfo.getPassword().strip())) {
            return Optional.empty();
        }

        return Optional.of(loginInfo);
    }
}

