package ai.aitia.demo.energy_forecast.outdoor_provider.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.aitia.demo.energy.forecast.common.EFDataService;
import ai.aitia.demo.energy.forecast.common.dto.EnergyDetailsDTO;
import ai.aitia.demo.energy.forecast.common.dto.EnergyDetailsDTO.Builder;

@Component
public class OutdoorService {

	//=================================================================================================
	// members
	
	@Autowired
	private EFDataService dataService;
	
	//=================================================================================================
	// methods
	
	public List<EnergyDetailsDTO> getHourlyEnergyDetails(final long building, final long tsStart, final long tsEnd) {
		LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(tsStart), TimeZone.getDefault().toZoneId());
		LocalDateTime startHour = start.withMinute(0).withSecond(0);
		LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(tsEnd), TimeZone.getDefault().toZoneId());
		LocalDateTime endHour = end.withMinute(0).withSecond(0);
		
		List<EnergyDetailsDTO> energyDetails = new ArrayList<>();
		for (LocalDateTime timestamp = startHour; timestamp.isBefore(endHour) || timestamp.isEqual(endHour); timestamp = timestamp.plusHours(1)) {
			Builder energyDetailsDTOBuilder = new EnergyDetailsDTO.Builder(timestamp.toEpochSecond(ZoneOffset.UTC), building);
			energyDetails.add(energyDetailsDTOBuilder.setOutTemp(dataService.getOutdoorTemperature(timestamp))
								   					 .setWater(dataService.getWaterHeat(timestamp))
								   					 .setTotal(dataService.getTotalHeat(timestamp))
								   					 .build());
		}
		return energyDetails;
	}
}
