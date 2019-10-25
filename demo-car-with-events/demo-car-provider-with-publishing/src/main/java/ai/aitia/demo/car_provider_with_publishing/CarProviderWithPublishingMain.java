package ai.aitia.demo.car_provider_with_publishing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import eu.arrowhead.common.CommonConstants;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, CarProviderWithPublishingConstants.BASE_PACKAGE})
public class CarProviderWithPublishingMain {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		SpringApplication.run(CarProviderWithPublishingMain.class, args);
	}	
}
