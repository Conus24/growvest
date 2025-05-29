package kr.kh.boot.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.kh.boot.dao.UserAssetTargetDAO;
import kr.kh.boot.model.vo.UserAssetTargetVO;


@Service
public class RebalancingService {

	@Autowired
	private UserAssetTargetDAO userAssetTargetDAO;

	public void insertTarget(int userId, LocalDate endDate, float cash, float dollar, float deposits, float bond, float gold, float etf) {
		userAssetTargetDAO.insertTarget(new UserAssetTargetVO(0, userId, 1, cash, endDate.toString()));
		userAssetTargetDAO.insertTarget(new UserAssetTargetVO(0, userId, 2, dollar, endDate.toString()));
		userAssetTargetDAO.insertTarget(new UserAssetTargetVO(0, userId, 3, deposits, endDate.toString()));
		userAssetTargetDAO.insertTarget(new UserAssetTargetVO(0, userId, 4, bond, endDate.toString()));
		userAssetTargetDAO.insertTarget(new UserAssetTargetVO(0, userId, 5, gold, endDate.toString()));
		userAssetTargetDAO.insertTarget(new UserAssetTargetVO(0, userId, 6, etf, endDate.toString()));
	}
}
