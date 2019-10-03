package ai.aitia.demo.energy_forecast.outdoor_provider.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.demo.energy.forecast.common.EFCommonConstants;
import ai.aitia.demo.energy.forecast.common.dto.EnergyDetailsDTO;
import ai.aitia.demo.energy_forecast.outdoor_provider.service.OutdoorService;

@RestController
public class OutdoorServiceController {
	
	//=================================================================================================
	// members
	
	@Autowired
	private OutdoorService outdoorService;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = EFCommonConstants.OUTDOOR_ENERGY_DETAILS_SERVICE_URI, produces = MediaType.APPLICATION_XML_VALUE)
	public List<EnergyDetailsDTO> getOutdoorEnergyDetails(@RequestParam(name = EFCommonConstants.OUTDOOR_REQUEST_PARAM_BUILDING) final long building,
														  @RequestParam(name = EFCommonConstants.OUTDOOR_REQUEST_PARAM_FROM) final long fromTimestamp,
														  @RequestParam(name = EFCommonConstants.OUTDOOR_REQUEST_PARAM_TO, required = false) Long toTimestamp) {
		
		toTimestamp = toTimestamp == null || toTimestamp < fromTimestamp ? fromTimestamp : toTimestamp; 
		
		return outdoorService.getHourlyEnergyDetails(building, fromTimestamp, toTimestamp);
	}
}
