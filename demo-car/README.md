# Car Demo (Java Spring-Boot)
##### The project provides Arrowhead Application demo implementation developed from [application-skeleton project](https://github.com/arrowhead-f/client-skeleton-java-spring)

## Overview
The goal of the project is to simply demonstrate how a consumer could orchestrate for a service and consume it afterward.
##### The Local Cloud Architecture 
🟦 `AH Service Registry`
🟥 `AH Authorization` 
🟩 `AH Orchestrator`
![Alt text](https://github.com/arrowhead-f/sos-examples-spring/blob/master/demo-car/doc/overview.png)

## Service Descriptions
**create-car:**

Creates a new car instance.
* ***input:*** CarRequestDTO.json
```
{
   "brand":"string",
   "color":"string"
}
```
* ***output:*** CarResponseDTO.json
```
{
   "id":"integer",
   "brand":"string",
   "color":"string"
}
```

**get-car:**

Returns a car list based on the given parameters.
* ***input:*** Query parameters: 

  `brand`={brand} [*not mandatory*]
  
  `color`={color} [*not mandatory*]

* ***output:*** List of CarResponseDTO.json
```
[{
   "id":"integer",
   "brand":"string",
   "color":"string"
}]
```

## How to run?
1. Clone this repo to your local machine.
2. Go to the root directory and execute `mvn install` command, then wait until the build succeeds.
3. Start the [Arrowhead Framework v4.1.3](https://github.com/arrowhead-f/core-java-spring), before you would start the demo.
   Required core systems:
   * Service Registry
   * Authorization
   * Orchestration
4. Start the provider (it will registrate automatically to the Service Registry Core System).
5. At the very first time, register the consumer manually and create the intra cloud authorization rules.
6. Start the Consumer.

## Video tutorial
[link](https://www.youtube.com/watch?v=9BHemnv3mQA&t=5s)
