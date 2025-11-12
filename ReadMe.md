# Spring AI Application with Gemma 3 and PostgreSQL

Это приложение демонстрирует интеграцию Spring Boot 3, Spring AI для работы с локальной LLM моделью **Gemma 3 4B** (через Ollama) и векторной базы данных **PostgreSQL** с расширением `pgvector` для хранения эмбеддингов.

## 🚀 Технологии

* **Java 21**
* **Spring Boot 3.2+**
* **Spring AI** (Ollama, PostgreSQL Vector)
* **Ollama** (для запуска локальной LLM `gemma3:4b-it-q4_K_M`)
* **PostgreSQL** + расширение `pgvector`
* **Docker & Docker Compose**

## 📋 Предварительные требования

1. **Docker** и **Docker Compose** установленные в системе.
2. **Java 21** (рекомендуется использовать [SDKMAN!](https://sdkman.io/) для управления версиями).
3. Не менее **8 ГБ оперативной памяти** для комфортной работы моделей и БД.

## 🛠️ Быстрый старт

### 1. Запуск инфраструктуры (Ollama & PostgreSQL)

Все необходимые сервисы запускаются одной командой:

```bash
  docker-compose up -d
```

Эта команда запустит:
* **Ollama** на порту `11431`
* **PostgreSQL** с `pgvector` на порту `5432`

> **Примечание:** При первом запуске Ollama автоматически скачает модель `gemma3:4b-it-q4_K_M`. Это может занять несколько минут в зависимости от скорости вашего интернета. Прогресс загрузки можно посмотреть в логах: `docker-compose logs -f ollama`.

### 2. Проверка работы Ollama

Убедитесь, что модель загружена и отвечает:

```bash
    # Проверить список загруженных моделей
    curl http://localhost:11434/api/tags
    
    # Протестировать модель напрямую
    curl http://localhost:11434/api/generate -d '{
      "model": "gemma2:4b-it-q4_K_M",
      "prompt": "Почему небо голубое?",
      "stream": false
    }'
```

### 3. Настройка приложения

Основные настройки (уже настроены для docker-compose):

```properties
spring:
  # Spring AI Ollama
  ai:
    ollama:
      base-url: http://localhost:11431
      chat:
        model: gemma3:4b-it-q4_K_M
  # PostgreSQL
  datasource:
    url: jdbc:postgresql://localhost:5432/ragdb
    username: postgres
    password: postgres
```

### 4. Запуск приложения

Соберите и запустите Spring Boot приложение:

```bash
    ./mvnw spring-boot:run
    
    # Или соберите JAR и запустите
    ./mvnw clean package
    java -jar target/your-app-name.jar
```

Приложение будет доступно по адресу: `http://localhost:8080`

## 📁 Структура проекта

```
src/main/java/com/yourapp/
└──SpringAiAppApplication.java   # Главный класс приложения
```

### Нехватка памяти

Если модель работает медленно или падает:
* Убедитесь, что у вас достаточно RAM
* Попробуйте использовать меньшую модель в `docker-compose.yml`:
  ```yaml
  ollama pull gemma3:2b-it-q4_K_M
  ```

### Соответствующий `docker-compose.yml`

Для полноты картины, вот пример `docker-compose.yml`, который предполагается в README:

```yaml
services:
  
  postgres:
    image: ankane/pgvector:v0.5.0
    restart: always
    environment:
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
      - POSTGRES_DB=ragdb
      - PGPASSWORD=postgres
    logging:
      options:
        max-size: 10m
        max-file: "3"
    ports:
      - "5432:5432"
    volumes:
      - ./postgres/data:/var/lib/postgresql/data
      - ./postgres/scripts:/docker-entrypoint-initdb.d
    healthcheck:
      test: "pg_isready -U postgres -d ragdb"
      interval: 2s
      timeout: 20s
      retries: 10
      
  pgadmin:
    container_name: pgadmin_container
    image: dpage/pgadmin4
    environment:
      PGADMIN_DEFAULT_EMAIL: ${PGADMIN_DEFAULT_EMAIL:-pgadmin4@pgadmin.org}
      PGADMIN_DEFAULT_PASSWORD: ${PGADMIN_DEFAULT_PASSWORD:-admin}
    volumes:
      - ./servers.json:/pgadmin4/servers.json
    ports:
      - "${PGADMIN_PORT:-5050}:80"

  ollama:
    image: ollama/ollama
    container_name: ollama
    ports:
      - "11431:11434"
    volumes:
      - ./ollama:/root/.ollama
    entrypoint: >
      /bin/sh -c "
        ollama serve &
        sleep 2 &&
        ollama pull gemma3:4b-it-q4_K_M &&
        ollama pull mxbai-embed-large &&
        wait
      "
    restart: unless-stopped
```
