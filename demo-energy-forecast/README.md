# Energy Forecast SoS Demo (Java Spring-Boot)
##### The project provides Arrowhead v4.1.3 Clients demo implementation developed from [client-skeleton project](https://github.com/arrowhead-f/client-skeleton-java-spring)

## Overview
The goal of the project is simulate how a central heating supplier could consume an 'energy consumption forecast' service which is accomplished by several provider's individual service interopeable collaboration.

##### The Local Cloud Architecture 
![#1589F0](https://placehold.it/15/1589F0/000000?text=+) `AH Service Registry`
![#f03c15](https://placehold.it/15/f03c15/000000?text=+) `AH Authorization` 
![#c5f015](https://placehold.it/15/c5f015/000000?text=+) `AH Orchestrator`
![Alt text](https://github.com/arrowhead-f/sos-examples-spring/blob/energy_forecast_demo_dev/demo-energy-forecast/doc/overview.png)
## Service Descriptions
**indoor-energy-details & outdoor-energy-deatils:**

Provides hourly measured data about a specific building within a specific time frame.
* ***input:*** Query parameters: 

  `builing`={building_id} [*mandatory*]
  
  `from`={uninx_epoch_time_stamp} [*mandatory*]
  
  `to`={uninx_epoch_time_stamp} [*not mandatory*]
  
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

  `builing`={building_id} [*mandatory*]
  
  `timestamp`={uninx_epoch_time_stamp}[*mandatory*]

* ***output:*** EnergyForecastDTO.xml
```
  <EnergyForecastDTO>
    <building>6</building>
    <forecastTime>1570814474</forecastTime>
    <forecastedTotalHeatConsumptionKWH>63.047</forecastedTotalHeatConsumptionKWH>
    <forecastedWaterHeatConsumptionKWH>1.44</forecastedWaterHeatConsumptionKWH>
  </EnergyForecastDTO>
```  

##### The SoS Workflow
![Alt text](https://github.com/arrowhead-f/sos-examples-spring/blob/energy_forecast_demo_dev/demo-energy-forecast/doc/SequenceUML.png)
