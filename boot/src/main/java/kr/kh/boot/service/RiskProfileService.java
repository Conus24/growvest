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

  // 포트폴리오 손실률 측정
  public double calculateMaxPortfolioLossRate(int userId) {
    List<AssetTypeScoreVO> list = getScoresByUser(userId); // 이 안에서 lossAmount 계산됨

    long total = 0;
    double totalLoss = 0;

    for (AssetTypeScoreVO score : list) {
      total += score.getAs_won();
      totalLoss += score.getLossAmount();
    }

    if (total == 0)
      return 0.0;

    return Math.round((totalLoss / total) * 10000.0) / 100.0; // 소수점 둘째 자리까지 반올림
  }

  public String getRiskGrade(double lossRate) {
    if (lossRate <= 5.0)
      return "초안정형";
    else if (lossRate <= 15.0)
      return "안정형";
    else if (lossRate <= 30.0)
      return "중립형";
    else if (lossRate <= 50.0)
      return "위험추구형";
    else
      return "공격형";
  }

}
