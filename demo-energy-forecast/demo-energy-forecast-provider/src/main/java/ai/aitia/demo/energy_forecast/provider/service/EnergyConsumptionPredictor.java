package ai.aitia.demo.energy_forecast.provider.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
//		final double totalHeatUnit = calculateTotalHeatUnit();
//		final double waterHeatUnit = calculateWaterHeatUnit();
//		
//		final List<Double> expectedTotalHeatConsumptions = new ArrayList<>();
//		final List<Double> expectedWaterHeatConsumptions = new ArrayList<>();
//		final LocalDateTime time = LocalDateTime.now();
//		while (time.isBefore(forecastedTimestamp) || time.isEqual(forecastedTimestamp)) {
//			final double expectedIndoorTemp = calculateExpectedIndoorTemperature(time);
//			final double expectedOutdoorTemp = calculateExpectedOutdoorTemperature(time);
//			final double expectedTempDiff = Math.abs(expectedIndoorTemp - expectedOutdoorTemp);
//			expectedTotalHeatConsumptions.add(totalHeatUnit * expectedTempDiff);
//			expectedWaterHeatConsumptions.add(waterHeatUnit * expectedTempDiff);
//			time.plusHours(1);
//		}
//		
//		return new EnergyForecastDTO(building, forecastedTimestamp.toEpochSecond(ZoneOffset.UTC), sum(expectedTotalHeatConsumptions), sum(expectedWaterHeatConsumptions));
		return new EnergyForecastDTO(10, 00000, 444, 555);
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private double calculateExpectedIndoorTemperature(final LocalDateTime time) {
		final List<Double> indoorTemps = new ArrayList<>();
		
		final int hourOfDay = time.getHour();
		final LocalDateTime scope = forecastedTimestamp.minusDays(5);
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
				outdoorTemps.add(dataSet.get(i).getInTemp());
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
			final double tempDiff = Math.abs(inTemp - outTemp);
			totalHeatUnits.add(record.getTotal() / tempDiff);
		}
		return calculateAverage(totalHeatUnits);
	}
	
	//-------------------------------------------------------------------------------------------------
	private double calculateWaterHeatUnit() {
		final List<Double> waterHeatUnits = new ArrayList<>();
		for (final EnergyDetailsDTO record : dataSet) {
			final double inTemp = record.getInTemp();
			final double outTemp = record.getOutTemp();
			final double tempDiff = Math.abs(inTemp - outTemp);
			waterHeatUnits.add(record.getWater() / tempDiff);
		}
		return calculateAverage(waterHeatUnits);
	}
	
	//-------------------------------------------------------------------------------------------------
	private LocalDateTime convertToLocalDateTime(final long timestamp) {
		return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TimeZone.getDefault().toZoneId());
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
		double sum = 0;
		for (final Double temp : temperatues) {
			sum += temp;
		}
		return sum;
	}
}
