package eu.arrowhead.application.skeleton.publisher.event;

import java.util.Collections;
import java.util.List;

import org.springframework.util.Assert;

import eu.arrowhead.common.Utilities;

public enum PresetEventType {

	//=================================================================================================
	// elements
	
	START_INIT( EventTypeConstants.EVENT_TYPE_START_INIT, List.of()),
	START_RUN( EventTypeConstants.EVENT_TYPE_START_RUN, List.of() ),
	REQUEST_RECEIVED( EventTypeConstants.EVENT_TYPE_REQUEST_RECEIVED, List.of() ),
	PUBLISHER_DESTROYED( EventTypeConstants.EVENT_TYPE_PUBLISHER_DESTROYED, List.of() );

	//=================================================================================================
	// members
	
	private final String eventTypeName;
	private final List<String> metadataKeys;
	
	//=================================================================================================
	// methods
	
	//-------------------------------------------------------------------------------------------------
	public String getEventTypeName() { return eventTypeName; }
	public List<String> getMetadataKeys() { return metadataKeys; }
	
	//=================================================================================================
	// assistant methods

	//-------------------------------------------------------------------------------------------------
	private PresetEventType( final String eventTypeName, final List<String> metadataKeys ) {
		Assert.isTrue(!Utilities.isEmpty(eventTypeName), "EventType name is invalid.");
		
		this.metadataKeys = metadataKeys != null ? Collections.unmodifiableList(metadataKeys) : List.of();
		this.eventTypeName = eventTypeName;
	}
}