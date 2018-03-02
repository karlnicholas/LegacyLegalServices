package opca.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@SuppressWarnings("serial")
@Table(uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Entity
@NamedQueries({
    @NamedQuery(name = User.FIND_BY_EMAIL, query = "select u from User u where u.email = :email"), 
    @NamedQuery(name = User.COUNT_EMAIL, query = "select Count(u.email) from User u where u.email = :email"), 
    @NamedQuery(name = User.FIND_ALL, query = "select u from User u"), 
    @NamedQuery(name = User.USER_COUNT, query = "select count(u) from User u"),
	@NamedQuery(name = User.FIND_UNVERIFIED, query = "select u from User u where u.verified = false and u.verifyErrors <= 3 and u.verifyCount <= 5"), 
	@NamedQuery(name = User.FIND_UNWELCOMED, query = "select u from User u where u.welcomed = false and u.welcomeErrors <= 3"), 
})
public class User implements Serializable {
    public static final String FIND_BY_EMAIL = "User.findByEmail";
    public static final String COUNT_EMAIL = "User.countEmail";
    public static final String FIND_ALL = "User.findAll";
    public static final String USER_COUNT = "User.userCount";
	public static final String FIND_UNVERIFIED = "User.findUnverified";
	public static final String FIND_UNWELCOMED = "User.findUnwelcomed";

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @NotNull(message="{email.required}")
    @Pattern(regexp = "[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\."
            + "[A-Za-z0-9!#$%&'*+/=?^_`{|}~-]+)*@"
            + "(?:[A-Za-z0-9](?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+[A-Za-z0-9]"
            + "(?:[A-Za-z0-9-]*[A-Za-z0-9])?",
            message = "{invalid.email}")
    private String email;

    private String password;

    private String firstName;
    private String lastName;
	private boolean emailUpdates;
	private boolean verified;
    private String verifyKey;
    private int verifyErrors;
    private int verifyCount;
    //
	private boolean welcomed;
    private int welcomeErrors;
    private String optoutKey;
	private boolean optout;
    //
    @Temporal(TemporalType.TIMESTAMP)
	private Date updateDate;
    @Temporal(TemporalType.TIMESTAMP)
	private Date createDate;
    private Locale locale;
	private String[] titles;
    
    @ManyToMany(fetch=FetchType.EAGER)
    private List<Role> roles;
    
    public User() {
		init();
    }

    /**
     * Create account constructor 
     * 
     * @param email
     * @param emailUpdates
     * @param password
     * @param role
     */
	public User(String email, boolean emailUpdates, String password, Locale locale) {
		init();
		this.email = email;
		this.emailUpdates = emailUpdates;
		this.password = password;
		this.locale = locale;
	}
	
	private void init() {
		this.verified = false;
		this.verifyKey = UUID.randomUUID().toString();
		this.createDate = new Date();
		
		Calendar firstDay = Calendar.getInstance();
		int year = firstDay.get(Calendar.YEAR);
		int dayOfYear = firstDay.get(Calendar.DAY_OF_YEAR);
		dayOfYear = dayOfYear - 4;
		if ( dayOfYear < 1 ) {
			year = year - 1;
			dayOfYear = 365 + dayOfYear;
		}
		firstDay.set(Calendar.YEAR, year);
		firstDay.set(Calendar.DAY_OF_YEAR, dayOfYear);
		this.updateDate = firstDay.getTime();
		
		this.verifyErrors = 0;
	}

	/**
     * Get Users E-Mail
     * @return Users E-Mail
     */
    public String getEmail() {
        return email;
    }
    /**
     * Set User's E-Mail
     * @param email to set.
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * Get User's Password
     * @return User's Password
     */
    public String getPassword() {
        return password;
    }
    /**
     * Set User's Password
     * @param password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * Get Users's First Name
     * 
     * @return First Name
     */
    public String getFirstName() {
        return firstName;
    }
    /**
     * Set User's First Name
     * 
     * @param firstName to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    /**
     * Get Users's Last Name
     * 
     * @return Last Name
     */
    public String getLastName() {
        return lastName;
    }
    /**
     * Set User's Last Name
     * 
     * @param lastName to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    /**
     * Get User's Database Id
     * @return User's Database Id
     */
    public Long getId() {
        return id;
    }
    /**
     * Get Roles associated with User
     * @return List of Roles
     */
    public List<Role> getRoles() {
        return roles;
    }
    /**
     * Set Roles associated with User
     * @param roles List of roles to set.
     */
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
    /**
     * Check to see if the User is has Admin role
     * 
     * @return true if admin
     */
    public boolean isAdmin() {
        for ( Role role: roles ) {
            if ( role.getRole().equals("ADMIN")) return true;
        }
        return false;
    }
	public boolean isEmailUpdates() {
		return emailUpdates;
	}
	public void setEmailUpdates(boolean emailUpdates) {
		this.emailUpdates = emailUpdates;
	}
	public boolean isVerified() {
		return verified;
	}
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	public String getVerifyKey() {
		return verifyKey;
	}
	public void setVerifyKey(String verifyKey) {
		this.verifyKey = verifyKey;
	}
	public int getVerifyErrors() {
		return verifyErrors;
	}
	public void setVerifyErrors(int verifyErrors) {
		this.verifyErrors = verifyErrors;
	}
	public int getVerifyCount() {
		return verifyCount;
	}
	public void setVerifyCount(int verifyCount) {
		this.verifyCount = verifyCount;
	}
	public boolean isWelcomed() {
		return welcomed;
	}
	public void setWelcomed(boolean welcomed) {
		this.welcomed = welcomed;
	}
	public int getWelcomeErrors() {
		return welcomeErrors;
	}
	public void setWelcomeErrors(int welcomeErrors) {
		this.welcomeErrors = welcomeErrors;
	}
	public String getOptoutKey() {
		return optoutKey;
	}
	public void setOptoutKey(String optoutKey) {
		this.optoutKey = optoutKey;
	}
	public boolean isOptout() {
		return optout;
	}
	public void setOptout(boolean optout) {
		this.optout = optout;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public Date getCreateDate() {
		return createDate;
	}
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	public Locale getLocale() {
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
	public String[] getTitles() {
		return titles;
	}
	public void setTitles(String[] codes) {
		this.titles = codes;
	}
}
