package opca.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import opca.model.User;
import opca.service.SystemService;
import opca.service.UserService;
import opca.web.util.WebResources;

@SuppressWarnings("serial")
@Named
@RequestScoped
public class AccountsController implements Serializable {
    @Inject protected FacesContext facesContext;
    @EJB protected UserService userService;
    @EJB protected SystemService systemService;
//    @Inject private UserCodesController userCodesController;
    
    public static final String NAV_ACCOUNTS_REDIRECT = "/views/accounts/accounts.xhtml?faces-redirect=true";
    public static final String NAV_GET_PASSWORD_REDIRECT = "/views/accounts/checkpassword.xhtml?faces-redirect=true";
    public static final String NAV_ACCOUNTS = "/views/accounts/accounts.xhtml";

    private User newUser;
    private User currentUser;

	private String passwordConfirmation;
	
    private String email;
    private String password;
    
    private Map<String, Boolean> checkboxItems;

    @PostConstruct
    public void postConstruct() {
        //
    	currentUser = getCurrentUser(facesContext, userService);
    	// used for registering new users
        newUser = new User();

    	checkboxItems = new HashMap<>();
        if ( currentUser != null && currentUser.getTitles() != null ) {
        	for ( String c: currentUser.getTitles()) {
        		checkboxItems.put(c, Boolean.TRUE);	
        	}
        }
    }

    public synchronized static User getCurrentUser(FacesContext facesContext, UserService userService) {
    	ExternalContext externalContext = facesContext.getExternalContext();
    	// This insures the current user is available as a field.
    	User currentUser = (User)externalContext.getSessionMap().get("user");
        // in case went to a specific URL
        if ( currentUser == null ) {
            HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
            java.security.Principal principal = request.getUserPrincipal();
            if ( principal != null ) {
                try {
                    currentUser = userService.findByEmail(principal.getName());
                } catch (Exception ignored) {
                    // logout whoever and set user to null.
                    try {
                        ((HttpServletRequest) externalContext.getRequest()).logout();
                    } catch (ServletException alsoIgnored) {}
                    externalContext.invalidateSession();
                    currentUser = null;
                }
            }
        }
    	return currentUser;
    }
	public Map<String, Boolean> getCheckboxItems() {
		return checkboxItems;
	}

	public void setCheckboxItems(Map<String, Boolean> checkboxItems) {
		this.checkboxItems = checkboxItems;
	}

	/**
     * True if a user is logged in.
     * 
     * @return True if Logged In
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * True if a user has the ADMIN role.
     * 
     * @return True if ADMIN
     */
    public boolean isAdmin() {
        if ( currentUser == null ) return false;
        return currentUser.isAdmin();
    }

    /**
     * True if a user has the ADMIN role.
     * 
     * @return True if ADMIN
     */
    public boolean isVerified() {
        if ( currentUser == null ) return false;
        return currentUser.isVerified();
    }

    /**
     * Login a user using the email and password fields.
     * 
     * @return Navigation to /views/account.xhtml
     */
    public String login() {
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        if ( request.getUserPrincipal() == null ) {
            try {
                request.login(email, email);
                currentUser = userService.findByEmail(email);
                externalContext.getSessionMap().put("user", currentUser);
            } catch (ServletException ignored) {
                // Handle unknown username/password in request.login().
                facesContext.addMessage(null, new FacesMessage("Login Failed!", ""));
                return null;
            }
        } 
        return NAV_ACCOUNTS_REDIRECT;
    }

    /**
     * Set the user session using the email but no password.
     * 
     * @return Navigation to /views/account.xhtml
     */
    public String signIn() {
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        if ( request.getUserPrincipal() == null ) {
            try {
            	// check here if a password has been set.
                User testUser = userService.checkUserByEmail(email);
                if ( testUser != null && testUser.isVerified() ) {
                	HttpSession session = request.getSession();
                	session.setAttribute("email", email);
                	return NAV_GET_PASSWORD_REDIRECT;                	
                }
                
                request.login(email, email);
                currentUser = userService.findByEmail(email);
                externalContext.getSessionMap().put("user", currentUser);
            } catch (ServletException ignored) {
                // Handle unknown username/password in request.login().
                facesContext.addMessage(null, new FacesMessage("Signin Failed!", ""));
                return null;
            }
        }
        return NAV_ACCOUNTS_REDIRECT;
    }

    /**
     * Set the user session using the email but no password.
     * 
     * @return Navigation to /views/account.xhtml
     */
    public String checkPassword() {
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        if ( request.getUserPrincipal() == null ) {
            try {
            	HttpSession session = request.getSession();
            	email = (String) session.getAttribute("email");
                request.login(email, password);
            	session.removeAttribute("email");
                currentUser = userService.findByEmail(email);
                externalContext.getSessionMap().put("user", currentUser);
            } catch (ServletException ignored) {
                // Handle unknown username/password in request.login().
                facesContext.addMessage(null, new FacesMessage("Signin Failed!", ""));
                return null;
            }
        }
        return NAV_ACCOUNTS_REDIRECT;
    }
    /**
     * Email used for login 
     * @return Email for Login.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Email Field
     * @param email for login.
     */
    public void setEmail(String email) {
        this.email = email;
    }

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
    /**
     * Logout current user
     * 
     * @return Naviation to /views/account.xhtml
     */
    public String signOut() {
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        try {
            request.logout();
            externalContext.invalidateSession();
        } catch (ServletException e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Logout Failed!", WebResources.getRootErrorMessage(e) ));
            return null;
        }
        // navigate
        return NAV_ACCOUNTS_REDIRECT;
    }

    /**
     * optout current user
     * 
     * @return Naviation to /views/account.xhtml
     */
    public String setOptout(boolean optout) {
        try {
        	if ( optout ) {
        		userService.setOptOut(currentUser);
        	} else {
        		userService.clearOptOut(currentUser);
        	}
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Update failed", WebResources.getRootErrorMessage(e)));
            return null;
        }
        // message and navigation
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Opt-" + (optout?"Out":"In") + " Succeeded", ""));
        
        return NAV_ACCOUNTS;
    }
    /**
     * Test to see if current user opted out
     * @return true if opted out
     */
    public boolean isOptout() {
        if ( currentUser == null ) return false;
        return currentUser.isOptout();
    }
    /**
     * Currently logged user or null.
     *  
     * @return User
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Check user.password against passwordConfirmation, encode password, and merge user.
     * @return String navigation to /views/account.xhtml
     */
    public String updatePassword() {
        try {
            // check password confirmation.
            if ( !currentUser.getPassword().equals(passwordConfirmation) ) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Passwords Must Match", ""));
                return null;
            }
            if ( !currentUser.isVerified() ) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Please verify your email to set your password.", ""));
                return null;
            }
            // update user
            // userService.merge(userService.updatePassword(currentUser));
            userService.updatePassword(currentUser);
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Update failed", WebResources.getRootErrorMessage(e)));
            return null;
        }
        // message and navigation
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Password Updated", ""));
        return NAV_ACCOUNTS;
    }

    /**
     * Password Confirmation Field
     * @return passwordConfirmation
     */
    public String getPasswordConfirmation() {
        return passwordConfirmation;
    }

    /**
     * Password Confirmation Field
     * @param passwordConfirmation to set.
     */
    public void setPasswordConfirmation(String passwordConfirmation) {
        this.passwordConfirmation = passwordConfirmation;
    }

    /**
     * Register a new user
     * 
     * @return String navigation to /views/account.xhtml
     */
    public String register() {
        try {
            // save the password before encoding
        	newUser.setPassword(newUser.getEmail());
            String password = newUser.getPassword();
        	
            currentUser = userService.encodeAndSave(newUser);
            if ( currentUser == null ) {
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration failed!", "User Already Exists" ));
                return null;
            } else {
                // login user            
                ExternalContext externalContext = facesContext.getExternalContext();
                HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
                request.login(currentUser.getEmail(), password);
                externalContext.getSessionMap().put("user", currentUser); 
            }
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Registration failed!", WebResources.getRootErrorMessage(e) ));
            return null;
        }

        // navigate
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Registration Successful!", "" ));
        return NAV_ACCOUNTS;
    }

    /**
     * Register a new user
     * 
     * @return String navigation to /views/account.xhtml
     */
    public String startVerify() {
        try {
        	systemService.startVerify(currentUser);
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Update failed", WebResources.getRootErrorMessage(e)));
            return null;
        }
        // navigate
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Verification Process Started!", "" ));
        return null;
    }

    /**
     * New User to be registered
     * @return newUser
     */
    public User getNewUser() {
        return newUser;
    }
    /**
     * New User to be registered
     * @param newUser to be registered.
     */
    public void setNewUser(User newUser) {
        this.newUser = newUser;
    }

    /**
     * Update/merge user fields
     * @return Navigation to /views/account.xhtml
     */
    public String updateDetails() {
        try {
        	List<String> listCodes = new ArrayList<String>();
        	for ( Entry<String, Boolean> entry: checkboxItems.entrySet() ) {
        		if ( entry.getValue() ) listCodes.add( entry.getKey() );
        	}
		    currentUser.setTitles(listCodes.toArray(new String[listCodes.size()]));
            userService.merge(currentUser);
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Update unsuccessful", WebResources.getRootErrorMessage(e)));
            return null;
        }
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "User Info Updated", ""));
        return NAV_ACCOUNTS;
    }

	public boolean isValid(String clientId) {
        UIComponent comp = FacesContext.getCurrentInstance().
                    getViewRoot().findComponent(clientId);
        if(comp instanceof UIInput) {
            return ((UIInput)comp).isValid();
        }
        throw new IllegalAccessError();
    }

	/**
     * Return a list of users
     * @return List of all users.
     */
    public List<User> getUsers() {
        return userService.findAll();
    }

    /**
     * Remove a user based on id.
     * @param id of user to be removed.
     */
    public void removeUser(Long id) {
        if ( currentUser.getId() == id ) throw new RuntimeException("Cannot change current user!");
        userService.delete(id);
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "User removed", "") );
    }

    /**
     * Remove a user based on id.
     * @param id of user to be removed.
     */
    public void unverify(Long id) {
        if ( currentUser.getId() == id ) throw new RuntimeException("Cannot change current user!");
        userService.unverify(id);
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "User unverified", "") );
    }

    /**
     * Promote user to administrator based on id by adding "ADMIN" role to user.
     * @param id of user to be promoted
     */
    public void promoteUser(Long id) {
        if ( currentUser.getId() == id ) return;
        userService.promoteUser(id);
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "User promoted to administrator", "") );
    }

    /**
     * Demote a user, based on id, by removing ADMIN role.
     * @param id of user to demote.
     */
    public void demoteUser(Long id) {
        if ( currentUser.getId() == id ) return;
        userService.demoteUser(id);
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "User demoted to user only", "") );
    }
}
