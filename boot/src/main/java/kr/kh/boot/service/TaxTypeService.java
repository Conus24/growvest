package kr.kh.boot.service;

import org.springframework.stereotype.Service;
import kr.kh.boot.model.form.GoalForm;

@Service
public class TaxTypeService {

	public String generateTaxType(GoalForm form) {
		StringBuilder taxType = new StringBuilder();

		// 예적금
		if (form.getSavingsTax().contains("15.4")) {
			taxType.append("SAVINGS_TAX_154");
		} else {
			taxType.append("SAVINGS_NON");
		}

		// 주식
		switch (form.getStockTaxOption()) {
			case "22":
				taxType.append(",STOCK_TAX_22");
				break;
			case "ISA_BASIC":
				taxType.append(",STOCK_ISA_BASIC");
				break;
			case "ISA_PREFERENTIAL":
				taxType.append(",STOCK_ISA_PREFERENTIAL");
				break;
		}

		// 선택 옵션
		if (form.isStockTax250()) {
			taxType.append(",STOCK_250");
		}

		if (form.isRealMoney()) {
			taxType.append(",CPI");
		}

		return taxType.toString();
	}
}
