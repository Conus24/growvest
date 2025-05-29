package kr.kh.boot.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import kr.kh.boot.model.vo.UserVO;
import kr.kh.boot.service.UserService;


@Controller
public class HomeController {

  @Autowired
  private UserService userService;

  @GetMapping("/signup")
  public String signup() {
    return "signup";
  }

  @PostMapping("/register")
  public String register(UserVO userVO) {
    userService.register(userVO);
    return "redirect:/login";
  }

  @GetMapping("/rebalancing")
  public String rebalancing() {
      return "rebalancing";
  }
  

}
