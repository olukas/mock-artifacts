package client;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import ejb.HelloBeanRemote;

/**
 * @author jmartisk
 */
public class Client {

    // for me the magical constant seems to be 16 (with 15 threads it still works)
    final static int THREADS = 25;

    public static void main(String[] args) throws NamingException, InterruptedException {
        final ExecutorService pool = Executors.newFixedThreadPool(THREADS);

        for (int i = 0; i < THREADS; i++) {
            pool.submit(() -> {
                try {
                    final InitialContext ctx = new InitialContext(getCtxProperties());
                    String lookupName = "ejb:/server/HelloBean!ejb.HelloBeanRemote";
                    HelloBeanRemote bean = (HelloBeanRemote)ctx.lookup(lookupName);
                    System.out.println(bean.hello());
                    ctx.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        pool.shutdown();

    }

    public static Properties getCtxProperties() {
        Properties props = new Properties();
        props.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        props.put("remote.connections", "main");
        props.put("remote.connection.main.host", "127.0.0.1");
        props.put("remote.connection.main.port", "8080");
        props.put("remote.connection.main.username", "joe");
        props.put("remote.connection.main.password", "joeIsAwesome2013!");
        return props;
    }

}
