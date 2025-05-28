package kr.kh.boot.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import kr.kh.boot.model.vo.UserAssetVO;

@Mapper
public interface UserAssetDAO {

    // 유저 자산 1건 입력
    public void insertUserAsset(UserAssetVO asset);

    // 특정 유저의 전체 자산 조회 (선택적으로 사용)
    public List<UserAssetVO> selectUserAssetsByUser(@Param("us_num") int us_num);
}
