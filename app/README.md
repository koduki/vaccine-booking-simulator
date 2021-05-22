# booking-simulator

This project is simulator to estimate load of booking site. 

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package && /mvnw package -Dquarkus.package.type=uber-jar
```

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

## Build and Deploy to Cloud Run

Environment paramater

```
PROJECT=$(gcloud config get-value project)
REGION=us-central1
ZONE=${REGION}-b
DB_PASS=abcd12345
```

### Cloud SQL

```bash
gcloud sql instances create pginstance --database-version=POSTGRES_12 --tier=db-custom-2-7680 --region=$REGION
gcloud sql users set-password postgres --host=% --instance pginstance --password $DB_PASS
```

You can find custome instance tier from [here](https://cloud.google.com/sql/docs/mysql/instance-settings).

### Cloud Run

```
./mvnw package && ./mvnw package -Dquarkus.package.type=uber-jar
docker build -t gcr.io/${PROJECT}/vaccine-booking-simulator -f src/main/docker/Dockerfile.jvm . 
docker push gcr.io/${PROJECT}/vaccine-booking-simulator

gcloud run deploy vaccine-booking-simulator \
  --image gcr.io/${PROJECT}/vaccine-booking-simulator \
  --region ${REGION} \
  --platform managed \
  --allow-unauthenticated \
  --add-cloudsql-instances ${PROJECT}:${REGION}:pginstance \
  --update-env-vars INSTANCE_CONNECTION_NAME="${PROJECT}:${REGION}:pginstance"

gcloud run services update vaccine-booking-simulator --region us-central1 --platform managed --update-env-vars QUARKUS_DATASOURCE_JDBC_URL="jdbc:postgresql:///exampledb?ipTypes=PUBLIC&cloudSqlInstance=${PROJRCT}:${REGION}:pginstance&socketFactory=com.google.cloud.sql.postgres.SocketFactory
",QUARKUS_DATASOURCE_USERNAME=postgres,QUARKUS_DATASOURCE_PASSWORD=${DB_PASSWORD}
```

