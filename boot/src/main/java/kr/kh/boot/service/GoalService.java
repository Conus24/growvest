package kr.kh.boot.service;

import java.util.List;
import org.springframework.stereotype.Service;
import kr.kh.boot.model.vo.UserAssetVO;
import kr.kh.boot.model.dto.GoalSimulationResult;

@Service
public class GoalService {

	public GoalSimulationResult simulateYearsToReachGoal(List<UserAssetVO> assets, long goalAmount) {
		int years = 0;

		while (true) {
			long totalAssets = assets.stream()
					.mapToLong(UserAssetVO::getAs_won)
					.sum();

			if (totalAssets >= goalAmount) {
				return new GoalSimulationResult(years, totalAssets); // 결과 리턴
			}

			for (UserAssetVO asset : assets) {
				double expectedReturn = asset.getAs_expected_return(); // ex. 1.025
				long updatedValue = Math.round(asset.getAs_won() * expectedReturn);
				asset.setAs_won(updatedValue); // 자산의 현재가치를 갱신
			}

			years++;
		}
	}
}
