package ai.aitia.demo.energy_forecast.outdoor_provider.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.demo.energy.forecast.common.EFCommonConstants;
import ai.aitia.demo.energy.forecast.common.EFUtilities;
import ai.aitia.demo.energy.forecast.common.dto.EnergyDetailsListDTO;
import ai.aitia.demo.energy_forecast.outdoor_provider.service.OutdoorService;
import eu.arrowhead.common.exception.BadPayloadException;

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
	public EnergyDetailsListDTO getOutdoorEnergyDetails(@RequestParam(name = EFCommonConstants.REQUEST_PARAM_BUILDING) final long building,
														  @RequestParam(name = EFCommonConstants.REQUEST_PARAM_FROM) final long fromTimestamp,
														  @RequestParam(name = EFCommonConstants.REQUEST_PARAM_TO, required = false) Long toTimestamp) {
		
		if (fromTimestamp < 0) {
			throw new BadPayloadException("fromTimestamp cannot be less than zero");
		}
		
		if (fromTimestamp > EFUtilities.nowUTCSeconds() || fromTimestamp > EFUtilities.nowUTCSeconds()) {
			throw new BadPayloadException("fromTimestamp or toTimestamp cannot be int the future");
		}
		
		toTimestamp = toTimestamp == null || toTimestamp < fromTimestamp ? fromTimestamp : toTimestamp; 
		
		return outdoorService.getHourlyEnergyDetails(building, fromTimestamp, toTimestamp);
	}
}
