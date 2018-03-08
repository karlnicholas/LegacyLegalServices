package opca.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
@SuppressWarnings("serial")
public class OpinionStatuteCitationId implements Serializable {
	private StatuteKey statuteKey;
	private OpinionKey opinionKey;
	public StatuteKey getStatuteKey() {
		return statuteKey;
	}
	public void setStatuteKey(StatuteKey statuteKey) {
		this.statuteKey = statuteKey;
	}
	public OpinionKey getOpinionKey() {
		return opinionKey;
	}
	public void setOpinionKey(OpinionKey opinionKey) {
		this.opinionKey = opinionKey;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((opinionKey == null) ? 0 : opinionKey.hashCode());
		result = prime * result + ((statuteKey == null) ? 0 : statuteKey.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OpinionStatuteCitationId other = (OpinionStatuteCitationId) obj;
		if (opinionKey == null) {
			if (other.opinionKey != null)
				return false;
		} else if (!opinionKey.equals(other.opinionKey))
			return false;
		if (statuteKey == null) {
			if (other.statuteKey != null)
				return false;
		} else if (!statuteKey.equals(other.statuteKey))
			return false;
		return true;
	}
}
