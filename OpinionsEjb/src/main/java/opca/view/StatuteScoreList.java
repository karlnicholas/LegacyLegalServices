package opca.view;

import java.util.ArrayList;
import java.util.List;

import opca.model.StatuteKeyEntity;

public class StatuteScoreList {
	private StatuteKeyEntity slipOpinionStatute;
	private int slipOpinionReferCount;
	private List<StatuteScore> statuteScoreList;

	public StatuteScoreList() {
		statuteScoreList = new ArrayList<StatuteScore>();
	}
	
	public StatuteKeyEntity getSlipOpinionStatute() {
		return slipOpinionStatute;
	}
	public void setSlipOpinionStatute(StatuteKeyEntity slipOpinionStatute) {
		this.slipOpinionStatute = slipOpinionStatute;
	}
	public int getSlipOpinionReferCount() {
		return slipOpinionReferCount;
	}
	public void setSlipOpinionReferCount(int slipOpinionReferCount) {
		this.slipOpinionReferCount = slipOpinionReferCount;
	}
	public List<StatuteScore> getStatuteScoreList() {
		return statuteScoreList;
	}
	public void setStatuteScoreList(List<StatuteScore> statuteScoreList) {
		this.statuteScoreList = statuteScoreList;
	}
}
