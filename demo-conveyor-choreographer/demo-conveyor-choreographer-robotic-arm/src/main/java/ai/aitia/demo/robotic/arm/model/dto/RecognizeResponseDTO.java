package ai.aitia.demo.robotic.arm.model.dto;

public class RecognizeResponseDTO {

	//=================================================================================================
	// members
	
	private boolean recognized;
	private CoordinateDTO coordinate;
	
	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public boolean isRecognized() { return recognized; }
	public CoordinateDTO getCoordinate() { return coordinate; }
	
	//-------------------------------------------------------------------------------------------------
	public void setRecognized(boolean recognized) { this.recognized = recognized; }
	public void setCoordinate(CoordinateDTO coordinate) { this.coordinate = coordinate; }
}
