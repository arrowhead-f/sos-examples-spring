package eu.arrowhead.application.skeleton.subscriber;

import eu.arrowhead.application.skeleton.subscriber.constants.SubscriberDefaults;
import eu.arrowhead.common.dto.shared.SubscriptionRequestDTO;
import eu.arrowhead.common.dto.shared.SystemRequestDTO;

public class SubscriberUtilities {
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static SubscriptionRequestDTO createSubscriptionRequestDTO(final String eventType, final SystemRequestDTO subscriber, final String notificationUri) {
		final SubscriptionRequestDTO subscription = new SubscriptionRequestDTO(
				eventType.toUpperCase(), 
				subscriber, 
				null, 
				SubscriberDefaults.DEFAULT_EVENT_NOTIFICATION_BASE_URI + "/" + notificationUri, 
				false, 
				null, 
				null, 
				null);
		
		return subscription;
	}
}