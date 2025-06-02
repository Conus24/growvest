package kr.kh.boot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.kh.boot.dao.ApiDAO;

@Service
public class ExchangeRateService {

	@Autowired
	private ApiDAO apiDAO;

	// 포트폴리오 출력에 쓸 환율 자동으로 가져오기
	public Double getExchangeRate() {
		return apiDAO.getUsdToKrwExchangeRate();
	}

	public Double getExchangeRateGld() {
		return apiDAO.getGldToKrwExchangeRate();
	}

	public Double getExchangeRateVoo() {
		return apiDAO.getVooToKrwExchangeRate();
	}
}
