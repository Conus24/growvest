package kr.kh.boot.model.form;

import lombok.Data;
import java.time.LocalDate;

@Data
public class GoalForm {

	private long goalAsset; // 목표 금액
	private long monthlyInvestment; // 월 납입액
	private String savingsTax; // 예적금 과세 방식 (15.4 / 0)
	private String stockTaxOption; // 주식 과세 방식 (22 / isa / 기타)
	private boolean stockTax250; // 250만 원 공제 여부 (선택)
	private boolean realMoney; // 인플레이션 적용 여부 (선택)
	private LocalDate targetEndDate; // 목표 종료일

	private String taxType;

}
