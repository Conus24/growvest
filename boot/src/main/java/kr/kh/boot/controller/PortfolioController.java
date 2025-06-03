package kr.kh.boot.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import kr.kh.boot.dao.AssetTypeScoreDAO;
import kr.kh.boot.model.form.UserAssetForm;
import kr.kh.boot.model.vo.AssetTypeScoreVO;
import kr.kh.boot.security.CustomUser;
import kr.kh.boot.service.ExchangeRateService;
import kr.kh.boot.service.RiskProfileService;
import kr.kh.boot.service.UserAssetService;
import kr.kh.boot.service.UserService;

@Controller
public class PortfolioController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserAssetService userAssetService;

	@Autowired
	private ExchangeRateService exchangeRateService;

	@Autowired
	private RiskProfileService riskProfileService;

	@Autowired
	private AssetTypeScoreDAO assetTypeScoreDAO;

	@GetMapping("/portfolio")
	public String portfolio(Model model, Principal principal) {
		// 유저 정보
		int userId = userService.getUserNum(principal.getName());
		double exchangeRate = exchangeRateService.getExchangeRate();
		double exchangeRateGld = exchangeRateService.getExchangeRateGld();
		double exchangeRateVoo = exchangeRateService.getExchangeRateVoo();

		// as_won이 0인 자산만 환산해서 업데이트
		userAssetService.updateAssetWonByCurrency(userId, exchangeRate, exchangeRateGld, exchangeRateVoo);

		// 포트폴리오 요약 정보 (총합 등)
		Map<String, Object> summary = userAssetService.getPortfolioSummary(userId, exchangeRate, exchangeRateGld,
				exchangeRateVoo);

		// 총 as_won 합계 가져오기
		long totalWon = userAssetService.getTotalWon(userId);

		model.addAllAttributes(summary);
		model.addAttribute("totalWon", totalWon);
		model.addAttribute("userAssetForm", new UserAssetForm());

		return "portfolio";
	}

	@GetMapping("/portfolio/create")
	public String portfolio(Model model) {
		model.addAttribute("userAssetForm", new UserAssetForm());
		return "portfolio_create";
	}

	@PostMapping("/portfolio/submit")
	public String createPortfolio(@ModelAttribute UserAssetForm form, Principal principal, Model model) {
		int userId = userService.getUserNum(principal.getName());
		boolean result = userAssetService.createUserAssets(form, userId);

		if (!result) {
			model.addAttribute("error", "최소 한 가지 이상의 자산을 입력해주세요.");
			return "portfolio_create";
		}
		return "redirect:/";
	}

	// 투자 성향 분석
	@GetMapping("/portfolio/risk")
	public String risk(Model model, @AuthenticationPrincipal CustomUser customUser) {
		int userId = customUser.getUser().getUs_num();

		// 투자 성향 분석: 가중 평균 리스크 계산
		double portfolioRisk = riskProfileService.calculatePortfolioRisk(userId);
		List<AssetTypeScoreVO> scoreList = assetTypeScoreDAO.selectAllScores();

		// 모델에 데이터 전달
		model.addAttribute("portfolioRisk", portfolioRisk);
		model.addAttribute("userAssetForm", new UserAssetForm());
		model.addAttribute("scoreList", scoreList);

		return "portfolio_risk";
	}

}
