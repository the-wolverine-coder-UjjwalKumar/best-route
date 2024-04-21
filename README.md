# Project Title: Best-Route API

## Overview

This project offers a solution for optimizing delivery routes to ensure efficient order fulfillment within the shortest possible time.

## Installation

For detailed instructions on installation and setup, refer to the following guides:
- [Using Spring Data JDBC](https://github.com/spring-projects/spring-data-examples/tree/master/jdbc/basics)
- [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
- [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
- [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
- [Producing a SOAP web service](https://spring.io/guides/gs/producing-web-service/)

## Usage

After running the project, the server will be available on port 5600. Use the provided API endpoint `/api/v1/order/best-route` with the necessary prior information to calculate the optimal delivery route.

## Assumptions

1. All orders are assumed to be assigned to a Delivery Captain, with provisions for extending support to multiple delivery partners.
2. Orders are assumed to be in the PLACED state, with successful state transitions not implemented in the current version.
3. The live location of the delivery captain is assumed to be updated via a service.
4. Initial locations for Aman, Restaurant R1, R2, and Customers C1, C2 are provided.
   - Aman: Kormangala ("latitude": "12.93453","longitude": "77.62657")
   - R1: Surjapur ("latitude": "12.93371", "longitude": "77.66219")
   - R2: Marahthahlli ("latitude": "12.95184", "longitude": "77.69957")
   - C1: KR Puram ("latitude": "13.00472", "longitude": "77.68759")
   - C2: Whitefield ("latitude": "12.97138", "longitude": "77.75013")
5. Assigned restaurants are picked randomly (equal chance to each restaurant) and are ready to serve the order, with its `average_preperation_time`.
6.
## Code Structure

![Code Structure pic](https://raw.githubusercontent.com/the-wolverine-coder-UjjwalKumar/best-route/main/code_structure.png)

## Production Quality

The code follows industry best practices and standards, including robust error-handling mechanisms, efficient memory management, and scalability considerations. Minor modifications can make it production-ready.

## Readability and Modularity

The code is organized for readability and modularity, with meaningful variable/function names, modular design, and comprehensive comments/documentation.

## Testing

JUnit testing is integrated, with test cases added in a JSON file for validation.

## Extensibility

The project is designed for easy extension by adding new core features.

## Conclusion

This project has provided valuable insights into the Haversine Geo Location formula, Spring unit testing, API development, and microservices architecture.

## Contributors

[GitHub Ujjwalk's profile](https://github.com/the-wolverine-coder-ujjwalkumar)
