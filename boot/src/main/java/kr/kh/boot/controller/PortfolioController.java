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
import kr.kh.boot.service.ApiService;
import kr.kh.boot.service.StockService;
import kr.kh.boot.service.UserAssetService;
import kr.kh.boot.service.UserService;

@Controller
public class PortfolioController {

	@Autowired
	// api db 연동
	private StockService stockService;

	@Autowired
	private UserService userService;

	@Autowired
	private UserAssetService userAssetService;

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
		boolean result = userAssetService.createUserAssets(form, userId);

		if (!result) {
			model.addAttribute("error", "최소 한 가지 이상의 자산을 입력해주세요.");
			return "portfolio_create";
		}
		return "redirect:/";
	}

	@GetMapping("/")
	public String home(Model model, Principal principal) {
		stockService.fetchAndStorePrice("USD/KRW");
		stockService.fetchAndStorePrice("QQQ");
		stockService.fetchAndStorePrice("VOO");
		stockService.fetchAndStorePrice("GLD");

		if (principal == null)
			return "redirect:/login";

		int userId = userService.getUserNum(principal.getName());
		double exchangeRate = apiService.getExchangeRate();

		Map<String, Object> summary = userAssetService.getPortfolioSummary(userId, exchangeRate);
		model.addAllAttributes(summary);
		model.addAttribute("userAssetForm", new UserAssetForm());

		return "index";
	}

}
