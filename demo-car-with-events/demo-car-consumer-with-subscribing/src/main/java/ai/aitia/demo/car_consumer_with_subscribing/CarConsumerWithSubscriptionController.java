package ai.aitia.demo.car_consumer_with_subscribing;

import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.arrowhead.application.skeleton.subscriber.constants.SubscriberConstants;
import eu.arrowhead.application.skeleton.subscriber.constants.SubscriberDefaults;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.dto.shared.EventDTO;

@RestController
@RequestMapping( SubscriberDefaults.DEFAULT_EVENT_NOTIFICATION_BASE_URI)
public class CarConsumerWithSubscriptionController {
	
	//=================================================================================================
	// members
	
	@Resource( name = SubscriberConstants.NOTIFICATION_QUEUE )
	private ConcurrentLinkedQueue<EventDTO> notificatonQueue;

	private final Logger logger = LogManager.getLogger(CarConsumerWithSubscriptionController.class);
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = CommonConstants.ECHO_URI)
	public String echoService() {
		return "Got it!";
	}
	
	//-------------------------------------------------------------------------------------------------
	@PostMapping(path = SubscriberConstants.REQUEST_RECEIVED_NOTIFICATION_URI) 
	public void receiveEventRequestReceived(@RequestBody final EventDTO event) {	
		logger.debug("receiveEventRequestReceived started...");
		
		if (event.getEventType() != null) {
			notificatonQueue.add(event);
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	@PostMapping(path = SubscriberConstants.PUBLISHER_DESTORYED_NOTIFICATION_URI) 
	public void receiveEventDestroyed(@RequestBody final EventDTO event) {
		logger.debug("receiveEventDestroyed started... ");
		
		if (event.getEventType() != null) {
			notificatonQueue.add(event);
		}
	}
}