package opca.view;

import java.util.ArrayList;
import java.util.List;

import opca.model.StatuteKey;

public class StatuteScoreList {
	private StatuteKey slipOpinionStatute;
	private int slipOpinionReferCount;
	private List<StatuteScore> statuteScoreList;

	public StatuteScoreList() {
		statuteScoreList = new ArrayList<StatuteScore>();
	}
	
	public StatuteKey getSlipOpinionStatute() {
		return slipOpinionStatute;
	}
	public void setSlipOpinionStatute(StatuteKey slipOpinionStatute) {
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
