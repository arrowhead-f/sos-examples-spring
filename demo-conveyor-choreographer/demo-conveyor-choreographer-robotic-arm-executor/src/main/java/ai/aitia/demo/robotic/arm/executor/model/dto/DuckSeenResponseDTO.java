package ai.aitia.demo.robotic.arm.executor.model.dto;

public class DuckSeenResponseDTO {

	//=================================================================================================
	// members
	
	private boolean seen;
	private CoordinateDTO coordinate;
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public boolean isSeen() { return seen; }
	public CoordinateDTO getCoordinate() { return coordinate; }
	
	//-------------------------------------------------------------------------------------------------
	public void setSeen(boolean seen) { this.seen = seen; }
	public void setCoordinate(CoordinateDTO coordinate) { this.coordinate = coordinate; }
}
