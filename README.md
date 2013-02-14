# AeroGear Controller - very lean mvc controller
[AeroGear](http://aerogear.org) Controller is a very lean mvc controller written in java. It focuses on the routing of HTTP request to plain java object endpoint
and the handling of the results, by either forwarding the data to a view, or returning the data in the format requested by the caller.

## Installation
1. Add the following maven dependency

        <dependency>
            <groupId>org.jboss.aerogear</groupId>
            <artifactId>aerogear-controller</artifactId>
            <version>1.0.0.M8</version>
            <scope>compile</scope>
        </dependency>
        
1. Since AeroGear Controller uses CDI it is required that a ```beans.xml``` file exists in the ```WEB-INF``` folder

        <beans xmlns="http://java.sun.com/xml/ns/javaee"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/beans_1_0.xsd">
        </beans>  

## Usage
1. Create a pojo controller

        public class Home {
            public void index() {
            }
        }

1. Create a Java class containing the routes (must extend `AbstractRoutingModule`)

        public class Routes extends AbstractRoutingModule {

        @Override
        public void configuration() {
            route()
                   .from("/")
                   .on(RequestMethod.GET)
                   .to(Home.class).index();
            }
        }

1. Create a jsp page at `/WEB-INF/pages/<Controller Class Name>/<method>.jsp`

        <!-- /WEB-INF/pages/Home/index.jsp -->
        <html>
            <body>
                <p>hello from index!</p>
            </body>
        </html>
        
For information about creating RESTful routes, please refer to the [User Guide]((http://aergear.org/docs/guides/aerogear-controller/).
        
### Populating parameters

You can use immutable beans straight away as controller parameters:

        public class Store {
            public Car save(Car car) {
                return car;
            }
        }

This can be populated by configuring a route to handle POST requests:

        route()
               .from("/cars")
               .on(RequestMethod.POST)
               .to(Store.class).save(param(Car.class));

And you can use a simple html form for it, by just following the convention:

            <input type="text" name="car.color"/>
            <input type="text" name="car.brand"/>

The car object will be automatically populated with the provided values - note that it supports deep linking, so this would work fine too:

            <input type="text" name="car.brand.owner"/>

All the intermediate objects are created automatically.  
  

## Documentation
* [User Guide](http://aergear.org/docs/guides/aerogear-controller/)
* [API](http://aerogear.org/docs/specs/aerogear-controller)
* [REST API](http://aerogear.org/docs/specs/aerogear-rest-api)

## Community
* [User Forum](https://community.jboss.org/en/aerogear?view=discussions)
* [Developer Mailing List](http://aerogear-dev.1069024.n5.nabble.com)

## Issue Tracker
* [JIRA](https://issues.jboss.org/browse/AEROGEAR)

## Examples
* [aerogear-controller-demo](https://github.com/aerogear/aerogear-controller-demo) 
