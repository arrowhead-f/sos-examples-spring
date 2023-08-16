# Energy Forecast Demo (Java Spring-Boot)
##### The project provides Arrowhead Application demo implementation developed from [application-skeleton project](https://github.com/arrowhead-f/client-skeleton-java-spring)

## Overview
The goal of the project is to simulate how a central heating supplier could consume an 'energy consumption forecast' service which is accomplished by several provider's individual service interoperable collaboration.

##### The Local Cloud Architecture 
🟦 `AH Service Registry`
🟥 `AH Authorization` 
🟩 `AH Orchestrator`
![Alt text](https://github.com/arrowhead-f/sos-examples-spring/blob/master/demo-energy-forecast/doc/overview.png)
## Service Descriptions
**indoor-energy-details & outdoor-energy-deatils:**

Provides hourly measured data about a specific building within a specific time frame.
* ***input:*** Query parameters: 

  `building`={building_id} [*mandatory*]
  
  `from`={unix_epoch_time_stamp} [*mandatory*]
  
  `to`={unix_epoch_time_stamp} [*not mandatory*]
  
* ***output:*** EnergyDetailsListDTO.xml
```
<EnergyDetailsListDTO>
  <energyDetails>
    <energyDetails>
      <inTemp>22.0</inTemp>
      <outTemp>9.6</outTemp>
      <total>12.04848</total>
      <water>0.204</water>
      <timestamp>1570788000</timestamp>
      <building>6</building>
    </energyDetails>
  </energyDetails>
  <fromTS>1570788000</fromTS>
  <toTS>1570788000</toTS>
</EnergyDetailsListDTO>
```
**energy-forecast-details**

Forcasts the energy consumption between the current time and the given time in the future. Forecast is calculated by a simple algorithm which requests historical data from indoor and outdoor providers.
* ***input:*** Query parameters: 

  `building`={building_id} [*mandatory*]
  
  `timestamp`={unix_epoch_time_stamp}[*mandatory*]

* ***output:*** EnergyForecastDTO.xml
```
  <EnergyForecastDTO>
    <building>6</building>
    <forecastTime>1570814474</forecastTime>
    <forecastedTotalHeatConsumptionKWH>63.047</forecastedTotalHeatConsumptionKWH>
    <forecastedWaterHeatConsumptionKWH>1.44</forecastedWaterHeatConsumptionKWH>
  </EnergyForecastDTO>
```  

##### The System of Systems (SoS) Workflow
![Alt text](https://github.com/arrowhead-f/sos-examples-spring/blob/master/demo-energy-forecast/doc/SequenceUML.png)

## How to run?
1. Clone this repo to your local machine.
2. Go to the root directory and execute `mvn install` command, then wait until the build succeeds.
3. Start the [Arrowhead Framework](https://github.com/eclipse-arrowhead/core-java-spring), before you would start the demo.
   Required core systems:
   * Service Registry
   * Authorization
   * Orchestration
4. At the very first time, register the consumer manually into the Service Registry Core System.
5. Start the demo:
   * Run `start_energy_forecast_clients.bat` or `start_energy_forecast_clients.sh`
     
     It will start the Indoor, the Outdoor, and the Energy Forecast provider in the background and will start a command line for the Consumer.
     
   * Or, enter to each `target` folder and run the jar files.
6. At the very first time, create the intra cloud authorization rules.
7. Use the command line commands to trigger the SoS workflow.
8. *(You can observe what happens in Arrowhead Framework if you switch the logging level to "DEBUG".)*

## Configuration
  - Find the `application.properties` confirguration file under the `<project>/src/main/resources` folder before the build or under the `<project>/target` after the build.
  - Default configuration is provided out of the box which works when the Arrowhead Local Cloud is running on your localhost and has the common [testclou2 certificates](https://github.com/eclipse-arrowhead/core-java-spring/tree/master/certificates/testcloud2). 

