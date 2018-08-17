package opca.controller;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import opca.service.SystemService;
import opca.web.form.AboutForm;

@ManagedBean
public class OptOutController {
	@Inject private FacesContext facesContext;
	@EJB private SystemService systemService;
	private AboutForm aboutForm;
	@PostConstruct
	public void postConstruct() {
		aboutForm = new AboutForm();
	}
	public AboutForm getAboutForm() {
		return aboutForm;
	}
	public void setAboutForm(AboutForm aboutForm) {
		this.aboutForm = aboutForm;
	}
	/**
	 * Send the email and comments to a designated recipient.
	 * @return String navigation to about-thankyou.xhtml
	 */
	public String submit() {
		systemService.sendAboutEmail(aboutForm.getEmail(), aboutForm.getComment(), facesContext.getViewRoot().getLocale());
		return "/views/about/about-thankyou.xhtml";
	}

	public String test() {
		systemService.sendAboutEmail(aboutForm.getEmail(), aboutForm.getComment(), facesContext.getViewRoot().getLocale());
		return "/views/about/about.xhtml";
	}
}
