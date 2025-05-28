package kr.kh.boot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import kr.kh.boot.dao.UserDAO;
import kr.kh.boot.model.vo.UserVO;
import kr.kh.boot.security.CustomUser;

@Service
public class UserDetailService implements UserDetailsService {

	@Autowired
	UserDAO userDao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserVO user = userDao.selectUser(username);
		if (user == null) {
			throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username);
		}
		return new CustomUser(user);
	}

}
