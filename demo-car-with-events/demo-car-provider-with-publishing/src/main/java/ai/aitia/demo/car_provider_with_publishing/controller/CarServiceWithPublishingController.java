package ai.aitia.demo.car_provider_with_publishing.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.demo.car_common.dto.CarRequestDTO;
import ai.aitia.demo.car_common.dto.CarResponseDTO;
import ai.aitia.demo.car_provider_with_publishing.CarProviderWithPublishingConstants;
import ai.aitia.demo.car_provider_with_publishing.database.DTOConverter;
import ai.aitia.demo.car_provider_with_publishing.database.InMemoryCarDB;
import ai.aitia.demo.car_provider_with_publishing.entity.Car;
import eu.arrowhead.application.skeleton.publisher.event.EventTypeConstants;
import eu.arrowhead.application.skeleton.publisher.event.PresetEventType;
import eu.arrowhead.application.skeleton.publisher.service.PublisherService;
import eu.arrowhead.common.exception.BadPayloadException;

@RestController
@RequestMapping(CarProviderWithPublishingConstants.CAR_URI)
public class CarServiceWithPublishingController {
	
	//=================================================================================================
	// members

	
	private static int counter = 0;
	
	@Autowired
	private InMemoryCarDB carDB;
	
	@Autowired
	private PublisherService publisherService;
	
	@Value( CarProviderWithPublishingConstants.$SERVICE_LIMIT_WD )
	private int serviceLimit;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public List<CarResponseDTO> getCars(@RequestParam(name = CarProviderWithPublishingConstants.REQUEST_PARAM_BRAND, required = false) final String brand,
													  @RequestParam(name = CarProviderWithPublishingConstants.REQUEST_PARAM_COLOR, required = false) final String color) {
		++counter;
		
		publisherService.publish(PresetEventType.REQUEST_RECEIVED, Map.of(EventTypeConstants.EVENT_TYPE_REQUEST_RECEIVED_METADATA_REQUEST_TYPE, HttpMethod.GET.name()), CarProviderWithPublishingConstants.CAR_URI);
		
		final List<CarResponseDTO> response = new ArrayList<>();
		for (final Car car : carDB.getAll()) {
			boolean toAdd = true;
			if (brand != null && !brand.isBlank() && !car.getBrand().equalsIgnoreCase(brand)) {
				toAdd = false;
			}
			if (color != null && !color.isBlank() && !car.getColor().equalsIgnoreCase(color)) {
				toAdd = false;
			}
			if (toAdd) {
				response.add(DTOConverter.convertCarToCarResponseDTO(car));
			}
		}
		
		if (counter > serviceLimit) {
			System.exit(0);
		}
		
		return response;
	}
	
	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = CarProviderWithPublishingConstants.BY_ID_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public CarResponseDTO getCarById(@PathVariable(value = CarProviderWithPublishingConstants.PATH_VARIABLE_ID) final int id) {
		return DTOConverter.convertCarToCarResponseDTO(carDB.getById(id));
	}
	
	//-------------------------------------------------------------------------------------------------
	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public CarResponseDTO createCar(@RequestBody final CarRequestDTO dto) {
		if (dto.getBrand() == null || dto.getBrand().isBlank()) {
			throw new BadPayloadException("brand is null or blank");
		}
		if (dto.getColor() == null || dto.getColor().isBlank()) {
			throw new BadPayloadException("color is null or blank");
		}
		final Car car = carDB.create(dto.getBrand(), dto.getColor());
		
		return DTOConverter.convertCarToCarResponseDTO(car);
	}
	
	//-------------------------------------------------------------------------------------------------
	@PutMapping(path = CarProviderWithPublishingConstants.BY_ID_PATH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public CarResponseDTO updateCar(@PathVariable(name = CarProviderWithPublishingConstants.PATH_VARIABLE_ID) final int id, @RequestBody final CarRequestDTO dto) {
		if (dto.getBrand() == null || dto.getBrand().isBlank()) {
			throw new BadPayloadException("brand is null or blank");
		}
		if (dto.getColor() == null || dto.getColor().isBlank()) {
			throw new BadPayloadException("color is null or blank");
		}
		final Car car = carDB.updateById(id, dto.getBrand(), dto.getColor());
		
		return DTOConverter.convertCarToCarResponseDTO(car);
	}
	
	//-------------------------------------------------------------------------------------------------
	@DeleteMapping(path = CarProviderWithPublishingConstants.BY_ID_PATH)
	public void removeCarById(@PathVariable(value = CarProviderWithPublishingConstants.PATH_VARIABLE_ID) final int id) {
		carDB.removeById(id);
	}
}