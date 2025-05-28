package kr.kh.boot.model.vo;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class UserVO {
	int us_num;
	String us_id;
	String us_pw;
  String us_nickname;
	String us_authority;

}
