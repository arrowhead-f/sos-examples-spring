package ai.aitia.demo.session.monitor.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.dto.shared.ChoreographerNotificationDTO;

@RestController
public class SessionMonitorController {
	
	//=================================================================================================
	// members
	
	private static final String NOTIFIED_URI = "/notified";
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = CommonConstants.ECHO_URI)
	public String echo() {
		return "Got it!";
	}
	
	//-------------------------------------------------------------------------------------------------
	@PostMapping(path = NOTIFIED_URI)
	public void notified(@RequestBody final ChoreographerNotificationDTO notification) {
		System.out.println(notification.toString());
	}
}
