# camel-spring-jpa-order-fulfillment-processor
Spring app that integrates with various Camel routes:
* Camel RouteBuilder for routing orders from SQL Server to ActiveMQ.
* Camel RouteBuilder to implement a Content-Based Router.
* Camel RouteBuilder for routing orders from ActiveMQ to an SFTP server.
* Camel RouteBuilder for routing orders from ActiveMQ to a REST endpoint.

Build war file:

    mvn package war:war
    
Generate Java classes from XSD using jaxb2-maven-plugin:
    
    mvn jaxb2:xjc
