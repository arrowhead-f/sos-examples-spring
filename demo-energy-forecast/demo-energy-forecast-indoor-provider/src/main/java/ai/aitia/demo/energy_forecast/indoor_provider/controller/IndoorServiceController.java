package ai.aitia.demo.energy_forecast.indoor_provider.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.demo.energy.forecast.common.EFCommonConstants;
import ai.aitia.demo.energy.forecast.common.EFUtilities;
import ai.aitia.demo.energy.forecast.common.dto.EnergyDetailsListDTO;
import ai.aitia.demo.energy_forecast.indoor_provider.service.IndoorService;
import eu.arrowhead.common.exception.BadPayloadException;

@RestController
public class IndoorServiceController {
	
	//=================================================================================================
	// members
	
	@Autowired
	private IndoorService indoorService;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = EFCommonConstants.INDOOR_ENERGY_DETAILS_SERVICE_URI)
	@ResponseBody public EnergyDetailsListDTO getIndoorEnergyDetails(@RequestParam(name = EFCommonConstants.REQUEST_PARAM_BUILDING) final long building,
														  			 @RequestParam(name = EFCommonConstants.REQUEST_PARAM_FROM) final long fromTimestamp,
														  			 @RequestParam(name = EFCommonConstants.REQUEST_PARAM_TO, required = false) Long toTimestamp) {
		
		if (fromTimestamp < 0) {
			throw new BadPayloadException("fromTimestamp cannot be less than zero");
		}
		
		if (fromTimestamp > EFUtilities.nowUTCSeconds() || toTimestamp > EFUtilities.nowUTCSeconds()) {
			throw new BadPayloadException("fromTimestamp or toTimestamp cannot be int the future");
		}
		
		toTimestamp = toTimestamp == null || toTimestamp < fromTimestamp ? fromTimestamp : toTimestamp; 
		
		return indoorService.getHourlyEnergyDetails(building, fromTimestamp, toTimestamp);
	}
}
