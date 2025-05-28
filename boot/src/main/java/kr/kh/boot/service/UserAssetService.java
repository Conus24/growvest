package kr.kh.boot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.kh.boot.dao.UserAssetDAO;
import kr.kh.boot.model.vo.UserAssetVO;

@Service
public class UserAssetService {

	@Autowired
	private UserAssetDAO userAssetDAO;

	// KRW와 USD끼리 합계
	public long[] calculateUserKRWUSDSum(int userId) {
		List<UserAssetVO> assets = userAssetDAO.selectUserAssetsByUser(userId);

		long krwSum = assets.stream()
				.filter(a -> "KRW".equals(a.getAs_currency()))
				.mapToLong(UserAssetVO::getAs_amount)
				.sum();

		long usdSum = assets.stream()
				.filter(a -> "USD".equals(a.getAs_currency()))
				.mapToLong(UserAssetVO::getAs_amount)
				.sum();

		return new long[] { krwSum, usdSum };
	}
}
