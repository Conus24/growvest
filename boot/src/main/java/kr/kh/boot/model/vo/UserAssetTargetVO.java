package kr.kh.boot.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserAssetTargetVO {
	private int ta_num;
	private int ta_us_num;
	private int ta_as_num;
	private float ta_target_percent;
	private String ta_end_date;
}
