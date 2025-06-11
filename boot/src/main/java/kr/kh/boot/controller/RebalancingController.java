package kr.kh.boot.controller;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.kh.boot.security.CustomUser;
import kr.kh.boot.service.RebalancingService;
import kr.kh.boot.service.UserService;

@Controller
public class RebalancingController {

	@Autowired
	private RebalancingService rebalancingService;

	@Autowired
	private UserService userService;

	@GetMapping("/rebalancing")
	public String rebalancing(Model model, Principal principal) {

		if (principal == null)
			return "redirect:/login";

		// 유저 정보
		int userId = userService.getUserNum(principal.getName());

		// 리밸런싱 비교
		List<Map<String, Object>> comparisonList = rebalancingService.getRebalancingComparison(userId);
		model.addAttribute("comparisonList", comparisonList);
		return "rebalancing";
	}

	@GetMapping("/rebalancing/create")
	public String rebalancing_create() {
		return "rebalancing_create";
	}

	@PostMapping("/rebalancing/submit")
	public String submitRebalancing(
			@AuthenticationPrincipal CustomUser customUser,
			@RequestParam("ta_end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam("cashWon") int cashWon,
			@RequestParam("cashDollar") int cashDollar,
			@RequestParam("deposits") int deposits,
			@RequestParam("bond") int bond,
			@RequestParam("gold") int gold,
			@RequestParam("voo") int voo) {
		int userId = customUser.getUser().getUs_num();
		rebalancingService.insertTarget(userId, endDate, cashWon, cashDollar, deposits, bond, gold, voo);

		return "redirect:/rebalancing";
		// return "rebalancing";
	}

}
