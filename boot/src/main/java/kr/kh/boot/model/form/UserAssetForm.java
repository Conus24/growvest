package kr.kh.boot.model.form;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserAssetForm {
    private long cashWon;
    private long cashDollar;
    private long deposits;
    private long bond;
    private long gold;
    private long voo;
}
