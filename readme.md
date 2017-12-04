# Zuul

It is a Spring boot application based on Spring Cloud Netflix Zuul to provide an out of the box Edge Router/ load balancer.
It takes care of routing incoming request to the proper inner service instance, based on url request destination, BESIDES it provides load-balancing between inner services instances
As it is itself a Eureka client, inner services URL are not predefined on application.yml, there are known on the fly as provided by Eureka Server (assuming inner services are Eureka clients). 

## Getting Started

Clone code: 
```
git clone https://github.com/mbolori/Zuul.git
``` 

### Prerequisites

You need maven installed on local.
In order to build the project: 
```
mvn clean install
```

### Running program

In order to run jar:
```
java -jar Zuul-1.0.0-SNAPSHOT.jar start --spring.config.location=/<path-to-configuration/application.yml  
```

It is not required to provide external configuration if Zuul is configured to route request towards service spring-boot-palindrome on path /spring-boot-palindrome/api/**
```
zuul:
    ignoredServices: '*'
    routes:
        spring-boot-palindrome:
            path:       /spring-boot-palindrome/api/**
            sensitiveHeaders: 
            serviceId:  spring-boot-palindrome
            stripPrefix: false
```

## Configuration

Most of the configuration is well known boilerplate configuration such spring.info, spring.profile, spring.application, actuator's endpoints configuration, server.port, etc.
What is specific? 

### Ribbon Configuration
Netflix Ribbon is a client side load balancer. It can be defined connect and read timeouts when working with inner services.
Some parameters can be tweaked. Follow this pattern: <clientName>.<nameSpace>.<propertyName>=<value>. If no nameSpace will affect all clients.
Some examples are <client>.ribbon.ConnectTimeout,  <client>.ribbon.ReadTimeout, <client>.ribbon.MaxAutoRetries, <client>.ribbon.MaxAutoRetriesNextServer, etc..
```
ribbon:  
    ReadTimeout: 30000    
    ConnectTimeout: 30000   
```

### Hystrix Configuration
Hystrix wraps request in Hystrix commands, those allow for circuit breakers and other strategies to avoid a failing inner service can compromise the whole infraestructure, like a kind of bulkhead.
```
hystrix.command.default.execution.timeout.enabled: false  /////Disable Hystrix timeout globally 
```

### Zuul Configuration 
It is an edge-router, so takes care of routing, based on matching request url against a set of predefined patterns. As this edge-router is an Eureka client, inner services are **KNOWN** based on services names instead of fixed url.
```
zuul:
    ignoredServices: '*'
    routes:
        spring-boot-palindrome:
            path:       /spring-boot-palindrome/api/**
            sensitiveHeaders: 
            serviceId:  spring-boot-palindrome
            stripPrefix: false

```


### Eureka Client Configuration
This Zuul implementation uses Eureka service registry to have a dynamic set of ServerList availables.
It is configured Eureka Server Url to register with, Zuul will "ping" periodically to avoid being removed from Eureka Server registry. 
eureka:
```
eureka:
    client:
        serviceUrl:
            defaultZone:    http://localhost:8761/eureka
        healthcheck:
            enabled: true
        instance:
            leaseRenewalIntervalInSeconds: 15
            preferIpAddress:  true
```

## Authors

* **Manuel Baeta** - *Initial work* 

