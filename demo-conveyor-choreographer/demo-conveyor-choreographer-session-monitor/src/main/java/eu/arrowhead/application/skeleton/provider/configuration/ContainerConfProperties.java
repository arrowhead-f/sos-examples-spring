package eu.arrowhead.application.skeleton.provider.configuration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.validation.annotation.Validated;

@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "container")
@Validated
public class ContainerConfProperties {

	//=================================================================================================
	// members
	
	@Min(ConfigConstants.MIN_MAXKEEPALIVE_REQUESTS)
	@Max(ConfigConstants.MAX_MAXKEEPALIVE_REQUESTS)
	private int maxKeepAliveRequests;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------

	public int getMaxKeepAliveRequests() { return maxKeepAliveRequests; }
	public void setMaxKeepAliveRequests(final int maxKeepAliveRequests) { this.maxKeepAliveRequests = maxKeepAliveRequests; }
}
