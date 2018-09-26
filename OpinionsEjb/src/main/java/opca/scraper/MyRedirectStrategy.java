package opca.scraper;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.HttpContext;

public class MyRedirectStrategy extends LaxRedirectStrategy {
	private String location;	

	@Override
	public HttpUriRequest getRedirect(HttpRequest arg0, HttpResponse arg1, HttpContext arg2) throws ProtocolException {
		location = arg1.getHeaders("Location")[0].getValue();
		return super.getRedirect(arg0, arg1, arg2);
	}

	public String getLocation() {
		return location;
	}
}
