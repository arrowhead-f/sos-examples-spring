package ai.aitia.demo.conveyor.belt.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.demo.conveyor.belt.model.service.provided.TransportWithConveyorService;
import eu.arrowhead.common.CommonConstants;

@RestController
public class ConveyorBeltController {
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = CommonConstants.ECHO_URI)
	public String echo() {
		return "Got it!";
	}
	
	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = TransportWithConveyorService.PATH)
	public void move(@RequestParam(name = "signed-distance", required = false) final Double distance) {
		final double dist = distance == null ? 10 : distance;
		System.out.println("Conveyor belt has been moved by " + dist);
	}
}
