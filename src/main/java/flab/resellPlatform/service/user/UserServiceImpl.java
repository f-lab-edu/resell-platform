package flab.resellPlatform.service.user;

import flab.resellPlatform.common.utils.UserUtils;
import flab.resellPlatform.domain.user.*;
import flab.resellPlatform.exception.user.PhoneNumberNotFoundException;
import flab.resellPlatform.exception.user.UserInfoNotFoundException;
import flab.resellPlatform.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.util.RandomValueStringGenerator;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RandomValueStringGenerator randomValueStringGenerator;
    private final MessageSourceAccessor messageSourceAccessor;

    @Override
    public UserDTO createUser(UserDTO userInfo) {
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        userInfo.setPhoneNumber(UserUtils.normalizePhoneNumber(userInfo.getPhoneNumber()));

        UserEntity userEntity = modelMapper.map(userInfo, UserEntity.class);
        userRepository.save(userEntity);
        return userInfo;
    }

    @Override
    public String findUsername(String phoneNumber) {
        String normalizedPhoneNumber = UserUtils.normalizePhoneNumber(phoneNumber);

        Optional<String> result = userRepository.findUsername(normalizedPhoneNumber);

        if (result.isEmpty()) {
            throw new PhoneNumberNotFoundException();
        }

        return result.get();
    }

    @Override
    public int updatePassword(LoginInfo loginInfo) {
        String encodedPassword = passwordEncoder.encode(loginInfo.getPassword());
        loginInfo.setPassword(encodedPassword);

        return userRepository.updatePassword(loginInfo);
    }

    @Override
    public String updatePassword(StrictLoginInfo strictLoginInfo) {
        strictLoginInfo.setPhoneNumber(UserUtils.normalizePhoneNumber(strictLoginInfo.getPhoneNumber()));

        // 임시 비밀번호 생성
        String randomGeneratedPassword = randomValueStringGenerator.generate();
        String encodedPassword = passwordEncoder.encode(randomGeneratedPassword);
        strictLoginInfo.setPassword(encodedPassword);

        int updatedCnt = userRepository.updatePassword(strictLoginInfo);
        if (updatedCnt == 0) {
            throw new UserInfoNotFoundException(messageSourceAccessor.getMessage("user.userInfo.notFound"));
        }

        return randomGeneratedPassword;
    }
}
