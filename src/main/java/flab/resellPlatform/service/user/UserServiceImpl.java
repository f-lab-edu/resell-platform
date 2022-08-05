package flab.resellPlatform.service.user;

import flab.resellPlatform.common.utils.UserUtils;
import flab.resellPlatform.domain.user.*;
import flab.resellPlatform.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Override
    public Optional<UserDTO> createUser(UserDTO userInfo) {
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        userInfo.setPhoneNumber(UserUtils.normalizePhoneNumber(userInfo.getPhoneNumber()));

        UserEntity userEntity = modelMapper.map(userInfo, UserEntity.class);
        userRepository.save(userEntity);
        return Optional.of(userInfo);
    }

    @Override
    public Optional<String> findUsername(String phoneNumber) {
        String normalizedPhoneNumber = UserUtils.normalizePhoneNumber(phoneNumber);
        return userRepository.findUsername(normalizedPhoneNumber);
    }

    @Override
    public int updatePassword(LoginInfo loginInfo) {
        String encodedPassword = passwordEncoder.encode(loginInfo.getPassword());
        loginInfo.setPassword(encodedPassword);

        return userRepository.updatePassword(loginInfo);
    }

    @Override
    public Optional<String> updatePassword(StrictLoginInfo strictLoginInfo) {
        strictLoginInfo.setPhoneNumber(UserUtils.normalizePhoneNumber(strictLoginInfo.getPhoneNumber()));

        // 임시 비밀번호 생성
        String randomGeneratedPassword = randomValueStringGenerator.generate();
        String encodedPassword = passwordEncoder.encode(randomGeneratedPassword);
        strictLoginInfo.setPassword(encodedPassword);

        int updatedCnt = userRepository.updatePassword(strictLoginInfo);
        if (updatedCnt == 0) {
            return Optional.empty();
        }
        return Optional.of(randomGeneratedPassword);
    }
}
