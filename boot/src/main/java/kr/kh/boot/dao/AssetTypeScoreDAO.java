package kr.kh.boot.dao;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.kh.boot.model.vo.AssetTypeScoreVO;

@Repository
public class AssetTypeScoreDAO {

	private static final String NAMESPACE = "AssetTypeScoreMapper.";

	@Autowired
	private SqlSession sqlSession;

	public AssetTypeScoreVO selectScoreByAssetId(int assetId) {
		return sqlSession.selectOne(NAMESPACE + "selectScoreByAssetId", assetId);
	}

	// 리스크 모두 보기
	public List<AssetTypeScoreVO> selectAllScores() {
		return sqlSession.selectList(NAMESPACE + "selectAllScores");
	}

	// 항목별 보유 원 보기
	public List<AssetTypeScoreVO> selectAllScoresByUser(int userId) {
		return sqlSession.selectList(NAMESPACE + "selectAllScores", userId);
	}

}
