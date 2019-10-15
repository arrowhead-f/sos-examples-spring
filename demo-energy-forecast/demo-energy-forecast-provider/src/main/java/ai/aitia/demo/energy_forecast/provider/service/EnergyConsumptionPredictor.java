package ai.aitia.demo.energy_forecast.provider.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import ai.aitia.demo.energy.forecast.common.dto.EnergyDetailsDTO;
import ai.aitia.demo.energy.forecast.common.dto.EnergyForecastDTO;

public class EnergyConsumptionPredictor {

	//=================================================================================================
	// members
	
	private final List<EnergyDetailsDTO> dataSet;
	private final long building;
	private final LocalDateTime forecastedTimestamp;
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public EnergyConsumptionPredictor(final List<EnergyDetailsDTO> dataSet,  final long building, final long forecastedTimestamp) {
		this.dataSet = dataSet;
		this.building = building;
		this.forecastedTimestamp = convertToLocalDateTime(forecastedTimestamp);
	}
	
	//-------------------------------------------------------------------------------------------------
	public EnergyForecastDTO predict() {
		final double totalHeatUnit = calculateTotalHeatUnit();
		final double waterHeatUnit = calculateWaterHeatUnit();
		
		final List<Double> expectedTotalHeatConsumptions = new ArrayList<>();
		final List<Double> expectedWaterHeatConsumptions = new ArrayList<>();
		LocalDateTime time = LocalDateTime.now();
		while (time.isBefore(forecastedTimestamp) || time.isEqual(forecastedTimestamp)) {
			final double expectedIndoorTemp = calculateExpectedIndoorTemperature(time);
			final double expectedOutdoorTemp = calculateExpectedOutdoorTemperature(time);
			final double expectedTempDiff = expectedIndoorTemp - expectedOutdoorTemp;
			if (expectedTempDiff > 0) {
				expectedTotalHeatConsumptions.add(totalHeatUnit * expectedTempDiff);
				expectedWaterHeatConsumptions.add(waterHeatUnit * expectedTempDiff);				
			}
			time = time.plusHours(1);
		}
		
		return new EnergyForecastDTO(building, forecastedTimestamp.toEpochSecond(ZoneOffset.UTC), sum(expectedTotalHeatConsumptions), sum(expectedWaterHeatConsumptions));
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private double calculateExpectedIndoorTemperature(final LocalDateTime time) {
		final List<Double> indoorTemps = new ArrayList<>();
		
		final int hourOfDay = time.getHour();
		final LocalDateTime scope = forecastedTimestamp.minusDays(10);
		for (int i = dataSet.size() - 1; convertToLocalDateTime(dataSet.get(i).getTimestamp()).isAfter(scope); --i) {
			if (convertToLocalDateTime(dataSet.get(i).getTimestamp()).getHour() == hourOfDay) {
				indoorTemps.add(dataSet.get(i).getInTemp());
			}
		}
		
		return calculateAverage(indoorTemps);
	}
	
	//-------------------------------------------------------------------------------------------------
	private double calculateExpectedOutdoorTemperature(final LocalDateTime time) {
		final List<Double> outdoorTemps = new ArrayList<>();
		
		final int hourOfDay = time.getHour();
		final LocalDateTime scope = forecastedTimestamp.minusDays(5);
		for (int i = dataSet.size() - 1; convertToLocalDateTime(dataSet.get(i).getTimestamp()).isAfter(scope); --i) {
			if (convertToLocalDateTime(dataSet.get(i).getTimestamp()).getHour() == hourOfDay) {
				outdoorTemps.add(dataSet.get(i).getOutTemp());
			}
		}
		
		return calculateAverage(outdoorTemps);
	}
	
	//-------------------------------------------------------------------------------------------------
	private double calculateTotalHeatUnit() {
		final List<Double> totalHeatUnits = new ArrayList<>();
		for (final EnergyDetailsDTO record : dataSet) {
			final double inTemp = record.getInTemp();
			final double outTemp = record.getOutTemp();
			final double tempDiff = Math.max(0d, inTemp - outTemp);
			totalHeatUnits.add(tempDiff > 0 ? record.getTotal() / tempDiff : 0d);
		}
		return calculateAverage(totalHeatUnits);
	}
	
	//-------------------------------------------------------------------------------------------------
	private double calculateWaterHeatUnit() {
		final List<Double> waterHeatUnits = new ArrayList<>();
		for (final EnergyDetailsDTO record : dataSet) {
			final double inTemp = record.getInTemp();
			final double outTemp = record.getOutTemp();
			final double tempDiff = Math.max(0d, inTemp - outTemp);
			waterHeatUnits.add(tempDiff > 0 ? record.getWater() / tempDiff : 0d);
		}
		return calculateAverage(waterHeatUnits);
	}
	
	//-------------------------------------------------------------------------------------------------
	private LocalDateTime convertToLocalDateTime(final long timestamp) {
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneOffset.UTC);
	}
	
	//-------------------------------------------------------------------------------------------------
	private double calculateAverage(final List<Double> temperatues) {
		double sum = 0;
		for (final Double temp : temperatues) {
			sum += temp;
		}
		return sum / temperatues.size();
	}
	
	//-------------------------------------------------------------------------------------------------
	private double sum(final List<Double> temperatues) {
		double sum = 0.0d;
		for (final Double temp : temperatues) {
			sum += temp;
		}
		return sum;
	}
}
