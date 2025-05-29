package kr.kh.boot.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.kh.boot.dao.UserAssetTargetDAO;
import kr.kh.boot.model.vo.UserAssetTargetVO;


@Service
public class RebalancingService {

	@Autowired
	private UserAssetTargetDAO userAssetTargetDAO;
	// 목표 % DB에 데이터 넣기
	public void insertTarget(int userId, LocalDate endDate, float cash, float dollar, float deposits, float bond, float gold, float etf) {
		userAssetTargetDAO.insertTarget(new UserAssetTargetVO(0, userId, 1, cash, endDate.toString()));
		userAssetTargetDAO.insertTarget(new UserAssetTargetVO(0, userId, 2, dollar, endDate.toString()));
		userAssetTargetDAO.insertTarget(new UserAssetTargetVO(0, userId, 3, deposits, endDate.toString()));
		userAssetTargetDAO.insertTarget(new UserAssetTargetVO(0, userId, 4, bond, endDate.toString()));
		userAssetTargetDAO.insertTarget(new UserAssetTargetVO(0, userId, 5, gold, endDate.toString()));
		userAssetTargetDAO.insertTarget(new UserAssetTargetVO(0, userId, 6, etf, endDate.toString()));
	}

	// 목표와 현재의 %차이 계산
	public List<Map<String, Object>> getRebalancingComparison(int userId) {
		return userAssetTargetDAO.selectRebalancingComparison(userId);
	}
}
