package ai.aitia.demo.exchange_rate_provider.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ai.aitia.demo.exchange_rate_provider.Constants;

@RestController
public class ExchangeRateServiceController {
	
	//=================================================================================================
	// members

	final double hufEur = 0.0028;
	final double eurHuf = 356.78;

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	@GetMapping(path = Constants.GET_EXCHANGE_RATE_SERVICE_URI, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getExchangeRate(@RequestParam (name = Constants.REQUEST_PARAM_CURRENCY_RELATION) final String currencyRelation) {
		if (currencyRelation.equalsIgnoreCase(Constants.REQUEST_PARAM_HUF_EUR_VALUE)) {
			return new ResponseEntity<String>(String.valueOf(hufEur), HttpStatus.OK);
		} else if (currencyRelation.equalsIgnoreCase(Constants.REQUEST_PARAM_EUR_HUF_VALUE)) {
			return new ResponseEntity<String>(String.valueOf(eurHuf), HttpStatus.OK);
		} else {
			return new ResponseEntity<String>("Currency relation not known", HttpStatus.BAD_REQUEST);
		}
	}
}
