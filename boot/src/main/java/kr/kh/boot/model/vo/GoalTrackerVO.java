package kr.kh.boot.model.vo;

import java.time.LocalDate;
import lombok.Data;

@Data
public class GoalTrackerVO {

	private int go_num;
	private int go_us_num;
	private long go_target_won;
	private long go_current_won;
	private LocalDate go_start_date;
	private LocalDate go_end_date;
	private String go_tax_type;
	private String go_state;
}
