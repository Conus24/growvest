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

	public GoalSimulationResult simulateYearsToReachGoal(
			List<UserAssetVO> assets,
			long goalAmount,
			double savingsTaxRate,
			String stockTaxOption) {

		int years = 0;
		final int MAX_YEARS = 100;
		long initialAmount = assets.stream().mapToLong(UserAssetVO::getAs_won).sum();

		while (true) {
			System.out.println("==== " + (years + 1) + "년차 시뮬레이션 시작 ====");

			long totalAssets = 0;

			for (UserAssetVO asset : assets) {
				double expectedReturn = asset.getAs_expected_return();
				long current = asset.getAs_won();
				String type = asset.getAs_asset_type();
				long updatedValue = Math.round(current * expectedReturn);
				long profit = updatedValue - current;

				System.out.printf("[%s] 예상 수익률: %.4f%n", type, expectedReturn);

				// === 과세 로직 ===
				if ("예적금".equals(type) && savingsTaxRate > 0) {
					long taxedProfit = Math.round(profit * (1 - savingsTaxRate / 100.0));
					updatedValue = current + taxedProfit;
					System.out.printf("[예적금] %d → %d (세후 수익률 적용)%n", current, updatedValue);

				} else if ("채권".equals(type)) {
					long taxedProfit = Math.round(profit * (1 - 0.154));
					updatedValue = current + taxedProfit;
					System.out.printf("[채권] %d → %d (세후 15.4%%)%n", current, updatedValue);

				} else if ("금".equals(type) || "S&P 500".equals(type)) {
					if ("22".equals(stockTaxOption)) {
						long taxedProfit = Math.round(profit * (1 - 0.22));
						updatedValue = current + taxedProfit;
						System.out.printf("[양도소득세 22%%] %d → %d (세후)%n", current, updatedValue);

					} else if ("ISA_BASIC".equals(stockTaxOption)) {
						long taxFreeAmount = Math.min(profit, 2_000_000);
						long taxableAmount = Math.max(0, profit - taxFreeAmount);
						long tax = Math.round(taxableAmount * 0.099);
						updatedValue = current + (profit - tax);

						System.out.printf("[ISA 일반형] 세전 수익: %d, 비과세: %d, 과세: %d → 세금: %d → 최종 자산: %d%n",
								profit, taxFreeAmount, taxableAmount, tax, updatedValue);

					} else if ("ISA_PREFERENTIAL".equals(stockTaxOption)) {
						// 추후 서민형 ISA 400만 원 처리 시 여기에 로직 추가
					}
				}

				asset.setAs_won(updatedValue);
				totalAssets += updatedValue;
			}

			System.out.printf("➡ 누적 자산: %d / 목표: %d%n", totalAssets, goalAmount);

			if (totalAssets >= goalAmount) {
				double actualReturnRate = (double) (totalAssets - initialAmount) / initialAmount * 100;
				System.out.printf("✅ 시뮬레이션 종료: 총 %d년 소요, 수익률: %.4f%%%n", years + 1, actualReturnRate);
				return new GoalSimulationResult(years + 1, totalAssets, actualReturnRate);
			}

			if (years >= MAX_YEARS) {
				throw new RuntimeException("100년 이내로 목표 자산 달성 불가");
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
