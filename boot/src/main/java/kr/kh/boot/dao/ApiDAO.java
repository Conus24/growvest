package kr.kh.boot.dao;

import java.math.BigDecimal;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ApiDAO {

		int countDataByNameAndDate(@Param("name") String name, @Param("date") String date);

    public void insertApiData(@Param("name") String name, 
                              @Param("value") BigDecimal value, 
                              @Param("date") String date,
                              @Param("datetime") String datetime);
}

