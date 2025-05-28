package kr.kh.boot.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import kr.kh.boot.dao.UserDAO;
import kr.kh.boot.model.vo.UserVO;

@Service
public class UserService {

	@Autowired
	UserDAO userDAO;

	@Autowired
	PasswordEncoder passwordEncoder;

	public void register(UserVO userVO) {
		userVO.setUs_pw(passwordEncoder.encode(userVO.getUs_pw()));
		userVO.setUs_authority("USER");
		userDAO.insertUser(userVO);
	}

	public boolean isDuplicate(String us_id) {
		return userDAO.selectUser(us_id) != null;
	}

	public int getUserNum(String username) {
    UserVO user = userDAO.selectUser(username);
    return user != null ? user.getUs_num() : 0; // or 예외 던지기
	}

}
