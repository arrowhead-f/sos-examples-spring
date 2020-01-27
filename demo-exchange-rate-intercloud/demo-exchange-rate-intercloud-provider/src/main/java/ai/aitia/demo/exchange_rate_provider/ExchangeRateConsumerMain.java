package ai.aitia.demo.exchange_rate_provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import eu.arrowhead.common.CommonConstants;

@SpringBootApplication
@ComponentScan(basePackages = {CommonConstants.BASE_PACKAGE, Constants.BASE_PACKAGE})
public class ExchangeRateConsumerMain {

	//=================================================================================================
	// methods

	//-------------------------------------------------------------------------------------------------
	public static void main(final String[] args) {
		SpringApplication.run(ExchangeRateConsumerMain.class, args);
	}	
}
