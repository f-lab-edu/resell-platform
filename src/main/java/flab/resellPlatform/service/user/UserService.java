package flab.resellPlatform.service.user;

import flab.resellPlatform.domain.user.PasswordInquiryForm;
import flab.resellPlatform.domain.user.UserDTO;

import java.util.Optional;

public interface UserService {
    Optional<UserDTO> createUser(UserDTO userInfo);
}
