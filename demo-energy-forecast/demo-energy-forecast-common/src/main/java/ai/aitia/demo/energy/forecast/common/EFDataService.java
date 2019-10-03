package ai.aitia.demo.energy.forecast.common;

import java.io.IOException;
import java.io.Reader;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.opencsv.CSVReader;

import eu.arrowhead.client.library.util.ClientCommonConstants;

@Component
public class EFDataService {
	
	//=================================================================================================
	// members
	
	/**
     *  Outdoor temperature data from:
     *
     *  PV systems, Department of Energy Technology, Aalborg University
     *  https://www.et.aau.dk/research-programmes/photovoltaic-systems/
     */
	private List<Double> outdoorTemperatureData;
	private final double indoorTemperatureData = 22.0d;
	
	private static final double L = 20.0d;
    private static final double H = 2.0d;
    private static final double W = 10.0d;
    private static final double U_BRICK = 2.6d;
    private static final double U_CONCRETE = 0.7d;
    private static final double U_ABESTOS = 5.7d;
    private static final double MATERIAL = 2.0d * L * H * U_BRICK + 2.0d * W * H * U_BRICK + L * W * U_CONCRETE + L * W * U_ABESTOS;
    private static final double VENTILATION = 1.2d;
    private static final double SCALE = 2.0d * 1000.0d;
	
    //=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@PostConstruct
	public void init() throws IOException, URISyntaxException {
		readOutdoorTemperatureData();		
	}
	
	//-------------------------------------------------------------------------------------------------
	public double getOutdoorTemperature(final LocalDateTime timestamp) {
		final LocalDateTime yearStart = LocalDateTime.of(timestamp.getYear(), 1, 1, 0, 0);
		final int hourOfYear = (int) yearStart.until(timestamp, ChronoUnit.HOURS);
		return outdoorTemperatureData.get(hourOfYear);
	}
	
	//-------------------------------------------------------------------------------------------------
	public double getIndoorTemperature(final LocalDateTime timestamp) {
		return Math.max(indoorTemperatureData, getOutdoorTemperature(timestamp));
	}
	
	//-------------------------------------------------------------------------------------------------
	public double getHeatLoss(final LocalDateTime timestamp) {
        return Math.max(0, VENTILATION * (getIndoorTemperature(timestamp) - getOutdoorTemperature(timestamp)) * MATERIAL / SCALE);
    }	
	
	//-------------------------------------------------------------------------------------------------
	public double getWaterHeat(final LocalDateTime timestamp) {
        return -0.01f * getOutdoorTemperature(timestamp) + 0.3d;
    }
	
	//-------------------------------------------------------------------------------------------------
	public double getTotalHeat(final LocalDateTime timestamp) {
		return getWaterHeat(timestamp) + getHeatLoss(timestamp);
	}
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private void readOutdoorTemperatureData() throws IOException, URISyntaxException {
		final Reader reader = Files.newBufferedReader(Paths.get(ClassLoader.getSystemResource("outdoor_temerature_data.csv").toURI()));
		final CSVReader csvReader = new CSVReader(reader);
		final List<String[]> readAll = csvReader.readAll();
		outdoorTemperatureData = new ArrayList<Double>(readAll.size());
		for (final String[] strings : readAll) {
			outdoorTemperatureData.add(Double.valueOf(strings[0]));
		}
		reader.close();
	    csvReader.close();
	}
}
