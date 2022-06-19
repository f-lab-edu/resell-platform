package flab.resellPlatform.service.user;

import flab.resellPlatform.domain.user.LoginInfo;
import flab.resellPlatform.domain.user.PasswordInquiryForm;
import flab.resellPlatform.domain.user.StrictLoginInfo;
import flab.resellPlatform.domain.user.UserDTO;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Optional;

public interface UserService {

    /**
    * 회원가입
    *
    * @param userInfo 회원가입에 필요한 정보
    * @return Optional UserDTO.
    * @exception SQLIntegrityConstraintViolationException Unique 정보가 이미 존재함.
    *
    * @작성일 6/19/2022
    * @작성자 minsuk
    */
    Optional<UserDTO> createUser(UserDTO userInfo);

    /**
    * 사용자 아이디 찾기
    *
    * @param phoneNumber 핸드폰 번호
    * @return Optional String. param으로 username을 찾지 못하면 Optional.empty() 반환.
    * @exception 
    *
    * @작성일 6/19/2022
    * @작성자 minsuk
    */
    Optional<String> findUsername(String phoneNumber);

    /**
    * 아이디와 비밀번호로 비밀번호를 변경함.
    *
    * @param loginInfo 로그인에 필요한 정보.
    * @return int 성공시 1보다 같거나 큰 값 반환, 실패시 0 반환.
    * @exception
    *
    * @작성일 6/19/2022
    * @작성자 minsuk
    */
    int updatePassword(LoginInfo loginInfo);

    /**
     * 아이디 비밀번호 외에 사용자를 검증할 수 있는 추가적인 데이터들을 이용하여 비밀번호를 변경함.
     *
     * @param strictLoginInfo 비밀번호 찾기에 필요한 정보.
     * @return int 성공시 1보다 같거나 큰 값 반환, 실패시 0 반환.
     * @exception
     *
     * @작성일 6/19/2022
     * @작성자 minsuk
     */
    int updatePassword(StrictLoginInfo strictLoginInfo);
}
