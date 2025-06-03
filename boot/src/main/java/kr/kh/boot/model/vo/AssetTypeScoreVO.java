package kr.kh.boot.model.vo;

import lombok.Data;

@Data
public class AssetTypeScoreVO {

    private int at_num;        // 기본키
    private int at_as_num;     // 자산 번호 (foreign key)
    private String at_name;    // 자산 이름
    private float at_mdd;      // MDD 값
    private int at_score;      // 위험 점수
}
