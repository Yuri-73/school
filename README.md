## NotificationsBot
NotificationsBot — это бот для Telegram, разработанный на Java с использованием Spring Boot. Бот предоставляет пользователям возможность настраивать и получать напоминания и уведомления в Telegram, а также поддерживает интеграцию с различными API для расширенного функционала уведомлений.

## Функциональность
- Настройка напоминаний в Telegram на основе заданных параметров (время, тип события и т.д.).
- Поддержка персонализированных уведомлений.
- Интеграция с внешними API для получения данных и отправки сообщений.
- Удобное управление через Telegram-команды.

## Инструкция по установке
Клонируйте репозиторий:

```bash
git clone https://github.com/2desoo/NotificationsBot.git
cd NotificationsBot
```
Установите зависимости:

```bash
mvn install
```
## Настройте application.properties в src/main/resources/:

```properties
telegram.api.token=YOUR_TELEGRAM_BOT_TOKEN
notification.api.key=YOUR_API_KEY (если требуется)
```
## Запустите приложение:

```bash
mvn spring-boot:run
```
Для локальной разработки используйте ngrok для проброса localhost в интернет:

```bash
ngrok http 8080
```
Скопируйте URL, сгенерированный ngrok, и настройте Webhook в Telegram Bot API.

## Использование
Основные команды
- /start — запуск бота и регистрация пользователя.
- /help — вывод справки по доступным командам.
## Пример
После запуска введите команду /start в Telegram, чтобы активировать бота.

## Технологии
- Java 17
- Spring Boot
- Telegram API
- Ngrok — для проброса localhost (при локальной разработке)
