package eu.arrowhead.application.skeleton.provider.configuration;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContainerConfiguration implements WebServerFactoryCustomizer<TomcatServletWebServerFactory>  {
	
	//=================================================================================================
	// members
	
	@Autowired
	ContainerConfProperties containerConfProperties;
	
	private final Logger log = LogManager.getLogger( ContainerConfiguration.class);
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------	
	@SuppressWarnings("rawtypes")
	@Override
	public void customize(TomcatServletWebServerFactory factory) {
		 factory.addConnectorCustomizers(connector -> {
	            final AbstractHttp11Protocol protocol = (AbstractHttp11Protocol) connector.getProtocolHandler();

	            protocol.setMaxKeepAliveRequests(containerConfProperties.getMaxKeepAliveRequests());

	            log.info("####################################################################################");
	            log.info("#");
	            log.info("# TomcatCustomizer");
	            log.info("#");
	            log.info("# custom maxKeepAliveRequests {}", protocol.getMaxKeepAliveRequests());
	            log.info("# origin keepalive timeout: {} ms", protocol.getKeepAliveTimeout());
	            log.info("# keepalive timeout: {} ms", protocol.getKeepAliveTimeout());
	            log.info("# connection timeout: {} ms", protocol.getConnectionTimeout());
	            log.info("# max connections: {}", protocol.getMaxConnections());
	            log.info("#");
	            log.info("####################################################################################");
		 });
	}
}