package kr.kh.boot.service;

import java.math.BigDecimal;
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

  public void fetchAndStorePrice(String symbol) {
    String today = LocalDate.now().toString();

    if (apiDAO.countDataByNameAndDate(symbol, today) > 0) {
      return;
    }

    RestTemplate restTemplate = new RestTemplate();

    String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
        .queryParam("symbol", symbol)
        .queryParam("interval", "1day")
        .queryParam("apikey", apiKey)
        .queryParam("outputsize", 1)
        .toUriString();

    Map<String, Object> response = restTemplate.getForObject(url, Map.class);

    if (response != null && response.containsKey("values")) {
      var values = (List<Map<String, String>>) response.get("values");
      Map<String, String> latest = values.get(0);

      String datetime = latest.get("datetime");
      String date = datetime.substring(0, 10);
      String closeStr = latest.get("close");
      BigDecimal close = new BigDecimal(closeStr); // ← 문자열로 바로 생성하면 정밀도 손실 없음
      apiDAO.insertApiData(symbol, close, date, datetime);
    }
  }

}
