package kr.kh.boot.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import kr.kh.boot.dao.UserAssetDAO;
import kr.kh.boot.model.form.UserAssetForm;
import kr.kh.boot.model.vo.UserAssetVO;
import kr.kh.boot.service.ApiService;
import kr.kh.boot.service.UserAssetService;
import kr.kh.boot.service.UserService;

@Controller
public class PortfolioController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserAssetService userAssetService;

	@Autowired
	UserAssetDAO userAssetDAO;

	@Autowired
	private ApiService apiService;

	@GetMapping("/portfolio/create")
	public String portfolio(Model model) {
		model.addAttribute("userAssetForm", new UserAssetForm());
		return "portfolio_create";
	}

	@PostMapping("/portfolio/submit")
	public String createPortfolio(@ModelAttribute UserAssetForm form, Principal principal, Model model) {
		int userId = userService.getUserNum(principal.getName());
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

		// 🔸 어떤 자산도 입력되지 않았을 경우 → 오류 메시지 처리
		if (assets.isEmpty()) {
			model.addAttribute("error", "최소 한 가지 이상의 자산을 입력해주세요.");
			return "portfolio";
		}

		// 🔸 유효한 자산만 DB에 저장
		for (UserAssetVO asset : assets) {
			userAssetDAO.insertUserAsset(asset);
		}
		return "redirect:/";
	}

	@GetMapping("/portfolio")
	public String summary(Model model, Principal principal) {
		if (principal == null)
			return "redirect:/login";

		int userId = userService.getUserNum(principal.getName());
		List<UserAssetVO> assetList = userAssetDAO.selectUserAssetsByUser(userId);
		double exchangeRate = apiService.getExchangeRate();

		long krwTotal = 0;
		long usdTotal = 0;
		long wonValue = 0;
		long total = 0;

		Map<String, Double> assetTypeWonMap = new LinkedHashMap<>(); // 환산 금액
		Map<String, Double> assetTypeRawAmountMap = new LinkedHashMap<>(); // 원본 금액
		double assetTotalWon = 0.0;

		for (UserAssetVO asset : assetList) {
			String type = asset.getAs_asset_type(); // 예: "현금 (원)", "채권"
			String currency = asset.getAs_currency();
			double amount = asset.getAs_amount(); // 원본 금액
			double won = amount;

			if ("USD".equals(currency)) {
				usdTotal += amount;
				won *= exchangeRate;
			} else {
				krwTotal += amount;
			}

			// 환산 금액 저장
			assetTypeWonMap.put(type, assetTypeWonMap.getOrDefault(type, 0.0) + won);
			// 원본 금액 저장 (툴팁용)
			assetTypeRawAmountMap.put(type, assetTypeRawAmountMap.getOrDefault(type, 0.0) + amount);
			assetTotalWon += won;
		}

		wonValue = (long) (usdTotal * exchangeRate);
		total = krwTotal + wonValue;
		int krwPercent = (int) ((krwTotal * 100.0) / total);
		int usdPercent = 100 - krwPercent;

		// 📊 차트용 리스트 생성
		List<String> typeLabels = new ArrayList<>();
		List<Double> typeValues = new ArrayList<>(); // 환산 금액 (도넛 value)
		List<Double> typePercents = new ArrayList<>(); // 퍼센트 (중앙 표시용)
		List<Double> typeAmounts = new ArrayList<>(); // 원본 금액 (툴팁용)

		for (Map.Entry<String, Double> entry : assetTypeWonMap.entrySet()) {
			String type = entry.getKey();
			double won = entry.getValue();
			double percent = (won / assetTotalWon) * 100.0;
			double raw = assetTypeRawAmountMap.get(type);

			typeLabels.add(type);
			typeValues.add(won);
			typePercents.add(percent);
			typeAmounts.add(raw);
		}

		// 📦 모델 전달
		model.addAttribute("krwTotal", krwTotal);
		model.addAttribute("usdTotal", usdTotal);
		model.addAttribute("exchangeRate", exchangeRate);
		model.addAttribute("wonValue", wonValue);
		model.addAttribute("krwPercent", krwPercent);
		model.addAttribute("usdPercent", usdPercent);

		model.addAttribute("typeLabels", typeLabels); // 차트 항목
		model.addAttribute("typeValues", typeValues); // 환산 금액 (도넛)
		model.addAttribute("typePercents", typePercents); // 퍼센트 (내부)
		model.addAttribute("typeAmounts", typeAmounts); // 원본 금액 (툴팁)

		model.addAttribute("userAssetForm", new UserAssetForm());
		return "portfolio";
	}

}
