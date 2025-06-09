package kr.kh.boot.controller;

import java.security.Principal;
import java.time.LocalDate;
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
import kr.kh.boot.dao.GoalTrackerDAO;
import kr.kh.boot.model.dto.GoalSimulationResult;
import kr.kh.boot.model.form.GoalForm;
import kr.kh.boot.model.form.UserAssetForm;
import kr.kh.boot.model.vo.AssetTypeScoreVO;
import kr.kh.boot.model.vo.GoalTrackerVO;
import kr.kh.boot.model.vo.UserAssetVO;
import kr.kh.boot.security.CustomUser;
import kr.kh.boot.service.ExchangeRateService;
import kr.kh.boot.service.GoalService;
import kr.kh.boot.service.RiskProfileService;
import kr.kh.boot.service.TaxTypeService;
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

	@Autowired
	private GoalTrackerDAO goalTrackerDAO;

	@Autowired
	private TaxTypeService taxTypeService;

	@Autowired
	private GoalService goalService;

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
		// List<AssetTypeScoreVO> scoreList =
		// assetTypeScoreDAO.selectAllScoresByUser(userId);
		List<AssetTypeScoreVO> scoreList = riskProfileService.getScoresByUser(userId);

		// 포트 최대 손실률 측정
		double lossRate = riskProfileService.calculateMaxPortfolioLossRate(userId);

		// 투자 성향 분류
		String profileType = riskProfileService.getRiskGrade(lossRate);

		// 모델에 데이터 전달
		model.addAttribute("portfolioRisk", portfolioRisk);
		model.addAttribute("userAssetForm", new UserAssetForm());
		model.addAttribute("scoreList", scoreList);
		model.addAttribute("lossRate", lossRate);
		model.addAttribute("profileType", profileType);

		return "portfolio_risk";
	}

	// 목표 금액 설정
	@GetMapping("/portfolio/goal")
	public String traker(Model model, Principal principal) {
		// 유저 정보
		int userId = userService.getUserNum(principal.getName());
		// 현재 자산 총합
		long totalWon = userAssetService.getTotalWon(userId);

		model.addAttribute("userAssetForm", new UserAssetForm());
		model.addAttribute("totalWon", totalWon);

		return "portfolio_goal";
	}

	@PostMapping("/portfolio/goal/submit")
	public String submitGoal(@ModelAttribute GoalForm form, Principal principal, Model model) {
		int userId = userService.getUserNum(principal.getName());
		long totalWon = userAssetService.getTotalWon(userId); // 현재 자산

		GoalTrackerVO goal = new GoalTrackerVO();
		goal.setGo_us_num(userId);
		goal.setGo_target_won(form.getGoalAsset());
		goal.setGo_current_won(totalWon);
		goal.setGo_start_date(LocalDate.now());
		goal.setGo_end_date(form.getTargetEndDate());

		// 세금 타입 문자열 생성
		String taxType = taxTypeService.generateTaxType(form);
		goal.setGo_tax_type(taxType);
		goal.setGo_state("진행중");

		goalTrackerDAO.insertGoal(goal);

		// 세금 계산
		// 1. savingsTax는 String 형태로 넘어옴: "15.4" 또는 "0"
		String taxStr = form.getSavingsTax(); // 예: "15.4"

		// 2. Double로 파싱
		double savingsTaxRate = 0.0;
		if ("15.4".equals(taxStr)) {
			savingsTaxRate = 15.4;
		}

		// ==== 시뮬레이션 계산 ====
		long goalAmount = form.getGoalAsset();
		List<UserAssetVO> assets = userAssetService.getUserAssetsByUser(userId);
		GoalSimulationResult result = goalService.simulateYearsToReachGoal(assets, goalAmount, savingsTaxRate,
				form.getStockTaxOption(), form.isStockTax250(), form.isRealMoney());

		// 기대 수익률 계산해서 전달
		double expectedReturn = goalService.calculateExpectedReturn(userId);
		model.addAttribute("expectedReturn", expectedReturn);

		// 세후 수익률 및 세금 계산 추가
		double actualReturnRate = (double) (result.getFinalAmount() - totalWon) / totalWon * 100;
		model.addAttribute("expectedReturnRateAfterTax", actualReturnRate);

		long estimatedTax = Math.round((totalWon * (expectedReturn - 1.0)) - (result.getFinalAmount() - totalWon));
		model.addAttribute("calculatedTax", estimatedTax);

		// ==== 결과 화면으로 전송 ====
		model.addAttribute("totalWon", totalWon);
		model.addAttribute("goal", form.getGoalAsset());
		model.addAttribute("years", result.getYears());
		model.addAttribute("finalAmount", result.getFinalAmount());
		model.addAttribute("yearlyAssets", result.getYearlyAssets());
		model.addAttribute("cumulativeProfits", result.getCumulativeProfits());
		model.addAttribute("yearlyProfits", result.getYearlyProfits());

		return "portfolio_goal_result";
	}

}