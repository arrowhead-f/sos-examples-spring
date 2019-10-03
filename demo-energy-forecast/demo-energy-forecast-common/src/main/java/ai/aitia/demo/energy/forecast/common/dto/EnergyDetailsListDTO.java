package ai.aitia.demo.energy.forecast.common.dto;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "EnergyDetailsListDTO")
public class EnergyDetailsListDTO implements Serializable {
	
	//=================================================================================================
	// members

	private static final long serialVersionUID = 323321104311683878L;

	private List<EnergyDetailsDTO> energyDetails;
	
	@JacksonXmlProperty
	private long fromTS;
	
	@JacksonXmlProperty
	private long toTS;
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public EnergyDetailsListDTO() {}
	
	//-------------------------------------------------------------------------------------------------
	public EnergyDetailsListDTO(final List<EnergyDetailsDTO> energyDetails, final long fromTS, final long toTS) {
		this.energyDetails = energyDetails;
		this.fromTS = fromTS;
		this.toTS = toTS;
	}
	
	//-------------------------------------------------------------------------------------------------
	public List<EnergyDetailsDTO> getEnergyDetails() { return energyDetails; }
	public long getFromTS() { return fromTS; }
	public long getToTS() { return toTS; }
	
	//-------------------------------------------------------------------------------------------------
	public void setEnergyDetails(final List<EnergyDetailsDTO> energyDetails) { this.energyDetails = energyDetails; }
	public void setFromTS(final long fromTS) { this.fromTS = fromTS; }
	public void setToTS(final long toTS) { this.toTS = toTS; }	
}
