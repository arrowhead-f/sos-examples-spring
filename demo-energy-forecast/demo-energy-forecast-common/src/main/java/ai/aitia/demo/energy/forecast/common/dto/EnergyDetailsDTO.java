package ai.aitia.demo.energy.forecast.common.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "EnergyDetailsDTO")
public class EnergyDetailsDTO {
	
	//=================================================================================================
	// members
	
	@JacksonXmlProperty
	private Double inTemp;
	
	@JacksonXmlProperty
	private Double outTemp;

	@JacksonXmlProperty
	private double total;
	
	@JacksonXmlProperty
	private double water;
	
	@JacksonXmlProperty
	private long timestamp;
	
	@JacksonXmlProperty
	private long building;
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public Double getInTemp() { return inTemp; }
	public Double getOutTemp() { return outTemp; }
	public double getTotal() { return total; }
	public double getWater() { return water; }
	public long getTimestamp() { return timestamp; }
	public long getBuilding() { return building; }
	
	//-------------------------------------------------------------------------------------------------
	public void setInTemp(final Double inTemp) { this.inTemp = inTemp; }
	public void setOutTemp(final Double outTemp) { this.outTemp = outTemp; }
	public void setTotal(final double total) { this.total = total; }
	public void setWater(final double water) { this.water = water; }
	public void setTimestamp(final long timestamp) { this.timestamp = timestamp; }
	public void setBuilding(final long building) { this.building = building; }
	
	//=================================================================================================
	// assistant methods
	
	//-------------------------------------------------------------------------------------------------
	private EnergyDetailsDTO(final Builder builder) {
		this.inTemp = builder.inTemp;
		this.outTemp = builder.outTemp;
		this.total = builder.total;
		this.water = builder.water;
		this.timestamp = builder.timestamp;
		this.building = builder.building;
	}
	
	//=================================================================================================
	// nested classes
	
	//-------------------------------------------------------------------------------------------------
	public static class Builder {
		
		//=================================================================================================
		// members
		
		private Double inTemp;
		private Double outTemp;
		private double total;
		private double water;
		private final long timestamp;
		private final long building;
		
		//=================================================================================================
		// methods

		//-------------------------------------------------------------------------------------------------
		public Builder(final long timestamp, final long building) {
			this.timestamp = timestamp;
			this.building = building;
		}
		
		//-------------------------------------------------------------------------------------------------
		public Builder setInTemp(final double inTemp) {
			this.inTemp = inTemp;
			return this;
		}
		
		//-------------------------------------------------------------------------------------------------
		public Builder setOutTemp(final double outTemp) {
			this.outTemp = outTemp;
			return this;
		}
		
		//-------------------------------------------------------------------------------------------------
		public Builder setTotal(final double total) {
			this.total = total;
			return this;
		}
		
		//-------------------------------------------------------------------------------------------------
		public Builder setWater(final double water) {
			this.water = water;
			return this;
		}
		
		//-------------------------------------------------------------------------------------------------
		public EnergyDetailsDTO build() {
			return new EnergyDetailsDTO(this);
		}
	}	
}
