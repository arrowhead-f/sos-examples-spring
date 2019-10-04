package ai.aitia.demo.energy.forecast.common.dto;

import java.io.Serializable;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "EnergyDetailsDTO")
public class EnergyForecastDTO implements Serializable {

	//=================================================================================================
	// members
	
	private static final long serialVersionUID = -4791068444949469627L;
	
	@JacksonXmlProperty
	private long building;
	
	@JacksonXmlProperty
	private long forecastTime;
	
	@JacksonXmlProperty
	private double forecastedTotalHeatConsumptionKWH;
	
	@JacksonXmlProperty
	private double forecastedWaterHeatConsumptionKWH;
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------	
	public EnergyForecastDTO() {}

	//-------------------------------------------------------------------------------------------------	
	public EnergyForecastDTO(final long building, final long forecastTime, final double forecastedTotalHeatConsumptionKWH, final double forecastedWaterHeatConsumptionKWH) {
		this.building = building;
		this.forecastTime = forecastTime;
		this.forecastedTotalHeatConsumptionKWH = forecastedTotalHeatConsumptionKWH;
		this.forecastedWaterHeatConsumptionKWH = forecastedWaterHeatConsumptionKWH;
	}

	//-------------------------------------------------------------------------------------------------
	public long getBuilding() { return building; }
	public long getForecastTime() { return forecastTime; }
	public double getForecastedTotalHeatConsumptionKWH() { return forecastedTotalHeatConsumptionKWH; }
	public double getForecastedWaterHeatConsumptionKWH() { return forecastedWaterHeatConsumptionKWH; }

	//-------------------------------------------------------------------------------------------------
	public void setBuilding(final long building) { this.building = building; }
	public void setForecastTime(final long forecastTime) { this.forecastTime = forecastTime; }
	public void setForecastedTotalHeatConsumptionKWH(final double forecastedTotalHeatConsumptionKWH) { this.forecastedTotalHeatConsumptionKWH = forecastedTotalHeatConsumptionKWH; }
	public void setForecastedWaterHeatConsumptionKWH(final double forecastedWaterHeatConsumptionKWH) { this.forecastedWaterHeatConsumptionKWH = forecastedWaterHeatConsumptionKWH; }	
}
