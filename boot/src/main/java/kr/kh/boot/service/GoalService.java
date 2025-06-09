package kr.kh.boot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import kr.kh.boot.model.vo.UserAssetVO;
import kr.kh.boot.dao.UserAssetDAO;
import kr.kh.boot.model.dto.GoalSimulationResult;

@Service
public class GoalService {

	@Autowired
	private UserAssetDAO userAssetDAO;

	public GoalSimulationResult simulateYearsToReachGoal(List<UserAssetVO> assets, long goalAmount, double savingsTaxRate, String stockTaxOption) {
	int years = 0;
	final int MAX_YEARS = 100;
	long initialAmount = assets.stream().mapToLong(UserAssetVO::getAs_won).sum();

	while (true) {
		long totalAssets = assets.stream().mapToLong(UserAssetVO::getAs_won).sum();

		if (totalAssets >= goalAmount) {
			double actualReturnRate = (double)(totalAssets - initialAmount) / initialAmount * 100;
			return new GoalSimulationResult(years, totalAssets, actualReturnRate);
		}

		if (years >= MAX_YEARS) {
			throw new RuntimeException("100년 이내로 목표 자산 달성 불가");
		}

		for (UserAssetVO asset : assets) {
			double expectedReturn = asset.getAs_expected_return();
			long current = asset.getAs_won();
			String type = asset.getAs_asset_type();
			long updatedValue = Math.round(current * expectedReturn);
			long profit = updatedValue - current;

			// 예적금 과세
			if ("예적금".equals(type) && savingsTaxRate > 0) {
				long taxedProfit = Math.round(profit * (1 - savingsTaxRate / 100.0));
				updatedValue = current + taxedProfit;

			// 채권 과세
			} else if ("채권".equals(type)) {
				long taxedProfit = Math.round(profit * (1 - 0.154));
				updatedValue = current + taxedProfit;

			// 금 & S&P500: 주식과세 22%
			} else if (("금".equals(type) || "S&P 500".equals(type)) && "22".equals(stockTaxOption)) {
				long taxedProfit = Math.round(profit * (1 - 0.22));
				updatedValue = current + taxedProfit;

			// ISA 계좌는 나중에 처리
			} else if (("금".equals(type) || "S&P 500".equals(type)) && stockTaxOption.startsWith("ISA")) {
				// 향후 ISA 로직 구현 예정
			}

			asset.setAs_won(updatedValue);
		}

		years++;
	}
}


	public double calculateExpectedReturn(int userId) {
		List<UserAssetVO> assets = userAssetDAO.selectUserAssetsByUser(userId);
		double total = 0;
		long totalWon = 0;

		for (UserAssetVO asset : assets) {
			if (asset.getAs_expected_return() > 0 && asset.getAs_won() > 0) {
				total += asset.getAs_expected_return() * asset.getAs_won();
				totalWon += asset.getAs_won();
			}
		}

		return totalWon == 0 ? 0.0 : total / totalWon;
	}

}
