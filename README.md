# Expense Limit Service

Банковский микросервис для учёта расходных операций и месячных лимитов в USD по категориям (product/service).

## Технологии

- Java 21
- Spring Boot 4.1.1
- Spring Data JPA
- PostgreSQL 16
- Flyway
- SpringDoc OpenAPI 3.1.0
- Lombok

## Запуск

### Требования

- JDK 21
- Docker Desktop

### Шаги

1. Запустить PostgreSQL:
```bash
docker compose up -d
```

2. Запустить приложение:
```bash
./mvnw.cmd spring-boot:run
```

Приложение будет доступно на:
- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

## API

### Transactions

| Метод | Путь | Описание |
|---|---|---|
| POST | `/api/integration/transactions` | Создание расходной операции |
| GET | `/api/accounts/{accountFrom}/transactions/exceeded` | Операции с превышением лимита |

### Limits

| Метод | Путь | Описание |
|---|---|---|
| POST | `/api/accounts/{accountFrom}/limits` | Создание месячного лимита |
| GET | `/api/accounts/{accountFrom}/limits` | История лимитов счёта |

## Особенности

- Расчётный месяц и время в UTC
- Если лимит не задан, действует 1000 USD
- Новый лимит не обновляет старые операции
- `limit_exceeded = true` только если сумма > лимита
- Параллельные запросы сериализуются через блокировку
- Поддерживаемые валюты: USD, KZT, RUB
- Операции без курса валюты получают статус PENDING_RATE
