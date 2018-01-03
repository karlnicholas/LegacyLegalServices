package opca.service;

import java.util.Date;

public class ViewParameters {
	public int totalCaseCount;
	public int accountCaseCount;
	public String navbarText;
	public Date sd;
	public Date ed;
	public boolean compressCodeReferences;
	public int levelOfInterest;
	public ViewParameters(Date sd, Date ed, boolean compressCodeReferences, int levelOfInterest) {
		this.sd = sd;
		this.ed = ed;
		this.compressCodeReferences = compressCodeReferences;
		this.levelOfInterest = levelOfInterest;
	}
}

