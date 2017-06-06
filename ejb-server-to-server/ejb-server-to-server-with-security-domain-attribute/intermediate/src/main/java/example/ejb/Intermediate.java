package example.ejb;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.jboss.ejb3.annotation.SecurityDomain;

/**
 * @author <a href="mailto:mjurc@redhat.com">Michal Jurc</a>
 * @author <a href="mailto:olukas@redhat.com">Ondrej Lukas</a>
 */
@Stateless
@SecurityDomain("other")
public class Intermediate implements WhoAmIBeanRemote {

    @EJB(lookup = "ejb:/server-side/WhoAmIBean!example.ejb.WhoAmIBeanRemote")
    private WhoAmIBeanRemote serverSideWhoAmI;

    @RolesAllowed("users")
    public String whoAmI() {
        return serverSideWhoAmI.whoAmI();
    }

}
