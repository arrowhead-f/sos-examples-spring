package ai.aitia.demo.energy_forecast.indoor_provider.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ai.aitia.demo.energy.forecast.common.EFDataService;
import ai.aitia.demo.energy.forecast.common.dto.EnergyDetailsDTO;
import ai.aitia.demo.energy.forecast.common.dto.EnergyDetailsDTO.Builder;
import ai.aitia.demo.energy.forecast.common.dto.EnergyDetailsListDTO;

@Component
public class IndoorService {
	
	//=================================================================================================
	// members
	
	@Autowired
	private EFDataService dataService;
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public EnergyDetailsListDTO getHourlyEnergyDetails(final long building, final long tsStart, final long tsEnd) {
		final LocalDateTime start = LocalDateTime.ofInstant(Instant.ofEpochSecond(tsStart), ZoneOffset.UTC);
		final LocalDateTime startHour = start.withMinute(0).withSecond(0);
		final LocalDateTime end = LocalDateTime.ofInstant(Instant.ofEpochSecond(tsEnd), ZoneOffset.UTC);
		final LocalDateTime endHour = end.withMinute(0).withSecond(0);
		
		final List<EnergyDetailsDTO> energyDetails = new ArrayList<>();
		for (LocalDateTime timestamp = startHour; timestamp.isBefore(endHour) || timestamp.isEqual(endHour); timestamp = timestamp.plusHours(1)) {
			if (timestamp.isAfter(LocalDateTime.now())) {
				break;
			}
			final Builder energyDetailsDTOBuilder = new EnergyDetailsDTO.Builder(timestamp.toEpochSecond(ZoneOffset.UTC), building);
			energyDetails.add(energyDetailsDTOBuilder.setInTemp(dataService.getIndoorTemperature(timestamp))
								   					 .build());
		}
		return new EnergyDetailsListDTO(energyDetails, energyDetails.get(0).getTimestamp(), energyDetails.get(energyDetails.size() - 1).getTimestamp());
	}
}
