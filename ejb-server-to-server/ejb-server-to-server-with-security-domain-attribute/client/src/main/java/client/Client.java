package client;

import java.security.PrivilegedActionException;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import example.ejb.WhoAmIBeanRemote;
import org.wildfly.naming.client.WildFlyInitialContextFactory;

/**
 * @author jmartisk, olukas
 */
public class Client {

    public static void main(String[] args)
            throws NamingException, PrivilegedActionException, InterruptedException {
        InitialContext ctx = new InitialContext(getCtxProperties());
        try {
            String lookupName = "ejb:/intermediate/Intermediate!example.ejb.WhoAmIBeanRemote";
            WhoAmIBeanRemote bean = (WhoAmIBeanRemote) ctx.lookup(lookupName);
            System.out.println(bean.whoAmI());
        } finally {
            ctx.close();
        }
    }

    public static Properties getCtxProperties() {
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, WildFlyInitialContextFactory.class.getName());
        return props;
    }

}
