package kr.kh.boot.dao;

import java.util.List;
import java.util.Map;

import kr.kh.boot.model.vo.UserAssetTargetVO;

public interface UserAssetTargetDAO {
    
    // 자산 목표 1건 저장
    void insertTarget(UserAssetTargetVO target);

    // (선택) 유저 ID와 자산 타입으로 기존 목표 조회 (중복 체크용)
    UserAssetTargetVO selectTargetByUserAndType(int userId, String assetType);

    // 목표와 현재의 %차이 계산
    List<Map<String, Object>> selectRebalancingComparison(int userId);

}
