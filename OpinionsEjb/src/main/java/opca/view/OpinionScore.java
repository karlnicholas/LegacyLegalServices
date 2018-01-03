package opca.view;

import opca.model.StatuteKeyEntity;

public class OpinionScore {
	private StatuteKeyEntity slipOpinionStatute;
	private int slipOpinionReferCount;
	private int opinionReferCount;
	
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
	public int getOpinionReferCount() {
		return opinionReferCount;
	}
	public void setOpinionReferCount(int opinionReferCount) {
		this.opinionReferCount = opinionReferCount;
	}
}
