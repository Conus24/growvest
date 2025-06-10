package kr.kh.boot.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.kh.boot.model.vo.ApiVO;

public interface ApiDAO {
  // API 자동 등록, 중복 체크
  int countDataByNameAndDate(@Param("name") String name, @Param("date") String date);
  // 데이터 삽입
  void insertApiData(@Param("name") String name, @Param("value") BigDecimal value, @Param("date") String date,@Param("datetime") String datetime);
  // 최신 USD/KRW 가져오기
  Double getUsdToKrwExchangeRate();
  Double getGldToKrwExchangeRate();
  Double getVooToKrwExchangeRate();

  // 홈에 지수 연동
  List<ApiVO> selectLatestApiValues();

}
