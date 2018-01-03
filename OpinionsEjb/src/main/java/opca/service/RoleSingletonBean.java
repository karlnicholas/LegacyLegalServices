package opca.service;

import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import opca.model.Role;
import opca.model.User;

/**
 * This class is a singleton that loads and holds all Role definitions from 
 * the database. Very specific to this particular 
 * application. Should really only be used by UserAccountEJB.
 * 
 * @author Karl Nicholas
 *
 */
@Singleton
public final class RoleSingletonBean {
    private List<Role> allRoles;
    @Inject private EntityManager em;

    //private constructor to avoid client applications to use constructor
    @PostConstruct
    protected void postConstruct(){
        allRoles = em.createNamedQuery(Role.LIST_AVAILABLE, Role.class).getResultList();
        // initialize if needed
        if ( allRoles.size() == 0 ) {
        	Role userRole = new Role();
        	userRole.setRole("USER");
        	em.persist(userRole);
        	allRoles.add(userRole);
        	Role adminRole = new Role();
        	adminRole.setRole("ADMIN");
        	em.persist(adminRole);
        	allRoles.add(adminRole);
        	// might as well add an administrator now as well.
        	User admin = new User("karl.nicholas@outlook.com", true, "N3sPSBxOjdhCygeA8LkqtBskJ+v8TR0do4zJRTIQ4Aw=", Locale.US);
        	admin.setFirstName("Karl");
        	admin.setLastName("Nicholas");
        	admin.setVerified(true);
        	admin.setRoles(allRoles);
        	em.persist(admin);
        }
    }

    /**
     * Get the USER Role
     * @return USER Role
     */
    public Role getUserRole()  {
        for ( Role role: allRoles ) {
            if ( role.getRole().equals("USER")) return role;
        }
        throw new RuntimeException("Role USER not found"); 
    }
    /**
     * Get the ADMIN Role
     * @return ADMIN Role
     */
    public Role getAdminRole()  {
        for ( Role role: allRoles ) {
            if ( role.getRole().equals("ADMIN")) return role;
        }
        throw new RuntimeException("Role ADMIN not found"); 
    }
}

