package eu.arrowhead.application.skeleton.executor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import eu.arrowhead.application.skeleton.executor.service.ExecutorService;
import eu.arrowhead.common.CommonConstants;
import eu.arrowhead.common.dto.shared.ChoreographerAbortStepRequestDTO;
import eu.arrowhead.common.dto.shared.ChoreographerExecuteStepRequestDTO;
import eu.arrowhead.common.dto.shared.ChoreographerExecutorServiceInfoRequestDTO;
import eu.arrowhead.common.dto.shared.ChoreographerExecutorServiceInfoResponseDTO;

@RestController
//@RequestMapping("/executor") // TODO: specify the base URI here
public class ExecutorController {

	//=================================================================================================
	// members

	@Autowired
	private ExecutorService executorService;
	
	//TODO: add your variables here

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = CommonConstants.ECHO_URI)
	public String echo() {
		return "Got it!";
	}
	
	//-------------------------------------------------------------------------------------------------
	@PostMapping(path = CommonConstants.CHOREOGRAPHER_EXECUTOR_CLIENT_SERVICE_START_URI, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void start(@RequestBody final ChoreographerExecuteStepRequestDTO request) {
		executorService.startExecution(request);
	}
	
	//-------------------------------------------------------------------------------------------------
	@PostMapping(path = CommonConstants.CHOREOGRAPHER_EXECUTOR_CLIENT_SERVICE_ABORT_URI, consumes = MediaType.APPLICATION_JSON_VALUE)
	public void abort(@RequestBody final ChoreographerAbortStepRequestDTO request) {
		executorService.abortExecution(request);
	}
	
	//-------------------------------------------------------------------------------------------------
	@PostMapping(path = CommonConstants.CHOREOGRAPHER_EXECUTOR_CLIENT_SERVICE_INFO_URI, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public ChoreographerExecutorServiceInfoResponseDTO serviceInfo(@RequestBody final ChoreographerExecutorServiceInfoRequestDTO request) {
		return executorService.collectServiceInfo(request);
	}
	
	//-------------------------------------------------------------------------------------------------
	//TODO: implement here your executor related REST end points
}
