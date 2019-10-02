package ai.aitia.demo.energy_forecast.outdoor_provider.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.arrowhead.common.CommonConstants;

@RestController
public class OutdoorTemperatureServiceController {
	
	//=================================================================================================
	// members

	//TODO: add your variables here

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = CommonConstants.ECHO_URI)
	public String echoService() {
		return "Got it!";
	}
	
	//-------------------------------------------------------------------------------------------------
	//TODO: implement here your provider related REST end points
}
