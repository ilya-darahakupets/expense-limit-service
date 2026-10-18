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

## Демо-данные для проверки

При старте приложения (по умолчанию `app.demo-data.enabled=true`) создаются тестовые счета,
лимиты, курсы валют и операции за текущий месяц (UTC). Повторно не создаются, если у демо-счетов уже есть операции.
Отключить: переменная окружения `DEMO_DATA_ENABLED=false`.

Курсы (provider `demo`): 1 KZT = 0.002 USD, 1 RUB = 0.0125 USD.

| Счёт | Лимиты | Операции | Превышения |
|---|---|---|---|
| `1000000001` | product: 2000 USD; service: по умолчанию 1000 USD | product: 500 USD, 500 000 KZT (1000 USD), 50 000 RUB (625 USD); service: 1200 USD | 2: RUB-операция (лимит 2000), service 1200 USD (лимит 1000) |
| `1000000002` | нет (по умолчанию 1000 USD) | product: 300 USD; service: 200 000 KZT (400 USD) | нет |
| `1000000003` | service: 100 USD, затем повышен до 500 USD | service: 150 USD (до повышения), 200 USD (после) | 1: операция 150 USD (лимит 100) |

Счёт получателя во всех операциях: `9000000001`.

Примеры:
```bash
curl http://localhost:8080/api/accounts/1000000001/transactions/exceeded
curl http://localhost:8080/api/accounts/1000000003/limits
```
