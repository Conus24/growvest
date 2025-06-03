package kr.kh.boot.dao;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import kr.kh.boot.model.vo.GoalTrackerVO;

@Repository
public class GoalTrackerDAO {

	private static final String NAMESPACE = "GoalTrackerMapper.";

	@Autowired
	private SqlSession sqlSession;

	public void insertGoal(GoalTrackerVO goal) {
		sqlSession.insert(NAMESPACE + "insertGoal", goal);
	}
}
