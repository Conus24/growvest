package kr.kh.boot.model.vo;

import java.time.LocalDateTime;

public class UserAssetVO {

	private int as_num;
	private int as_us_num;
	private String as_asset_type;
	private String as_currency;
	private long as_amount;
	private LocalDateTime as_created;

	// 기본 생성자
	public UserAssetVO() {}

	// 원하는 생성자 추가
	public UserAssetVO(int as_us_num, String as_asset_type, String as_currency, long as_amount) {
		this.as_us_num = as_us_num;
		this.as_asset_type = as_asset_type;
		this.as_currency = as_currency;
		this.as_amount = as_amount;
	}
}
