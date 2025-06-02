package kr.kh.boot.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.kh.boot.dao.UserAssetDAO;
import kr.kh.boot.model.form.UserAssetForm;
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

	public void insertUserAsset(UserAssetVO asset) {
		userAssetDAO.insertUserAsset(asset);
	}

	public List<UserAssetVO> getUserAssetsByUser(int userId) {
		return userAssetDAO.selectUserAssetsByUser(userId);
	}

	public boolean createUserAssets(UserAssetForm form, int userId) {
		List<UserAssetVO> assets = new ArrayList<>();

		if (form.getCashWon() > 0) {
			assets.add(new UserAssetVO(userId, "현금 (원)", "KRW", form.getCashWon()));
		}
		if (form.getCashDollar() > 0) {
			assets.add(new UserAssetVO(userId, "현금 (달러)", "USD", form.getCashDollar()));
		}
		if (form.getDeposits() > 0) {
			assets.add(new UserAssetVO(userId, "예적금", "KRW", form.getDeposits()));
		}
		if (form.getBond() > 0) {
			assets.add(new UserAssetVO(userId, "채권", "KRW", form.getBond()));
		}
		if (form.getGold() > 0) {
			assets.add(new UserAssetVO(userId, "금", "KRW", form.getGold()));
		}
		if (form.getEtf() > 0) {
			assets.add(new UserAssetVO(userId, "ETF (달러)", "USD", form.getEtf()));
		}

		if (assets.isEmpty()) {
			return false;
		}

		for (UserAssetVO asset : assets) {
			insertUserAsset(asset); // 기존 메서드 재활용
		}

		return true;
	}

	public Map<String, Object> getPortfolioSummary(int userId, double exchangeRate, double gldRate, double vooRate) {
		List<UserAssetVO> assetList = getUserAssetsByUser(userId);

		long krwTotal = 0;
		long usdTotal = 0;
		long wonValue = 0;
		long total = 0;
		double assetTotalWon = 0.0;

		Map<String, Double> assetTypeWonMap = new LinkedHashMap<>();
		Map<String, Double> assetTypeRawAmountMap = new LinkedHashMap<>();
		Map<String, String> typeCurrencyMap = new LinkedHashMap<>();

		for (UserAssetVO asset : assetList) {
			String type = asset.getAs_asset_type();
			String currency = asset.getAs_currency();
			double amount = asset.getAs_amount();
			double won = amount;

			if ("USD".equals(currency)) {
				usdTotal += amount;
				won *= exchangeRate;
			} else if ("GLD".equals(currency)) {
				won *= gldRate * exchangeRate;
			} else if ("VOO".equals(currency)) {
				won *= vooRate * exchangeRate;
			} else {
				krwTotal += amount;
			}

			assetTypeWonMap.put(type, assetTypeWonMap.getOrDefault(type, 0.0) + won);
			assetTypeRawAmountMap.put(type, assetTypeRawAmountMap.getOrDefault(type, 0.0) + amount);
			typeCurrencyMap.put(type, currency);
			assetTotalWon += won;
		}

		wonValue = (long) (usdTotal * exchangeRate);
		total = krwTotal + wonValue;
		int krwPercent = (int) ((krwTotal * 100.0) / total);
		int usdPercent = 100 - krwPercent;

		List<String> typeLabels = new ArrayList<>();
		List<Double> typeValues = new ArrayList<>();
		List<Double> typePercents = new ArrayList<>();
		List<Double> typeAmounts = new ArrayList<>();
		List<String> typeCurrencies = new ArrayList<>();
		List<Double> typeUnitPrice = new ArrayList<>();

		for (String type : assetTypeWonMap.keySet()) {
			typeLabels.add(type);
			typeValues.add(assetTypeWonMap.get(type));
			typePercents.add((assetTypeWonMap.get(type) / assetTotalWon) * 100.0);
			typeAmounts.add(assetTypeRawAmountMap.get(type));
			typeCurrencies.add(typeCurrencyMap.getOrDefault(type, "KRW"));

			// 💡 여기서 단가 계산해서 넣기
			double totalWon = assetTypeWonMap.get(type);
			double amount = assetTypeRawAmountMap.get(type);
			double unitPrice = (amount > 0) ? totalWon / amount : 0;
			typeUnitPrice.add(unitPrice);
		}

		Map<String, Object> summary = new LinkedHashMap<>();
		summary.put("krwTotal", krwTotal);
		summary.put("usdTotal", usdTotal);
		summary.put("exchangeRate", exchangeRate);
		summary.put("wonValue", wonValue);
		summary.put("krwPercent", krwPercent);
		summary.put("usdPercent", usdPercent);
		summary.put("typeLabels", typeLabels);
		summary.put("typeValues", typeValues);
		summary.put("typePercents", typePercents);
		summary.put("typeAmounts", typeAmounts);
		summary.put("typeCurrencies", typeCurrencies);
		summary.put("typeUnitPrice", typeUnitPrice);

		return summary;
	}

}
