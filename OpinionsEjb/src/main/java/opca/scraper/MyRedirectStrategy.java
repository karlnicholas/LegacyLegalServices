package opca.scraper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

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
/*		
		try {
			location = URLDecoder.decode( arg1.getHeaders("Location")[0].getValue(), StandardCharsets.UTF_8.name() );
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
*/		
		return super.getRedirect(arg0, arg1, arg2);
	}

	public String getLocation() {
		return location;
	}
}
