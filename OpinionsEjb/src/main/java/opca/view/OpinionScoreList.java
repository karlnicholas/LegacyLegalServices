package opca.view;

import java.util.ArrayList;
import java.util.List;

import opca.model.OpinionKey;

public class OpinionScoreList {
	private OpinionKey opinionKey;
	private List<OpinionScore> opinionScoreList;

	public OpinionScoreList() {
		opinionScoreList = new ArrayList<OpinionScore>();
	}
	public OpinionKey getOpinionKey() {
		return opinionKey;
	}
	public void setOpinionKey(OpinionKey opinionKey) {
		this.opinionKey = opinionKey;
	}
	public List<OpinionScore> getOpinionScoreList() {
		return opinionScoreList;
	}
	public void setOpinionScoreList(List<OpinionScore> opinionScoreList) {
		this.opinionScoreList = opinionScoreList;
	}

}
