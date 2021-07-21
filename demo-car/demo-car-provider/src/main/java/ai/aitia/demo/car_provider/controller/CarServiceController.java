package ai.aitia.demo.car_provider.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import ai.aitia.demo.car_provider.CarProviderConstants;
import ai.aitia.demo.car_provider.database.DTOConverter;
import ai.aitia.demo.car_provider.database.InMemoryCarDB;
import ai.aitia.demo.car_provider.entity.Car;
import eu.arrowhead.common.exception.BadPayloadException;

@RestController
@RequestMapping(CarProviderConstants.CAR_URI)
public class CarServiceController {
	
	//=================================================================================================
	// members	
	
	@Autowired
	private InMemoryCarDB carDB;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public List<CarResponseDTO> getCars(@RequestParam(name = CarProviderConstants.REQUEST_PARAM_BRAND, required = false) final String brand,
													  @RequestParam(name = CarProviderConstants.REQUEST_PARAM_COLOR, required = false) final String color) {
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
		return response;
	}
	
	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = CarProviderConstants.BY_ID_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public CarResponseDTO getCarById(@PathVariable(value = CarProviderConstants.PATH_VARIABLE_ID) final int id) {
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
	@PutMapping(path = CarProviderConstants.BY_ID_PATH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody public CarResponseDTO updateCar(@PathVariable(name = CarProviderConstants.PATH_VARIABLE_ID) final int id, @RequestBody final CarRequestDTO dto) {
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
	@DeleteMapping(path = CarProviderConstants.BY_ID_PATH)
	public void removeCarById(@PathVariable(value = CarProviderConstants.PATH_VARIABLE_ID) final int id) {
		carDB.removeById(id);
	}
}
