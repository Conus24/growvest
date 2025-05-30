package kr.kh.boot.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import kr.kh.boot.model.form.UserAssetForm;
import kr.kh.boot.model.vo.UserVO;
import kr.kh.boot.service.ApiService;
import kr.kh.boot.service.StockService;
import kr.kh.boot.service.UserAssetService;
import kr.kh.boot.service.UserService;

@Controller
public class HomeController {

  @Autowired
  // api db 연동
  private StockService stockService;

  @Autowired
  private UserService userService;

  @Autowired
  private ApiService apiService;

  @Autowired
  private UserAssetService userAssetService;


  @GetMapping("/signup")
  public String signup() {
    return "signup";
  }

  @PostMapping("/register")
  public String register(UserVO userVO) {
    userService.register(userVO);
    return "redirect:/login";
  }

  @GetMapping("/portfolio")
  public String portfolio() {
    return "portfolio";
  }

  @GetMapping("/")
  public String home(Model model, Principal principal) {
    stockService.fetchAndStorePrice("USD/KRW");
    stockService.fetchAndStorePrice("QQQ");
    stockService.fetchAndStorePrice("VOO");
    stockService.fetchAndStorePrice("GLD");

    // 유저 정보
    int userId = userService.getUserNum(principal.getName());
    double exchangeRate = apiService.getExchangeRate();

    // 포트폴리오 요약 정보 (총합 등)
    Map<String, Object> summary = userAssetService.getPortfolioSummary(userId, exchangeRate);
    model.addAllAttributes(summary);
    model.addAttribute("userAssetForm", new UserAssetForm());

    return "index";
  }

}
