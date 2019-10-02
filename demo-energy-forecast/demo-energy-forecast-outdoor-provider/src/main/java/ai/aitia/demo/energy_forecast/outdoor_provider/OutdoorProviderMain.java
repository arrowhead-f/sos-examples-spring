package ai.aitia.demo.energy_forecast.outdoor_provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import eu.arrowhead.common.CommonConstants;
import ai.aitia.demo.energy.forecast.common.EnergyForecastCommonConstants;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, EnergyForecastCommonConstants.BASE_PACKAGE}) //TODO: add custom packages if any
public class OutdoorProviderMain {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		SpringApplication.run(OutdoorProviderMain.class, args);
	}	
}
