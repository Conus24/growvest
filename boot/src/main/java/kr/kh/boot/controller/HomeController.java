package kr.kh.boot.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import kr.kh.boot.dao.UserAssetDAO;
import kr.kh.boot.model.form.UserAssetForm;
import kr.kh.boot.model.vo.UserAssetVO;
import kr.kh.boot.model.vo.UserVO;
import kr.kh.boot.service.StockService;
import kr.kh.boot.service.UserService;

@Controller
public class HomeController {

  @Autowired
  // api db 연동
  private StockService stockService;

  @Autowired
  private UserService userService;

  @Autowired
  UserAssetDAO userAssetDAO;

  @GetMapping("/")
  public String home() {
    // api db 연동 => 일 1회
    stockService.fetchAndStorePrice("USD/KRW");
    stockService.fetchAndStorePrice("QQQ");
    stockService.fetchAndStorePrice("VOO");
    stockService.fetchAndStorePrice("GLD");
    return "index";
  }

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
  public String portfolio(Model model) {
    model.addAttribute("userAssetForm", new UserAssetForm());
    return "portfolio";
  }

  @PostMapping("/portfolio/create")
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
      return "portfolio"; // 다시 폼으로
    }

    // 🔸 유효한 자산만 DB에 저장
    for (UserAssetVO asset : assets) {
      userAssetDAO.insertUserAsset(asset);
    }

    return "redirect:/";

  }

}
