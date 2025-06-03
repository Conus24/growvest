package kr.kh.boot.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import kr.kh.boot.model.form.UserAssetForm;
import kr.kh.boot.service.ExchangeRateService;
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

	
	@GetMapping("/portfolio")
  public String portfolio(Model model, Principal principal) {
    // 유저 정보
    int userId = userService.getUserNum(principal.getName());
    double exchangeRate = exchangeRateService.getExchangeRate();
		double exchangeRateGld = exchangeRateService.getExchangeRateGld();
		double exchangeRateVoo = exchangeRateService.getExchangeRateVoo();

    // 포트폴리오 요약 정보 (총합 등)
    Map<String, Object> summary = userAssetService.getPortfolioSummary(userId, exchangeRate, exchangeRateGld, exchangeRateVoo);

		// as_won이 0인 자산만 환산해서 업데이트
  	userAssetService.updateAssetWonByCurrency(userId, exchangeRate, exchangeRateGld, exchangeRateVoo);

    model.addAllAttributes(summary);
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
	public String risk(Model model) {
		model.addAttribute("userAssetForm", new UserAssetForm());
		return "portfolio_risk";
	}

}
