package opca.model;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
@SuppressWarnings("serial")
public class OpinionStatuteCitationId implements Serializable {
	private Integer statuteId;
	private Integer opinionId;
	public Integer getStatuteId() {
		return statuteId;
	}
	public void setStatuteId(Integer statuteId) {
		this.statuteId = statuteId;
	}
	public Integer getOpinionId() {
		return opinionId;
	}
	public void setOpinionId(Integer opinionId) {
		this.opinionId = opinionId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((opinionId == null) ? 0 : opinionId.hashCode());
		result = prime * result + ((statuteId == null) ? 0 : statuteId.hashCode());
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
		if (opinionId == null) {
			if (other.opinionId != null)
				return false;
		} else if (!opinionId.equals(other.opinionId))
			return false;
		if (statuteId == null) {
			if (other.statuteId != null)
				return false;
		} else if (!statuteId.equals(other.statuteId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return 	"statuteId=" + statuteId + ", opinionId=" + opinionId;
	}
}
