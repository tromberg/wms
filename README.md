# wms

* requires maven 3.0+
* setup new maven repo if desired (especially the jboss profile will download quite a few MB)
* ensure your settings.xml contains download.java.net/maven/2 and repo1.maven.org/maven2
* default profile: arquillian-glassfish, alternative -Parquillian-wildfly (maven will install  wildfly)
* mvn test

limitations:
* no REST interface (yet - as it was explicitly not required). still, WAR project was chosen to prepare for this
* hence 'app-managed' entity manager and transactions, where in more complex apps one would rely on EJB
* client is responsible for avoiding to inject WatermarkService into multithreaded contexts (e.g. @ApplicationScoped). This would be solved by EJB as well.


