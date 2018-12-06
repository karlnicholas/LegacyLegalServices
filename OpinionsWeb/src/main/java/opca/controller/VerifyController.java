package opca.controller;

import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import opca.model.User;
import opca.service.UserService;

@SuppressWarnings("serial")
@Named
@RequestScoped
public class VerifyController implements Serializable {
	@EJB private UserService userService;
	@Inject private FacesContext facesContext;

	private String email;
    private String verifyKey;
    
    public void verify() throws IOException {

    	ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
    	User user = userService.findByEmail(email);
    	Principal p = request.getUserPrincipal() ;
        if ( request.getUserPrincipal() !=  null ) {
        	if ( !p.getName().equalsIgnoreCase(user.getEmail())) {
        		try {
					request.logout();
				} catch (ServletException ignored) {
				}
        	}
        }
    	if ( !user.isVerified() ) {
    		user = userService.verifyUser(email, verifyKey);
    	}
        boolean passwordChanged = false;
        try {
			request.login(user.getEmail(), user.getEmail());
			externalContext.getSessionMap().put("user", user);
		} catch (ServletException ignored) {
			passwordChanged = true; 
		}
        if ( !passwordChanged ) {
	        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "You have been verified, but your password is the same as your email. Please change it now.", "") );
        } else {
	        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "You have been verified.", "") );
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
