package kr.kh.boot.model.vo;

import lombok.Data;

@Data
public class UserAssetTargetVO {
	private int ta_num;
	private int ta_us_num;
	private String ta_asset_type;
	private String ta_currency;
	private long ta_amount;
	private String ta_end_date;
}
