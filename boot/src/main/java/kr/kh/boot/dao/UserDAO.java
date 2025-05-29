package kr.kh.boot.dao;

import org.apache.ibatis.annotations.Param;
import kr.kh.boot.model.vo.UserVO;

public interface UserDAO {

	UserVO selectUser(@Param("username") String username);
	void insertUser(UserVO userVO);

}
