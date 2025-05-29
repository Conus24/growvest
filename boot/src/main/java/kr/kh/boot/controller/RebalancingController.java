package kr.kh.boot.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.kh.boot.security.CustomUser;
import kr.kh.boot.service.RebalancingService;

@Controller
public class RebalancingController {

	@Autowired
	private RebalancingService rebalancingService;

	@PostMapping("/rebalancing/submit")
	public String submitRebalancing(
			@AuthenticationPrincipal CustomUser customUser,
			@RequestParam("ta_end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam("cashWon") int cashWon,
			@RequestParam("cashDollar") int cashDollar,
			@RequestParam("deposits") int deposits,
			@RequestParam("bond") int bond,
			@RequestParam("gold") int gold,
			@RequestParam("etf") int etf) {
		int userId = customUser.getUser().getUs_num();

		rebalancingService.insertTarget(userId, endDate, cashWon, cashDollar, deposits, bond, gold, etf);

		return "redirect:/"; // 저장 후 이동할 페이지
	}

}
