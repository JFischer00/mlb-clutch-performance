## 1. Clone the repo

```bash
git clone https://github.com/JFischer00/mlb-clutch-performance.git
cd mlb-clutch-performance/applications/data-collector
```

## 2. Create a `.env` file

```text
DB_NAME=[YOUR DB NAME]
DB_USERNAME=[YOUR USERNAME]
DB_PASSWORD=[YOUR PASSWORD]
MLB_API_URL=https://statsapi.mlb.com/api
```

## 3. Start the local database

```bash
docker-compose up -d
```

## 4. Run the application

```bash
./gradlew bootRun
```

## 5. Trigger the service

Get data for a specific date
```bash
curl -X POST http://localhost:8080/api/tasks/fetch-games \
  -H "Content-Type: application/json" \
  -d '{"date": "2026-06-01"}'
```

Default to yesterday
```bash
curl -X POST http://localhost:8080/api/tasks/fetch-games \
  -H "Content-Type: application/json" \
  -d '{}'
```