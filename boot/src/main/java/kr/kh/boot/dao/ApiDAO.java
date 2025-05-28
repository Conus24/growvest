package kr.kh.boot.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ApiDAO {

		int countDataByNameAndDate(@Param("name") String name, @Param("date") String date);

    public void insertApiData(@Param("name") String name, 
                              @Param("value") double value, 
                              @Param("datetime") String datetime);
}

