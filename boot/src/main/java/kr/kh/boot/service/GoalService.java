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

	public GoalSimulationResult simulateYearsToReachGoal(List<UserAssetVO> assets, long goalAmount, double savingsTaxRate) {
		int years = 0;
		final int MAX_YEARS = 100;

		while (true) {
			long totalAssets = assets.stream().mapToLong(UserAssetVO::getAs_won).sum();

			if (totalAssets >= goalAmount) {
				return new GoalSimulationResult(years, totalAssets);
			}

			if (years >= MAX_YEARS) {
				throw new RuntimeException("100년 이내로 목표 자산 달성 불가");
			}

			for (UserAssetVO asset : assets) {
				double expectedReturn = asset.getAs_expected_return();
				long current = asset.getAs_won();
				long updatedValue = Math.round(current * expectedReturn);
				String type = asset.getAs_asset_type();
				long profit = updatedValue - current;

				// === 세금 적용 로직 ===
				if ("예적금".equals(type) && savingsTaxRate > 0) {
					// 사용자가 선택한 세금율 적용
					long taxedProfit = Math.round(profit * (1 - savingsTaxRate / 100.0));
					updatedValue = current + taxedProfit;
				} else if ("채권".equals(type)) {
					// 채권은 무조건 15.4% 과세
					long taxedProfit = Math.round(profit * (1 - 0.154));
					updatedValue = current + taxedProfit;
				}
				// =====================

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
