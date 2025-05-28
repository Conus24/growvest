package kr.kh.boot.service;

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
    RestTemplate restTemplate = new RestTemplate();

    String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
        .queryParam("symbol", "QQQ")
        .queryParam("interval", "1day")
        .queryParam("apikey", apiKey)
        .queryParam("outputsize", 1)
        .toUriString();

    Map<String, Object> response = restTemplate.getForObject(url, Map.class);

    if (response != null && response.containsKey("values")) {
      var values = (java.util.List<Map<String, String>>) response.get("values");
      Map<String, String> latest = values.get(0);

      String datetime = latest.get("datetime"); // 예: 2025-05-27
      double close = Double.parseDouble(latest.get("close"));

      // 🔁 중복 확인을 API가 준 날짜 기준으로!
      if (apiDAO.countDataByNameAndDate("QQQ", datetime) > 0) {
        return ""; // 이미 저장되어 있으면 아무것도 안 함
      }

      // 📝 DB에 저장
      apiDAO.insertApiData("QQQ", close, datetime);

      return "📈 시간: " + datetime + ", 종가: " + close;
    } else {
      return "❌ 데이터 호출 실패: " + response;
    }
  }

}
