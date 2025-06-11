package kr.kh.boot.controller;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import kr.kh.boot.model.vo.UserVO;
import kr.kh.boot.service.MarketService;
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
  private MarketService marketService;

  @GetMapping("/")
  public String home(Model model) {
    stockService.fetchAndStorePrice("USD/KRW");
    stockService.fetchAndStorePrice("QQQ");
    stockService.fetchAndStorePrice("VOO");
    stockService.fetchAndStorePrice("GLD");
    stockService.fetchAndStorePrice("SPY");

    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
    LocalTime time = now.toLocalTime();
    DayOfWeek day = now.getDayOfWeek();

    // 국내시장: 평일 09:00 ~ 15:30
    boolean isKoreaMarketOpen = (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) &&
        !time.isBefore(LocalTime.of(9, 0)) &&
        time.isBefore(LocalTime.of(15, 30));

    boolean isUSMarketOpen = (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY) &&
        (time.isAfter(LocalTime.of(23, 29)) || time.isBefore(LocalTime.of(6, 0)));

    // 장시간 체크
    model.addAttribute("isKoreaMarketOpen", isKoreaMarketOpen);
    model.addAttribute("isUSMarketOpen", isUSMarketOpen);

    Map<String, Double> priceMap = stockService.getLatestApiPriceMap();
    model.addAttribute("priceMap", priceMap);

    // SPY 차트용 데이터
    Map<String, Object> market = marketService.getSPYMarketSummary();
    model.addAttribute("dates", market.get("dates"));
    model.addAttribute("closes", market.get("closes"));
    model.addAttribute("percentChange", market.get("percentChange"));

    // USD/KRW 차트용 데이터 (분리된 키 이름으로)
    Map<String, Object> usdkrwMarket = marketService.getUSDKRWMarketSummary();
    model.addAttribute("usdDates", usdkrwMarket.get("dates"));
    model.addAttribute("usdCloses", usdkrwMarket.get("closes"));
    model.addAttribute("usdChange", usdkrwMarket.get("percentChange"));

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

}
