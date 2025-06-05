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

	public GoalSimulationResult simulateYearsToReachGoal(List<UserAssetVO> assets, long goalAmount) {
		int years = 0;
		final int MAX_YEARS = 100;

		while (true) {
			// 1. 자산 총합 계산
			long totalAssets = assets.stream()
					.mapToLong(UserAssetVO::getAs_won)
					.sum();

			// 2. 목표 도달 시 종료
			if (totalAssets >= goalAmount) {
				return new GoalSimulationResult(years, totalAssets);
			}

			// 3. 연도 제한: 100년 넘으면 강제 종료
			if (years >= MAX_YEARS) {
				throw new RuntimeException("100년 이내로 목표 자산 달성 불가");
			}

			// 4. 자산 갱신
			for (UserAssetVO asset : assets) {
				double expectedReturn = asset.getAs_expected_return();
				long updatedValue = Math.round(asset.getAs_won() * expectedReturn);
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
