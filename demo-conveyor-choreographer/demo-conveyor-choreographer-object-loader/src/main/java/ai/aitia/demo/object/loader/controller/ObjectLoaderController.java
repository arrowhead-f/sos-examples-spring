package ai.aitia.demo.object.loader.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.demo.object.loader.model.service.provided.LoadObjectService;
import eu.arrowhead.common.CommonConstants;

@RestController
public class ObjectLoaderController {
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = CommonConstants.ECHO_URI)
	public String echo() {
		return "Got it!";
	}
	
	//-------------------------------------------------------------------------------------------------
	@PostMapping(path = LoadObjectService.PATH)
	public void load() {
		System.out.println("An object has been loaded to the conveyor belt");
	}
}
