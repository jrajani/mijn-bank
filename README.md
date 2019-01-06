# mijn-bank

## Pre-requisites
- Axon Server 4.x
- MySQL
- Java 8

## Running the application

1. Run Axon Server

```java
java -jar axonserver-4.0.jar
```
2. Start mysql
3. Start mb-query

```maven
mvn clean spring-boot:run 
```
4. Start mb-accounts

```maven
mvn clean spring-boot:run 
```

5. Start mb-transactions

```maven
mvn clean spring-boot:run 
```

7. Start mb-ui

```maven
mvn clean spring-boot:run 
```

## Useful links - Local Server

- [Axon Server](http://localhost:8024)
- [UI](http://localhost:8080)
