package kr.kh.boot.service;

import java.net.URI;
import java.net.http.*;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class MarketService {

	@Value("${twelvedata.api.key}")
	private String apiKey;

	private static final String BASE_URL = "https://api.twelvedata.com/time_series";

	public Map<String, Object> getSPXMarketSummary() {
		Map<String, Object> result = new HashMap<>();

		try {
			String url = BASE_URL + "?symbol=SPY&interval=1day&outputsize=30&apikey=" + apiKey;

			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(url))
					.build();

			HttpClient client = HttpClient.newHttpClient();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			String body = response.body();
			System.out.println("✅ API 응답:\n" + body);

			JSONObject json = new JSONObject(body);

			// 에러 응답 처리
			if (json.has("status") && json.getString("status").equals("error")) {
				System.out.println("❌ API 오류: " + json.getString("message"));
				return result;
			}

			JSONArray values = json.getJSONArray("values");

			if (values.length() == 0) {
				System.out.println("❌ 데이터 없음 (values empty)");
				return result;
			}

			List<String> dates = new ArrayList<>();
			List<Double> closes = new ArrayList<>();

			for (int i = values.length() - 1; i >= 0; i--) {
				JSONObject day = values.getJSONObject(i);
				dates.add(day.getString("datetime"));
				closes.add(Double.parseDouble(day.getString("close")));
			}

			double start = closes.get(0);
			double end = closes.get(closes.size() - 1);

			if (start != 0) {
				double percent = ((end - start) / start) * 100;
				result.put("percentChange", percent);
				System.out.println("📈 상승률: " + percent + "%");
			} else {
				System.out.println("❌ 시작가가 0이라 상승률 계산 불가");
			}

			result.put("dates", dates);
			result.put("closes", closes);

		} catch (Exception e) {
			System.out.println("❌ 예외 발생");
			e.printStackTrace();
		}
		return result;
	}
}
