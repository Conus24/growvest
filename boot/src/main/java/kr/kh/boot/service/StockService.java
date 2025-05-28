package kr.kh.boot.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class StockService {

		@Value("${twelvedata.api.key}")
    private String apiKey;
		
    private static final String BASE_URL = "https://api.twelvedata.com/time_series";

    public String getQQQPrice() {
        RestTemplate restTemplate = new RestTemplate();

        String url = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("symbol", "QQQ")
                .queryParam("interval", "1min")
                .queryParam("apikey", apiKey)
                .queryParam("outputsize", 1)
                .toUriString();

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response != null && response.containsKey("values")) {
            var values = (java.util.List<Map<String, String>>) response.get("values");
            Map<String, String> latest = values.get(0);
            return "📈 시간: " + latest.get("datetime") + ", 종가: " + latest.get("close");
        } else {
            return "❌ 데이터 호출 실패: " + response;
        }
    }
}
