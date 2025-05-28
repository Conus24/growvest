package kr.kh.boot.dao;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ApiDAO {
  // API 자동 등록 //
  // 중복 체크
  int countDataByNameAndDate(@Param("name") String name, @Param("date") String date);
  // 데이터 삽입
  public void insertApiData(@Param("name") String name, 
                            @Param("value") BigDecimal value, 
                            @Param("date") String date,
                            @Param("datetime") String datetime);
  // 최신 USD/KRW 가져오기
  Double getUsdToKrwExchangeRate();
}
