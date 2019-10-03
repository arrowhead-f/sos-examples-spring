package ai.aitia.demo.energy_forecast.indoor_provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import ai.aitia.demo.energy.forecast.common.EFCommonConstants;
import eu.arrowhead.common.CommonConstants;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, EFCommonConstants.BASE_PACKAGE})
public class IndoorProviderMain {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		SpringApplication.run(IndoorProviderMain.class, args);
	}	
}
