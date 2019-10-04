package ai.aitia.demo.energy_forecast.provider.service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import ai.aitia.demo.energy.forecast.common.EFUtilities;
import ai.aitia.demo.energy.forecast.common.dto.EnergyDetailsDTO;
import ai.aitia.demo.energy.forecast.common.dto.EnergyForecastDTO;

@Component
public class EnergyForecastService {

	//=================================================================================================
	// members
	
	private static final String CSV_DATA_SET_NAME_PATTERN = "buliding_%_data.csv";
	private static final String DATA_SET_HEADER = "\"Timestamp\",\"Indoor\",\"Outdoor\",\"Energy Consumption kWh HEAT\",\"ENERGY Consumption kWh Water\"\n";
	
	@Autowired
	private EnergyForecastDriver energyForecastDriver;
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public EnergyForecastDTO forecast(final long building, final long forecastedTimestamp) throws IOException, URISyntaxException {
		final List<String[]> dataSet = updateDataSet(building, forecastedTimestamp);
		return predict(dataSet, building, forecastedTimestamp);
	}
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	private List<String[]> updateDataSet(final long building, final long forecastedTimestamp) throws IOException, URISyntaxException {
		final String fileName = String.format(CSV_DATA_SET_NAME_PATTERN, building);
		creteIfAbsentCSV(fileName);
		final List<String[]> dataSet = readCSV(fileName);
		
		long lastTimestamp;
		if (dataSet.size() == 1) {
			lastTimestamp = LocalDateTime.now().minusYears(1).toEpochSecond(ZoneOffset.UTC);
		} else {
			lastTimestamp = Long.valueOf(dataSet.get(dataSet.size() - 1)[0]);
		}
		
		final List<EnergyDetailsDTO> indoorNewData = energyForecastDriver.getIndoorEnergyDetails(building, lastTimestamp, EFUtilities.nowUTCSeconds()).getEnergyDetails();
		final List<EnergyDetailsDTO> outdoorNewData = energyForecastDriver.getIndoorEnergyDetails(building, lastTimestamp, EFUtilities.nowUTCSeconds()).getEnergyDetails();
		
		final List<String[]> newData = new ArrayList<>(indoorNewData.size());
		for (int i = 0; i < indoorNewData.size(); i++) {
			if (indoorNewData.get(i).getTimestamp() == lastTimestamp) {
				continue;
			}			
			final String timestamp = String.valueOf(indoorNewData.get(i).getTimestamp());
			final String inTemp = indoorNewData.get(i).getInTemp().toString();
			final String outTemp = outdoorNewData.get(i).getOutTemp().toString();
			final String totalHeat = String.valueOf(outdoorNewData.get(i).getTotal());
			final String waterHeat = String.valueOf(outdoorNewData.get(i).getWater());
			newData.add(new String[] {timestamp, inTemp, outTemp, totalHeat, waterHeat});
		}
		
		writeCSV(fileName, newData);
		return readCSV(fileName);
	}
	
	//-------------------------------------------------------------------------------------------------
	private EnergyForecastDTO predict(final List<String[]> dataSet, final long building, final long forecastedTimestamp) {
		final List<EnergyDetailsDTO> convertedDataSet = new ArrayList<>(dataSet.size());
		for (final String[] record : dataSet) {
			final EnergyDetailsDTO energyDetails = new EnergyDetailsDTO.Builder(Long.valueOf(record[0]), Long.valueOf(building))
																	   .setInTemp(Double.valueOf(record[1]))
																	   .setOutTemp(Double.valueOf(record[2]))
																	   .setTotal(Double.valueOf(record[3]))
																	   .setWater(Double.valueOf(record[4]))
																	   .build();
			convertedDataSet.add(energyDetails);
		}		
		return new EnergyForecastDTO(building, forecastedTimestamp, predictTotalHeat(convertedDataSet, forecastedTimestamp), predictWaterHeat(convertedDataSet, forecastedTimestamp));
	}
	
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
	
	//-------------------------------------------------------------------------------------------------
	private void creteIfAbsentCSV(final String fileName) throws IOException {
		final File file = new File(fileName);
		final FileWriter writer = new FileWriter(file);
		if (file.createNewFile()) {
			writer.write(DATA_SET_HEADER);
			writer.close();
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	private List<String[]> readCSV(final String fileName) throws IOException, URISyntaxException {
		final Reader reader = Files.newBufferedReader(Paths.get(ClassLoader.getSystemResource(fileName).toURI()));
		final CSVReader csvReader = new CSVReader(reader);
		final List<String[]> dataSet = csvReader.readAll();
		reader.close();
		csvReader.close();
		return dataSet;		
	}
	
	//-------------------------------------------------------------------------------------------------
	private void writeCSV(final String fileName, final List<String[]> newData) throws IOException, URISyntaxException {
		final CSVWriter writer = new CSVWriter(new FileWriter(Paths.get(ClassLoader.getSystemResource(fileName).toURI()).toString()));
		writer.writeAll(newData);
		writer.close();
	}
}
