package client;

import java.util.Properties;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.wildfly.naming.client.WildFlyInitialContextFactory;

import ejb.TransactionalBeanRemote;

/**
 * @author jmartisk
 */
public class Client {

    public static final String USERNAME = "joe";
    public static final String PASSWORD = "joeIsAwesome2013!";
    public static final String URL = "remote+http://127.0.0.1:8080";

    public static void main(String[] args) throws Exception {
        runUsingSecurityCredentialsInInitialContext();
    }

    public static Properties getCtxPropertiesWithSecurityCredentials() {
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, WildFlyInitialContextFactory.class.getName());
        props.put(Context.PROVIDER_URL, URL);
        props.put(Context.SECURITY_PRINCIPAL, USERNAME);
        props.put(Context.SECURITY_CREDENTIALS, PASSWORD);
        return props;
    }

    public static void runUsingSecurityCredentialsInInitialContext() throws NamingException {
        System.out.println("******************* running using security credentials in Context properties");
        execute(new InitialContext(getCtxPropertiesWithSecurityCredentials()));
    }

    private static void execute(InitialContext ctx) {
        UserTransaction tx = null;
        try {
            String lookupName = "ejb:/server/TransactionalBean!ejb.TransactionalBeanRemote";
            final TransactionalBeanRemote bean = (TransactionalBeanRemote)ctx.lookup(lookupName);
            tx = (UserTransaction)ctx.lookup("txn:UserTransaction");
            System.out.println("Number of committed entities: " + bean.getEntitiesCount());
            System.out.println("Beginning a transaction...");
            tx.begin();
            System.out.println("Creating an entity...");
            bean.createEntity();
            System.out.println("Rolling back the transaction...");
            tx.rollback();
            System.out.println("Number of committed entities: " + bean.getEntitiesCount());
            System.out.println("Beginning a transaction...");
            tx.begin();
            System.out.println("Creating an entity...");
            bean.createEntity();
            System.out.println("Committing the transaction...");
            tx.commit();
            System.out.println("Number of committed entities: " + bean.getEntitiesCount());
        } catch (Exception e) {

            System.out.println("Something failed!!!");
            e.printStackTrace();

            System.out.println("Rolling back....");
            try {
                if (tx != null && tx.getStatus() == Status.STATUS_ACTIVE) {
                    try {
                        tx.rollback();
                        System.out.println("Rollback successful.");
                    } catch (SystemException e1) {
                        System.out.println("Rollback failed.");
                        e1.printStackTrace();
                    }
                }
            } catch (SystemException e1) {
                e1.printStackTrace();
            }

            throw new RuntimeException(e);
        } finally {
            System.out.println("***************** Trying to call a method with @TransactionalAttribute(REQUIRED) now....");
            final TransactionalBeanRemote bean;
            try {
                bean = (TransactionalBeanRemote)ctx.lookup("ejb:/server/TransactionalBean!ejb.TransactionalBeanRemote");
                System.out.println("number of committed entities: " + bean.getEntitiesCount());
            } catch (NamingException e) {
                e.printStackTrace();
            }
            try {
                ctx.close();
            } catch (NamingException e) {
            }
        }
    }

}
