# Arrowhead Framework System of Systems Example Projects (Java Spring-Boot)

### Current examples in this repository

1. Car demo ([read me](https://github.com/arrowhead-f/sos-examples-spring/blob/master/demo-car/README.md))
2. Energy Forecast demo ([read me](https://github.com/arrowhead-f/sos-examples-spring/blob/master/demo-energy-forecast/README.md))
3. Car demo with events ([read me](https://github.com/arrowhead-f/sos-examples-spring/blob/master/demo-car-with-events/README.md))
4. Exchange Rate Intercloud demo ([read me](https://github.com/arrowhead-f/sos-examples-spring/blob/master/demo-exchange-rate-intercloud/README.md))

### Requirements

The project has the following dependencies:
* **JRE/JDK 11** [Download from here](https://www.oracle.com/technetwork/java/javase/downloads/jdk11-downloads-5066655.html)
* **Maven 3.5+** [Download from here](http://maven.apache.org/download.cgi) | [Install guide](https://www.baeldung.com/install-maven-on-windows-linux-mac)
* **GitHub Packages** [Configuring Maven for GitHub Packages](https://help.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-apache-maven-for-use-with-github-packages) | [Example](https://github.com/arrowhead-f/client-library-java-spring/blob/master/docs/example_mvn_settings.xml)

* **Authorization settings for the demo systems in your local arrowhead database**

  ***By Arrowhead Management Tool***
  - coming soon
  
  ***By Swagger API documentation***
  
  - Go to `http(s)://<authorization_host>:<authorization_port>`.
  - Create the authorization rule via the [`POST /authorization/mgmt/intracloud`](https://github.com/arrowhead-f/core-java-spring/blob/master/README.md#add-intracloud-rules) or [`POST /authorization/mgmt/intercloud`](https://github.com/arrowhead-f/core-java-spring/blob/master/README.md#add-intercloud-rules) endpoint.
  
  ***By MySQL queries***
  
  *Intra-Cloud:*
  - Insert a new entry with the consumer details into the `system_` table.
  - Insert a new entry with the IDs of consumer entry, provider entry and the service definition entry into the `authorization_intra_cloud` table.
  - Insert a new entry with the IDs of authorization intra cloud entry and service interface entry into the `authorization_intra_cloud_interface_connection` table.
  
  *Inter-Cloud:*
  - Insert a new entry with the cloud details into the `cloud` table. The `authentication_info` have to be filled out with the gatekeper's public key of the cloud.
  - Insert a new entry with the IDs of the cloud entry, provider entry and the service definition entry into the `authorization_inter_cloud` table.
  - Insert a new entry with the IDs of authorization inter cloud entry and service interface entry into the `authorization_inter_cloud_interface_connection` table.
