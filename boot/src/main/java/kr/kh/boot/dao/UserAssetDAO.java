package kr.kh.boot.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import kr.kh.boot.model.vo.UserAssetVO;

public interface UserAssetDAO {

    // 유저 자산 1건 입력
    void insertUserAsset(UserAssetVO asset);

    // 특정 유저의 전체 자산 조회 (선택적으로 사용)
    List<UserAssetVO> selectUserAssetsByUser(@Param("us_num") int us_num);
    
    // won 조회
    List<UserAssetVO> selectUnconvertedAssets(@Param("userId") int userId);
    double selectApiValueByCurrency(@Param("currency") String currency);
    void updateWonValue(@Param("as_num") int as_num, @Param("won") long won);

    // won 총량 표기
    Long selectTotalWonByUser(@Param("userId") int userId);

}
