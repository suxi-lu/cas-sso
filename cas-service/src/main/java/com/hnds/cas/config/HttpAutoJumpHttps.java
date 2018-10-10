package com.hnds.cas.config;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration("httpAutoJumpHttps")
@ConfigurationProperties(prefix = "cas-sso-server.auto-jump")
@ConditionalOnProperty(prefix = "cas-sso-server.auto-jump", name = "enabled", havingValue = "true")
public class HttpAutoJumpHttps {

    @Value("${http-port}")
    private String httpPort = "8080";
    @Value("${https-port}")
    private String httpsPort = "8443";

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");//confidential
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(httpConnector());
        return tomcat;
    }

    @Bean
    public Connector httpConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
//        connector.setPort(8080);
        connector.setPort(Integer.valueOf(httpPort));
        connector.setSecure(false);
//        connector.setRedirectPort(8443);
        connector.setRedirectPort(Integer.valueOf(httpsPort));
        return connector;
    }

}
