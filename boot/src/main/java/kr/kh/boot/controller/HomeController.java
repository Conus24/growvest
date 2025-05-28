package kr.kh.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import kr.kh.boot.service.StockService;

@Controller
public class HomeController {

  @Autowired
  private StockService stockService;

  @GetMapping("/")
  public String home() {
    // 일일 주가 연동
    stockService.fetchAndStorePrice("QQQ");
    stockService.fetchAndStorePrice("USD/KRW");
    stockService.fetchAndStorePrice("VOO");
    return "index";
  }

}
