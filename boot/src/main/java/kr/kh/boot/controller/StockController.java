package kr.kh.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.kh.boot.service.StockService;

@RestController
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping("/qqq")
    public String getQQQPrice() {
        return stockService.getQQQPrice();
    }
}
