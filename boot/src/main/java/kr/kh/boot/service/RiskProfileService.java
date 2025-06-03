package kr.kh.boot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.kh.boot.dao.AssetTypeScoreDAO;
import kr.kh.boot.dao.UserAssetDAO;
import kr.kh.boot.model.vo.AssetTypeScoreVO;
import kr.kh.boot.model.vo.UserAssetVO;

@Service
public class RiskProfileService {

  @Autowired
  private UserAssetDAO userAssetDAO;

  @Autowired
  private AssetTypeScoreDAO assetTypeScoreDAO;

  public double calculatePortfolioRisk(int userId) {
    List<UserAssetVO> assets = userAssetDAO.selectUserAssetsByUser(userId);
    double total = assets.stream().mapToDouble(UserAssetVO::getAs_won).sum();
    double weightedRiskSum = 0;

    for (UserAssetVO asset : assets) {
      AssetTypeScoreVO score = assetTypeScoreDAO.selectScoreByAssetId(asset.getAs_num());
      if (score != null && total > 0) {
        weightedRiskSum += ((double) asset.getAs_won() / total) * score.getAt_mdd();
      }
    }
    return weightedRiskSum;
  }

  // 항목별 보유 원 // 금액 * mdd
  public List<AssetTypeScoreVO> getScoresByUser(int userId) {
    List<AssetTypeScoreVO> list = assetTypeScoreDAO.selectAllScoresByUser(userId);

    for (AssetTypeScoreVO score : list) {
      double mdd = score.getAt_mdd();
      long won = score.getAs_won();
      double loss = Math.round(won * (mdd / 100.0) * 100.0) / 100.0; // 소수점 둘째 자리 반올림
      score.setLossAmount(loss);
    }

    return list;
  }

}
