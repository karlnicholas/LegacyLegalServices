package opca.service;

import java.util.Date;

public class ViewParameters {
	public int totalCaseCount;
	public int accountCaseCount;
	public String navbarText;
	public Date sd;
	public Date ed;
	public ViewParameters(Date sd, Date ed) {
		this.sd = sd;
		this.ed = ed;
	}
}

