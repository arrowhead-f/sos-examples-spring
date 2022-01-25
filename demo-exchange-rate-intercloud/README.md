# Exchange Rate Intercloud Demo (Java Spring-Boot)
##### The project provides Arrowhead Application demo implementation developed from [arrowhead-skeleton project](https://github.com/arrowhead-f/client-skeleton-java-spring)

## Overview
The goal of the project is to simply demonstrate how a consumer could proceed an **inter-cloud orchestration** for a service and consume it afterward.

## Service Descriptions
**exchange-rate:**

Returns the rate based on the given parameters.
* ***input:*** Query parameters: 

  `currency-relation`={relation} *[possible values: EUR-HUF, HUF-EUR]*
 
* ***output:*** String value

## How to run?
1. Install two instances of [Arrowhead Framework](https://github.com/eclipse-arrowhead/core-java-spring) one with [testcloud1 certificates](https://github.com/eclipse-arrowhead/core-java-spring/tree/master/certificates/testcloud1) and another with [testcloud2 certificates](https://github.com/eclipse-arrowhead/core-java-spring/tree/master/certificates/testcloud2). It's recommended to have the two arrowhead clouds separated on different machines. 
   Required core systems:
   * Service Registry
   * Authorization
   * Orchestration
   * Gatekeeper
   * Gateway
2. At the very first time, [**configure the Gatekeeper and Gateway Core Systems**](https://github.com/eclipse-arrowhead/core-java-spring/blob/master/documentation/gatekeeper/GatekeeperSetup.md).
3. Clone or fork this repo to your local machine and set the Service Registry addresses & ports in the **application.properties** located in `src/main/resources folder`.
   * Consumer is belonged to TestCloud1
   * Provider is belonged to TestCloud2
4. Go to the root directory and execute `mvn clean install` command, then wait until the build succeeds.
5. Start the **provider** (it will registrate automatically into the Service Registry Core System of **TestCloud2**).
6. At the very first time, [register](https://github.com/eclipse-arrowhead/core-java-spring#serviceregistry_endpoints_post_systems) the **consumer into TestCloud1** and create the [inter-cloud authorization rules](https://github.com/eclipse-arrowhead/core-java-spring#authorization_endpoints_post_intercloud).
7. Start the Consumer.
