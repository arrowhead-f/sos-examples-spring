package ai.aitia.demo.energy_forecast.outdoor_provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import eu.arrowhead.common.CommonConstants;
import ai.aitia.demo.energy.forecast.common.EFCommonConstants;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, EFCommonConstants.BASE_PACKAGE})
public class OutdoorProviderMain {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		SpringApplication.run(OutdoorProviderMain.class, args);
	}	
}
