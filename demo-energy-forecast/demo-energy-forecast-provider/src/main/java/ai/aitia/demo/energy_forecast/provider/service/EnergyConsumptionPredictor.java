package ai.aitia.demo.energy_forecast.provider.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import ai.aitia.demo.energy.forecast.common.dto.EnergyDetailsDTO;
import ai.aitia.demo.energy.forecast.common.dto.EnergyForecastDTO;

public class EnergyConsumptionPredictor {

	//=================================================================================================
	// members
	
	private final List<EnergyDetailsDTO> dataSet;
	private final long building;
	private final long forecastedTimestamp;
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public EnergyConsumptionPredictor(final List<EnergyDetailsDTO> dataSet,  final long building, final long forecastedTimestamp) {
		this.dataSet = dataSet;
		this.building = building;
		this.forecastedTimestamp = forecastedTimestamp;
	}
	
	//-------------------------------------------------------------------------------------------------
	public EnergyForecastDTO predict() {
		return new EnergyForecastDTO(building, forecastedTimestamp, predictTotalHeat(dataSet, forecastedTimestamp), predictWaterHeat(dataSet, forecastedTimestamp));
	}
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	private double predictTotalHeat(final List<EnergyDetailsDTO> dataSet, final long forecastedTimestamp) {
		final List<Double> averageTotalHeatConsumptionPerHourOfDay = new ArrayList<>(24);
		final List<Long> recordCount = new ArrayList<>(24);
		
		for (final EnergyDetailsDTO record : dataSet) {
			final LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochSecond(record.getTimestamp()), TimeZone.getDefault().toZoneId());
			final int hourOfDay = timestamp.getHour();
			if (recordCount.get(hourOfDay) == null) {
				recordCount.add(hourOfDay, 1l);;
				averageTotalHeatConsumptionPerHourOfDay.add(hourOfDay, record.getTotal());
			} else {
				recordCount.add(hourOfDay, recordCount.get(hourOfDay) + 1l);
				averageTotalHeatConsumptionPerHourOfDay.add(hourOfDay, (averageTotalHeatConsumptionPerHourOfDay.get(hourOfDay) + record.getTotal()) / recordCount.get(hourOfDay));
			}
		}
		
		final int forecastedHour = LocalDateTime.ofInstant(Instant.ofEpochSecond(forecastedTimestamp), TimeZone.getDefault().toZoneId()).getHour();
		double forecast = 0.0d;
		for (int i = 0; i <= forecastedHour; i++) {
			forecast += averageTotalHeatConsumptionPerHourOfDay.get(i);
		}
		return forecast;
	}
	
	//-------------------------------------------------------------------------------------------------
	private double predictWaterHeat(final List<EnergyDetailsDTO> dataSet, final long forecastedTimestamp) {
		final List<Double> averageWaterHeatConsumptionPerHourOfDay = new ArrayList<>(24);
		final List<Long> recordCount = new ArrayList<>(24);
		
		for (final EnergyDetailsDTO record : dataSet) {
			final LocalDateTime timestamp = LocalDateTime.ofInstant(Instant.ofEpochSecond(record.getTimestamp()), TimeZone.getDefault().toZoneId());
			final int hourOfDay = timestamp.getHour();
			if (recordCount.get(hourOfDay) == null) {
				recordCount.add(hourOfDay, 1l);;
				averageWaterHeatConsumptionPerHourOfDay.add(hourOfDay, record.getWater());
			} else {
				recordCount.add(hourOfDay, recordCount.get(hourOfDay) + 1l);
				averageWaterHeatConsumptionPerHourOfDay.add(hourOfDay, (averageWaterHeatConsumptionPerHourOfDay.get(hourOfDay) + record.getWater()) / recordCount.get(hourOfDay));
			}
		}
		
		final int forecastedHour = LocalDateTime.ofInstant(Instant.ofEpochSecond(forecastedTimestamp), TimeZone.getDefault().toZoneId()).getHour();
		double forecast = 0.0d;
		for (int i = 0; i <= forecastedHour; i++) {
			forecast += averageWaterHeatConsumptionPerHourOfDay.get(i);
		}
		return forecast;
	}
}
