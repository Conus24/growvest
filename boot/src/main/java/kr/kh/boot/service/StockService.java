package kr.kh.boot.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import kr.kh.boot.dao.ApiDAO;

@Service
public class StockService {

  @Value("${twelvedata.api.key}")
  private String apiKey;

  private static final String BASE_URL = "https://api.twelvedata.com/time_series";

  @Autowired
  private ApiDAO apiDAO;

  public String getQQQPrice() {
    // 오늘 날짜
    String today = LocalDate.now().toString(); // "2025-05-28"

    // 이미 저장된 기록이 있다면 → API 호출하지 않고 종료
    if (apiDAO.countDataByNameAndDate("QQQ", today) > 0) {
      return ""; // 또는 null
    }

    // API 호출 시작
    RestTemplate restTemplate = new RestTemplate();

    String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
        .queryParam("symbol", "QQQ")
        .queryParam("interval", "1day")
        .queryParam("apikey", apiKey)
        .queryParam("outputsize", 1)
        .toUriString();

    Map<String, Object> response = restTemplate.getForObject(url, Map.class);

    if (response != null && response.containsKey("values")) {
      var values = (List<Map<String, String>>) response.get("values");
      Map<String, String> latest = values.get(0);

      String datetime = latest.get("datetime");
      double close = Double.parseDouble(latest.get("close"));

      // 데이터 저장
      apiDAO.insertApiData("QQQ", close, datetime);

      return "📈 저장 완료: " + datetime + ", 종가: " + close;
    } else {
      return "❌ 데이터 호출 실패";
    }
  }

}
