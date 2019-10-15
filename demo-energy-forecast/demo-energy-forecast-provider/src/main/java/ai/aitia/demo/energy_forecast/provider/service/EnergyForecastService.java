package ai.aitia.demo.energy_forecast.provider.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
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
	
	private static final String CSV_DATA_SET_NAME_PATTERN = "building_%d_data.csv";
	private static final String DATA_SET_HEADER = "Timestamp,Indoor,Outdoor,Energy Consumption kWh HEAT,ENERGY Consumption kWh Water\n";
	
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
		final String fileName = String.format("data_model/" + CSV_DATA_SET_NAME_PATTERN, building);
		creteIfAbsentCSV(fileName);
		final List<String[]> dataSet = readCSV(fileName);
		
		long lastTimestamp;
		if (dataSet.size() == 1) {
			lastTimestamp = LocalDateTime.now().minusYears(1).toEpochSecond(ZoneOffset.UTC);
		} else {
			lastTimestamp = Long.valueOf(dataSet.get(dataSet.size() - 1)[0]);
		}
		
		final List<EnergyDetailsDTO> indoorNewData = energyForecastDriver.getIndoorEnergyDetails(building, lastTimestamp, EFUtilities.nowUTCSeconds()).getEnergyDetails();
		final List<EnergyDetailsDTO> outdoorNewData = energyForecastDriver.getOutdoorEnergyDetails(building, lastTimestamp, EFUtilities.nowUTCSeconds()).getEnergyDetails();
		
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
			if (NumberUtils.isCreatable(record[0])) {				
				final EnergyDetailsDTO energyDetails = new EnergyDetailsDTO.Builder(Long.valueOf(record[0]), Long.valueOf(building))
						.setInTemp(Double.valueOf(record[1]))
						.setOutTemp(Double.valueOf(record[2]))
						.setTotal(Double.valueOf(record[3]))
						.setWater(Double.valueOf(record[4]))
						.build();
				convertedDataSet.add(energyDetails);
			}
		}		
		return new EnergyConsumptionPredictor(convertedDataSet, building, forecastedTimestamp).predict();
	}
	
	//-------------------------------------------------------------------------------------------------
	private void creteIfAbsentCSV(final String fileName) throws IOException {
		final File file = new File(fileName);
		file.getParentFile().mkdirs();
		if (file.createNewFile()) {
			final FileWriter writer = new FileWriter(new File(fileName));
			writer.write(DATA_SET_HEADER);
			writer.flush();
			writer.close();
		}
	}
	
	//-------------------------------------------------------------------------------------------------
	private List<String[]> readCSV(final String fileName) throws IOException, URISyntaxException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		final CSVReader csvReader = new CSVReader(reader);
		final List<String[]> dataSet = csvReader.readAll();
		reader.close();
		csvReader.close();
		return dataSet;		
	}
	
	//-------------------------------------------------------------------------------------------------
	private void writeCSV(final String fileName, final List<String[]> newData) throws IOException, URISyntaxException {
		final CSVWriter writer = new CSVWriter(new FileWriter(new File(fileName), true));
		writer.writeAll(newData);
		writer.close();
	}
}
