# Car Demo With Events (Java Spring-Boot)
##### The project provides Arrowhead Application demo implementation developed from [application-skeleton project](https://github.com/arrowhead-f/client-skeleton-java-spring)

## Overview
The goal of the original Car Demo project is to simply demonstrate how a consumer could orchestrate for service and consume it afterward.

The goal of the Events addition is to simply demonstrate how would a consumer subscribe to events and receive events, and how would a producer publish events.


##### The Local Cloud Architecture 
🟦 `AH Service Registry`
🟥 `AH Authorization` 
🟩 `AH Orchestrator`
🟨 `AH Event Handler`
![Alt text](https://github.com/arrowhead-f/sos-examples-spring/blob/master/demo-car-with-events/doc/overview.png)

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

3. Start the [Arrowhead Framework](https://github.com/eclipse-arrowhead/core-java-spring), before you would start the demo. Required core systems:
   * Service Registry
   * Authorization
   * Orchestration
   * Event Handler

4. ( Optional ) Set service_limit property at the provider's application.properties.
   * The provider will terminate after it served the number of requests given in service_limit property.
   
5. Start the provider (it will do the registration automatically to the Service Registry Core System).

6. ( Optional ) Set max_retry property at the consumer's application.properties.
   * The consumer will terminate after it performed the number of consecutive unsuccessful orchestration, given in max_retry.
   
7. ( Optional ) Set reorchestration property at the consumer's application.properties.
   * The consumer will terminate after it received a `PUBLISHER_DESTROYED` event if reorchestration is set to false.
   
8. For the very first time, register the consumer manually and create the `intracloud`  authorization rules.

9. Start the Consumer.

## Configuration
  - Find the `application.properties` confirguration file under the `<project>/src/main/resources` folder before the build or under the `<project>/target` after the build.
  - Default configuration is provided out of the box which works when the Arrowhead Local Cloud is running on your localhost and has the common [testclou2 certificates](https://github.com/eclipse-arrowhead/core-java-spring/tree/master/certificates/testcloud2). 
