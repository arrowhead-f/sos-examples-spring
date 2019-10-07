package ai.aitia.demo.energy_forecast.provider.controller;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.demo.energy.forecast.common.EFCommonConstants;
import ai.aitia.demo.energy.forecast.common.EFUtilities;
import ai.aitia.demo.energy.forecast.common.dto.EnergyForecastDTO;
import ai.aitia.demo.energy_forecast.provider.service.EnergyForecastService;
import eu.arrowhead.common.exception.BadPayloadException;

@RestController
public class EnergyServiceController {
	
	//=================================================================================================
	// members

	@Autowired
	private EnergyForecastService energyForecastService;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = EFCommonConstants.ENERGY_FORECAST_SERVICE_URI)
	@ResponseBody public EnergyForecastDTO getEnergyForecastService(@RequestParam(name = EFCommonConstants.REQUEST_PARAM_BUILDING) final long building,
													  				@RequestParam(name = EFCommonConstants.REQUEST_PARAM_TIMESTAMP) final long timestamp) throws IOException, URISyntaxException {
		if (timestamp <= EFUtilities.nowUTCSeconds()) {
			throw new BadPayloadException("timestamp cannot be in the past");
		}
		return energyForecastService.forecast(building, timestamp);
	}
}