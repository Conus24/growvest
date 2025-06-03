package kr.kh.boot.model.vo;

import lombok.Data;

@Data
public class AssetTypeScoreVO {
    private int at_num;
    private int at_as_num;
    private String at_name;
    private float at_mdd;
    private int at_score;
    private long as_won;
}
