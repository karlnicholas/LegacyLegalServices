package statutes;

import java.io.Serializable;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Created with IntelliJ IDEA.
 * User: brad
 * Date: 5/20/12
 * Time: 6:50 PM
 * To change this template use File | Settings | File Templates.
 */
@JsonPropertyOrder({
		"sNumber", "eNumber"
	})
public class StatuteRange implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(StatuteRange.class.getName());

//	private String sectionString;
    private SectionNumber sNumber;
    private SectionNumber eNumber;

/*
    public SectionRange(String sString, String eString) throws CodeException {
        sNumber = new SectionNumber(sString);
        if ( eString != null )
            eNumber = new SectionNumber(eString);
        else
            eNumber = null;
    }
*/
    public StatuteRange() {
    	initEmpty();
    }
    
    public StatuteRange(SectionNumber sNumber, SectionNumber eNumber) {
    	this.sNumber = sNumber;
    	this.eNumber = eNumber;
    }
    
    private void initEmpty() {
    	sNumber = null;
    	eNumber = null;
    }
    
    // can only assume that it is merged in order
    public void mergeRange(StatuteRange statuteRange) {
    	if ( sNumber == null && statuteRange.sNumber != null && statuteRange.sNumber.getSectionNumber() != null ) {
    		sNumber = statuteRange.sNumber;
    	}
    	if ( statuteRange.eNumber != null && statuteRange.eNumber.getSectionNumber() != null ) {
        	eNumber = statuteRange.eNumber;
    	} else if ( statuteRange.sNumber != null && statuteRange.sNumber.getSectionNumber() != null ) {
        	eNumber = statuteRange.sNumber;
    	}
//    	sectionString = new String(sNumber + "-" + eNumber );
    }
	@JsonInclude
	public SectionNumber getsNumber() {
		return sNumber;
	}
	public void setsNumber(SectionNumber sNumber) {
		this.sNumber = sNumber;
	}
	@JsonInclude
	public SectionNumber geteNumber() {
		return eNumber;
	}
	public void seteNumber(SectionNumber eNumber) {
		this.eNumber = eNumber;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(sNumber.toString());
		if ( eNumber != null ) sb.append(" - " + eNumber);
		String ret = sb.toString();
		logger.finer(ret);
		return ret;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eNumber == null) ? 0 : eNumber.hashCode());
		result = prime * result + ((sNumber == null) ? 0 : sNumber.hashCode());
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
		StatuteRange other = (StatuteRange) obj;
		if (eNumber == null) {
			if (other.eNumber != null)
				return false;
		} else if (!eNumber.equals(other.eNumber))
			return false;
		if (sNumber == null) {
			if (other.sNumber != null)
				return false;
		} else if (!sNumber.equals(other.sNumber))
			return false;
		return true;
	}
    
}