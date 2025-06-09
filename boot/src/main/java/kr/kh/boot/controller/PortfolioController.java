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
		int userId = userService.getUserNum(principal.getName());
		double exchangeRate = exchangeRateService.getExchangeRate();
		double exchangeRateGld = exchangeRateService.getExchangeRateGld();
		double exchangeRateVoo = exchangeRateService.getExchangeRateVoo();

		userAssetService.updateAssetWonByCurrency(userId, exchangeRate, exchangeRateGld, exchangeRateVoo);

		Map<String, Object> summary = userAssetService.getPortfolioSummary(userId, exchangeRate, exchangeRateGld, exchangeRateVoo);
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

	@GetMapping("/portfolio/risk")
	public String risk(Model model, @AuthenticationPrincipal CustomUser customUser) {
		int userId = customUser.getUser().getUs_num();
		double portfolioRisk = riskProfileService.calculatePortfolioRisk(userId);
		List<AssetTypeScoreVO> scoreList = riskProfileService.getScoresByUser(userId);
		double lossRate = riskProfileService.calculateMaxPortfolioLossRate(userId);
		String profileType = riskProfileService.getRiskGrade(lossRate);

		model.addAttribute("portfolioRisk", portfolioRisk);
		model.addAttribute("userAssetForm", new UserAssetForm());
		model.addAttribute("scoreList", scoreList);
		model.addAttribute("lossRate", lossRate);
		model.addAttribute("profileType", profileType);

		return "portfolio_risk";
	}

	@GetMapping("/portfolio/goal")
	public String traker(Model model, Principal principal) {
		int userId = userService.getUserNum(principal.getName());
		long totalWon = userAssetService.getTotalWon(userId);

		model.addAttribute("userAssetForm", new UserAssetForm());
		model.addAttribute("totalWon", totalWon);

		return "portfolio_goal";
	}

	@PostMapping("/portfolio/goal/submit")
	public String submitGoal(@ModelAttribute GoalForm form, Principal principal, Model model) {
		int userId = userService.getUserNum(principal.getName());
		long totalWon = userAssetService.getTotalWon(userId);

		GoalTrackerVO goal = new GoalTrackerVO();
		goal.setGo_us_num(userId);
		goal.setGo_target_won(form.getGoalAsset());
		goal.setGo_current_won(totalWon);
		goal.setGo_start_date(LocalDate.now());
		goal.setGo_end_date(form.getTargetEndDate());
		String taxType = taxTypeService.generateTaxType(form);
		goal.setGo_tax_type(taxType);
		goal.setGo_state("진행중");

		goalTrackerDAO.insertGoal(goal);

		String taxStr = form.getSavingsTax();
		double savingsTaxRate = 0.0;
		if ("15.4".equals(taxStr)) {
			savingsTaxRate = 15.4;
		}

		long goalAmount = form.getGoalAsset();
		List<UserAssetVO> assets = userAssetService.getUserAssetsByUser(userId);
		GoalSimulationResult result = goalService.simulateYearsToReachGoal(
				assets, goalAmount, savingsTaxRate,
				form.getStockTaxOption(), form.isStockTax250(), form.isRealMoney());

		double expectedReturn = goalService.calculateExpectedReturn(userId);
		model.addAttribute("expectedReturn", expectedReturn);

		double actualReturnRate = (double) (result.getFinalAmount() - totalWon) / totalWon * 100;
		model.addAttribute("expectedReturnRateAfterTax", actualReturnRate);

		long estimatedTax = result.getTotalTax();
		model.addAttribute("calculatedTax", estimatedTax);

		model.addAttribute("totalWon", totalWon);
		model.addAttribute("goal", form.getGoalAsset());
		model.addAttribute("years", result.getYears());
		model.addAttribute("finalAmount", result.getFinalAmount());
		model.addAttribute("yearlyAssets", result.getYearlyAssets());

		return "portfolio_goal_result";
	}

}
