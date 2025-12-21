# Vending Machine Telemetry System

This project consists of two microservices to handle telemetry data from vending machines.

## Architecture
1. **Telemetry Producer**: Receives telemetry data via REST API and sends it to a Kafka topic (`vending-telemetry`).
2. **Telemetry Consumer**: Consumes data from Kafka and persists it to a SQL Server database.

## Prerequisites
- Java 8+
- Maven
- Docker (for Kafka and SQL Server)

## Tech Stack
- **Spring Boot 2.7.18**
- **Java 1.8**
- **Spring Kafka**
- **Spring Data JPA**
- **SQL Server**

## Getting Started

### 1. Start Infrastructure
Run the following command to start Kafka (KRaft mode), Kafka UI, and SQL Server:
```bash
docker-compose up -d
```
- **Kafka UI**: Available at [http://localhost:8080](http://localhost:8080)
- **Kafka Broker**: `localhost:9092`
- **SQL Server**: `localhost:1433`

#### Database Setup
Run the following command to create the database:

1. Create Database
```
docker exec -it kafka-learn-sqlserver-1 /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "Okay@1234" -C -Q "CREATE DATABASE VendingDB;"
```

2. Create Table
```
docker exec -it kafka-learn-sqlserver-1 /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "Okay@1234" -C -d VendingDB -Q "CREATE TABLE vending_telemetry (id BIGINT IDENTITY(1,1) PRIMARY KEY, machine_id NVARCHAR(255), timestamp DATETIME2, temperature FLOAT, inventory_json NVARCHAR(MAX), status NVARCHAR(255));"
```

3. Verify Table
```
docker exec -it kafka-learn-sqlserver-1 /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "Okay@1234" -C -d VendingDB -Q "SELECT name FROM sys.tables;"
```
#### Kafka Setup
Run the following command to create the topic:
```
docker exec -it kafka-learn-kafka-1 kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic vending-telemetry
```

### 2. Run the Producer
```bash
cd telemetry-producer
mvn spring-boot:run
```
The producer will be available at `http://localhost:8081`.

### 3. Run the Consumer
```bash
cd telemetry-consumer
mvn spring-boot:run
```
The consumer will start listening to Kafka and saving to SQL Server.

## Testing the System

You can send a sample telemetry payload using `curl`:

```bash
curl -X POST http://localhost:8081/api/telemetry \
-H "Content-Type: application/json" \
-D '{
  "machineId": "VM-001",
  "temperature": 4.5,
  "inventory": {
    "Soda": 10,
    "Sparkling Water": 8,
    "Water": 15
  },
  "status": "OPERATIONAL"
}'
```

Check the producer logs to see if the message was sent successfully:
```bash
docker logs kafka-learn-telemetry-producer-1
```

Check the kafka UI to see if the message was received successfully:
```bash
http://localhost:8080
```

Check the consumer logs to see if the message was received successfully:
```bash
docker logs kafka-learn-telemetry-consumer-1
```

Check the SQL Server to see if the message was received successfully:
```bash
docker exec -it kafka-learn-sqlserver-1 /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "Okay@1234" -C -d VendingDB -Q "SELECT * FROM vending_telemetry;"
```

## Cleanup
```bash
docker-compose down
```

## Docker commands
```bash
docker ps -a
docker logs kafka-learn-sqlserver-1
docker logs kafka-learn-sqlserver-1 --tail 50
docker inspect kafka-learn-sqlserver-1 --format="{{.State.Status}} {{.State.ExitCode}} {{.State.Error}}"
docker-compose up -d sqlserver
docker ps --filter name=sqlserver
docker inspect kafka-learn-sqlserver-1 --format="{{.State.Status}}"
``` 

## DB Scripts
```bash
# 1. Create Database
docker exec -it kafka-learn-sqlserver-1 /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "Okay@1234" -C -Q "CREATE DATABASE VendingDB;"

# 2. Create Table
docker exec -it kafka-learn-sqlserver-1 /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "Okay@1234" -C -d VendingDB -Q "CREATE TABLE vending_telemetry (id BIGINT IDENTITY(1,1) PRIMARY KEY, machine_id NVARCHAR(255), timestamp DATETIME2, temperature FLOAT, inventory_json NVARCHAR(MAX), status NVARCHAR(255));"

# 3. Verify Table
docker exec -it kafka-learn-sqlserver-1 /opt/mssql-tools18/bin/sqlcmd -S localhost -U sa -P "Okay@1234" -C -d VendingDB -Q "SELECT name FROM sys.tables;"
``` 

## Kafka commands
```bash
# 1. Create Topic
docker exec -it kafka-learn-kafka-1 kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic vending-telemetry

# 2. List Topics
docker exec -it kafka-learn-kafka-1 kafka-topics --list --bootstrap-server localhost:9092

# 3. Describe Topic
docker exec -it kafka-learn-kafka-1 kafka-topics --describe --bootstrap-server localhost:9092 --topic vending-telemetry
``` 

## Kafka UI
```bash
http://localhost:8080
```

