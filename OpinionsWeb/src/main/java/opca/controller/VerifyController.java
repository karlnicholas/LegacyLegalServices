package opca.controller;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import opca.model.User;
import opca.service.UserService;

@ManagedBean
@SuppressWarnings("serial")
public class VerifyController implements Serializable {
	@Inject private UserService userService;
	@Inject private FacesContext facesContext;

	private String email;
    private String verifyKey;
    
    public void verify() throws IOException {
    	User user = userService.verifyUser(email, verifyKey);
    	if ( user != null ) {
	        ExternalContext externalContext = facesContext.getExternalContext();
	        externalContext.getSessionMap().put("user", user);
	     }
    }

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getVerifyKey() {
		return verifyKey;
	}

	public void setVerifyKey(String verifyKey) {
		this.verifyKey = verifyKey;
	}
}
