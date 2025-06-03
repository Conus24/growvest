package kr.kh.boot.model.vo;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserAssetVO {

	private int as_num;
	private int as_us_num;
	private String as_asset_type;
	private String as_currency;
	private long as_amount;
	private long as_won;
	private LocalDateTime as_created;

}
