package eu.arrowhead.application.skeleton.subscriber;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "event")
public class ConfigEventProperites {
	
	//=================================================================================================
	// members
	
	private Map<String, String> eventTypeURIMap;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------

	public Map<String, String> getEventTypeURIMap() { return eventTypeURIMap; }
	public void setEventTypeURIMap(final Map<String, String> eventTypeURIMap) { this.eventTypeURIMap = eventTypeURIMap; }

}
