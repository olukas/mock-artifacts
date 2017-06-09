## Invoking EJBs in a server-to-server scenario using Elytron on the client side and OAUTHBEARER SASL mechanism
### 1. Prepare server-side EAP
1. Run EAP
2. Configure the things needed for the EJB client connection:
```
/subsystem=elytron/token-realm=JWT:add(principal-claim=aud,jwt={})
/subsystem=elytron/constant-role-mapper=users:add(roles=[users])
/subsystem=elytron/constant-realm-mapper=JWT:add(realm-name=JWT)
/subsystem=elytron/security-domain=ApplicationDomain:list-add(name=realms,value={realm=JWT,role-mapper=users,role-decoder=groups-to-roles})
/subsystem=elytron/sasl-authentication-factory=application-sasl-authentication:undefine-attribute(name=mechanism-configurations)
/subsystem=elytron/sasl-authentication-factory=application-sasl-authentication:write-attribute(name=mechanism-configurations,value=[{mechanism-name=OAUTHBEARER,realm-mapper=JWT}])
/subsystem=ejb3/application-security-domain=other:add(security-domain=ApplicationDomain)
/subsystem=remoting/http-connector=http-remoting-connector:undefine-attribute(name=security-realm)
/subsystem=remoting/http-connector=http-remoting-connector:write-attribute(name=sasl-authentication-factory,value=application-sasl-authentication)
reload
```
3. Build and deploy the `server` project

### 2. Prepare client-side EAP
1. Run the EAP with property `-Dremote.ejb.host=HOSTNAME_OF_REMOTE_SERVER` (where `HOSTNAME_OF_REMOTE_SERVER` is the address where server-side EAP is available)
2. Configure the things needed for the EJB client connection:
```
/subsystem=elytron/token-realm=JWT:add(principal-claim=aud,jwt={})
/subsystem=elytron/constant-role-mapper=users:add(roles=[users])
/subsystem=elytron/constant-realm-mapper=JWT:add(realm-name=JWT)
/subsystem=elytron/security-domain=ApplicationDomain:list-add(name=realms,value={realm=JWT,role-mapper=users,role-decoder=groups-to-roles})
/subsystem=elytron/sasl-authentication-factory=application-sasl-authentication:undefine-attribute(name=mechanism-configurations)
/subsystem=elytron/sasl-authentication-factory=application-sasl-authentication:write-attribute(name=mechanism-configurations,value=[{mechanism-name=OAUTHBEARER,realm-mapper=JWT}])
/subsystem=ejb3/application-security-domain=other:add(security-domain=ApplicationDomain)
/socket-binding-group=standard-sockets/remote-destination-outbound-socket-binding=remote-ejb:add(host=${remote.ejb.host}, port=8080)
/subsystem=elytron/authentication-configuration=cfg-with-security-domain:add(sasl-mechanism-selector="OAUTHBEARER", security-domain=ApplicationDomain)
/subsystem=elytron/authentication-context=auth-ctx:add(match-rules=[{authentication-configuration=cfg-with-security-domain}])
/subsystem=elytron:write-attribute(name=default-authentication-context,value=auth-ctx)
/subsystem=remoting/remote-outbound-connection=remote-ejb-connection:add(authentication-context=auth-ctx, outbound-socket-binding-ref=remote-ejb)
/subsystem=remoting/http-connector=http-remoting-connector:undefine-attribute(name=security-realm)
/subsystem=remoting/http-connector=http-remoting-connector:write-attribute(name=sasl-authentication-factory,value=application-sasl-authentication)
reload
```
3. Build and deploy the `intermediate` project

### 3. Run the example
1. Build and run the `client` project: `mvn clean install exec:exec -Dintermediate.ejb.host=HOSTNAME_OF_INTERMEDIATE_SERVER` (where `HOSTNAME_OF_INTERMEDIATE_SERVER` is EAP with `intermediate` deployment).
SaslException is thrown.

