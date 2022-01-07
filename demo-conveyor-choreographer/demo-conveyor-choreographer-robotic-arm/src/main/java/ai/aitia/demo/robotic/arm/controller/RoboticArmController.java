package ai.aitia.demo.robotic.arm.controller;

import java.util.Random;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.demo.robotic.arm.model.dto.CoordinateDTO;
import ai.aitia.demo.robotic.arm.model.dto.DuckSeenResponseDTO;
import ai.aitia.demo.robotic.arm.model.service.provided.DuckSeenService;
import ai.aitia.demo.robotic.arm.model.service.provided.TakeOffService;
import eu.arrowhead.common.CommonConstants;

@RestController
public class RoboticArmController {
	
	//=================================================================================================
	// members

	private final Random rnd = new Random();

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = CommonConstants.ECHO_URI)
	public String echo() {
		return "Got it!";
	}
	
	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = DuckSeenService.PATH, produces = MediaType.APPLICATION_JSON_VALUE)
	public DuckSeenResponseDTO duckSeen() {
		final DuckSeenResponseDTO response = new DuckSeenResponseDTO();
		response.setSeen(!rnd.nextBoolean());
		
		if (!response.isSeen()) {
			System.out.println("No duck recognized");
			
		} else {
			response.setCoordinate(new CoordinateDTO(rnd.nextInt(50), rnd.nextInt(50), rnd.nextInt(50)));
			System.out.println("Duck has been recognize at " + response.getCoordinate().toString());
		}
		
		return response;
	}
	
	//-------------------------------------------------------------------------------------------------
	@PostMapping(path = TakeOffService.PATH, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void takeDuck(@RequestBody final CoordinateDTO coordinate) {
		System.out.println("Object has taken off from " + coordinate.toString());
	}
}
