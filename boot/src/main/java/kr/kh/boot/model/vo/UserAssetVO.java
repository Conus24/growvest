package kr.kh.boot.model.vo;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserAssetVO {

	private int as_num;
	private int as_us_num;
	private int as_type;
	private String as_asset_type;
	private String as_currency;
	private long as_amount;
	private long as_won;
	private double as_expected_return;
	private LocalDateTime as_created;

	// 원하는 생성자만 따로 작성
	public UserAssetVO(int as_us_num, int as_type, String as_asset_type, String as_currency, long as_amount, long as_won, double as_expected_return) {
		this.as_us_num = as_us_num;
		this.as_type = as_type;
		this.as_asset_type = as_asset_type;
		this.as_currency = as_currency;
		this.as_amount = as_amount;
		this.as_won = as_won;
		this.as_expected_return = as_expected_return;
	}
}
