package opca.view;

import opca.model.StatuteKey;

public class OpinionScore {
	private StatuteKey slipOpinionStatute;
	private int slipOpinionReferCount;
	private int opinionReferCount;
	
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
	public int getOpinionReferCount() {
		return opinionReferCount;
	}
	public void setOpinionReferCount(int opinionReferCount) {
		this.opinionReferCount = opinionReferCount;
	}
}
