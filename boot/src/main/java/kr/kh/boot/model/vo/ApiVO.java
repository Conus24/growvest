package kr.kh.boot.model.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ApiVO {
	private int api_num;                
    private String api_name;          
    private double api_value;         
    private LocalDate api_date;       
    private LocalDateTime api_datetime;
}
