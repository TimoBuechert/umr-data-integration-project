# Data Integration Service

This service is responsible for the multiple steps of the data integration process.
It is implemented in Java and uses Spring Batch. The implementation needs a running database, which is shipped
as a docker container. The database and the Spring batch application are started with the following command:

```shell
cd ../4_data_integration_service
docker-compose up -d
./gradlew bootRun
```