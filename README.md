# Hybrid Calculator App

Гибридное мобильное приложение-калькулятор с нативным Android UI и React Native компонентами, интегрированное с Laravel backend API.

## 📱 Описание проекта

Это гибридное приложение, состоящее из:
- **Backend**: Laravel API с PostgreSQL базой данных
- **Frontend**: Android приложение с нативным UI + React Native компоненты
- **Интеграция**: Полная интеграция между frontend и backend через REST API

### ✨ Основные возможности

- **Нативный калькулятор** - полнофункциональный калькулятор на Java
- **React Native компонент** - история вычислений с современным UI (работает с Hermes)
- **API интеграция** - вычисления выполняются через Laravel backend
- **Fallback система** - автоматическое переключение на локальные вычисления при недоступности API
- **История вычислений** - сохранение и просмотр истории операций через React Native компонент
- **Гибридная архитектура** - переключение между нативным и React Native режимами
- **Стабильная работа** - приложение работает стабильно с включенным Hermes

## 📊 Текущий статус проекта

### ✅ Работает
- **Нативный калькулятор** - полностью функционален
- **API сервер** - Laravel с PostgreSQL работает корректно
- **Кнопка "RN"** - переключает компонент без крашей
- **React Native компонент** - полноценно работает с Hermes
- **Интеграция frontend-backend** - вычисления сохраняются в базу данных
- **История вычислений** - отображается в React Native компоненте
- **Нативный калькулятор в RN** - встроен в React Native компонент

### 🔧 Технические детали
- **Версия React Native:** 0.72.6
- **JavaScript Engine:** Hermes (включен и работает)
- **База данных:** PostgreSQL
- **Backend:** Laravel API
- **Frontend:** Android нативный + React Native с Hermes

## 🚀 Быстрый старт

### 1. Клонирование и настройка

```bash
git clone <repository-url>
cd rnproject
```

### 2. Настройка Backend (Laravel)

```bash
cd backend

# Установка зависимостей
composer install

# Настройка базы данных PostgreSQL
# Создайте базу данных 'calculator_db' в PostgreSQL

# Настройка .env файла
cp .env.example .env
# Отредактируйте .env файл:
# DB_CONNECTION=pgsql
# DB_HOST=127.0.0.1
# DB_PORT=5432
# DB_DATABASE=calculator_db
# DB_USERNAME=postgres
# DB_PASSWORD=

# Генерация ключа приложения
php artisan key:generate

# Запуск миграций
php artisan migrate

# Запуск сервера
php artisan serve
```

Backend будет доступен по адресу: `http://localhost:8000`

### 3. Настройка Frontend (Android)

```bash
cd frontend

# Установка зависимостей
npm install

# Сборка Android приложения
cd android
./gradlew assembleDebug

# Установка на эмулятор/устройство
./gradlew installDebug
```

## 📖 Подробная инструкция

### Backend API

Laravel API предоставляет следующие endpoints:

#### POST `/api/calculate`
Выполнение математических вычислений

**Поддерживаемые операции:**
- `+` - Сложение
- `-` - Вычитание  
- `*` - Умножение
- `/` - Деление

**Запрос:**
```json
{
  "a": 10,
  "b": 5,
  "operation": "*"
}
```

**Ответ:**
```json
{
  "success": true,
  "result": 50,
  "calculation": {
    "a": 10,
    "b": 5,
    "operation": "*",
    "result": 50
  }
}
```

#### GET `/api/history`
Получение истории вычислений

**Ответ:**
```json
{
  "success": true,
  "history": [
    {
      "id": 1,
      "calculation": "5 + 3 = 8",
      "created_at": "2024-01-01T12:00:00Z"
    }
  ]
}
```

#### DELETE `/api/history`
Очистка истории вычислений

#### GET `/api/operations`
Получение списка поддерживаемых операций

**Ответ:**
```json
{
  "success": true,
  "operations": ["+", "-", "*", "/"],
  "description": {
    "+": "Addition",
    "-": "Subtraction", 
    "*": "Multiplication",
    "/": "Division"
  }
}
```

#### GET `/api/health`
Проверка состояния API

**Ответ:**
```json
{
  "success": true,
  "status": "healthy",
  "timestamp": "2024-01-01T12:00:00Z",
  "version": "1.0.0"
}
```

### Frontend (Android + React Native)

Android приложение построено на гибридной архитектуре с нативным Java UI и React Native компонентами.

#### Нативные компоненты (Java):

- **MainActivity.java** - главная активность с UI калькулятора
- **CalculatorService** - сервис для работы с API
- **AsyncTask** - асинхронные HTTP запросы к backend

#### React Native компоненты:

- **App.tsx** (`frontend/src/App.tsx`) - основной React Native компонент для истории вычислений
  - Отображает историю вычислений с современным UI
  - Загружает данные через CalculatorService
  - Поддерживает очистку истории
  - Активируется через кнопку "RN" в нативном калькуляторе
  - Содержит встроенный нативный калькулятор
- **NativeCalculator.tsx** (`frontend/src/components/NativeCalculator.tsx`) - React Native компонент для нативного калькулятора
  - Интегрирует нативный Android калькулятор в React Native
  - Обрабатывает события вычислений
  - Отправляет результаты в историю
- **CalculatorService.ts** (`frontend/src/services/CalculatorService.ts`) - TypeScript сервис для API интеграции
- **React Native Bridge** - интеграция между нативным и RN кодом через MainActivity.java

#### Функциональность:

1. **Нативный калькулятор** - кнопки 0-9, операции (+, -, ×, ÷), очистка (C), backspace (⌫)
2. **Кнопка "RN"** - активация React Native компонента
3. **React Native история** - просмотр истории вычислений с современным UI
4. **Нативный калькулятор в RN** - встроенный нативный калькулятор в React Native компоненте
5. **API интеграция** - отправка запросов к Laravel backend
6. **Fallback система** - локальные вычисления при недоступности API

### Интеграция Frontend-Backend

1. **При нажатии "="** приложение отправляет POST запрос на `/api/calculate`
2. **Laravel обрабатывает** вычисление и сохраняет в базу данных
3. **Android получает** результат и отображает на экране
4. **При ошибке API** автоматически переключается на локальные вычисления

## 🛠️ Разработка

### Полезные команды

#### Backend
```bash
# Запуск сервера разработки
php artisan serve

# Запуск миграций
php artisan migrate

# Очистка кэша
php artisan cache:clear
php artisan config:clear
```

#### Frontend
```bash
# Установка React Native зависимостей
cd frontend
npm install

# Запуск Metro сервера (для React Native)
npx react-native start

# Сборка debug APK
cd android
./gradlew assembleDebug

# Установка на устройство
./gradlew installDebug

# Очистка проекта
./gradlew clean
```

## 🐛 Отладка

### Известные проблемы и решения

#### React Native и Hermes

**Проблема:** При нажатии кнопки "RN" приложение крашилось с ошибкой:
```
java.lang.UnsatisfiedLinkError: dlopen failed: library "libhermes.so" not found
```

**Причина:** React Native 0.72.6 имел проблемы с загрузкой Hermes библиотеки.

**Решение:** 
1. Включен Hermes в конфигурации (`enableHermes = true` в `build.gradle`)
2. Добавлена безопасная инициализация React Native с обработкой ошибок
3. Создан метод `getReactInstanceManagerSafely()` для предотвращения крашей

**Статус:** ✅ Решено - приложение стабильно работает с включенным Hermes.

#### Мониторинг логов

**Просмотр логов приложения:**
```bash
# Очистка логов
adb logcat -c

# Запуск приложения
adb shell am start -n com.calculator/.MainActivity

# Мониторинг логов MainActivity
adb logcat -s MainActivity:D

# Мониторинг ошибок
adb logcat -s AndroidRuntime:E
```

**Проверка работы кнопки "RN":**
```bash
# Нажатие кнопки "RN" (координаты могут отличаться)
adb shell input tap 540 1200
```

### Проблемы с Backend

1. **Ошибка подключения к БД**
   - Проверьте настройки в `.env`
   - Убедитесь, что PostgreSQL запущен
   - Проверьте права пользователя postgres

2. **Ошибка "could not find driver"**
   - Включите расширение `pdo_pgsql` в `php.ini`
   - Перезапустите веб-сервер

### Проблемы с Frontend

1. **APK не собирается**
   - Проверьте версию Android SDK
   - Очистите проект: `./gradlew clean`

2. **API недоступен**
   - Проверьте, что backend запущен на `localhost:8000`
   - Убедитесь, что эмулятор может обращаться к `10.0.2.2:8000`

3. **React Native компонент не загружается**
   - Убедитесь, что Metro сервер запущен: `npx react-native start`
   - Проверьте, что Hermes отключен в `app/build.gradle`
   - Проверьте логи Android: `adb logcat -s MainActivity:D`

4. **Ошибка "libhermes.so not found"**
   - Убедитесь, что `enableHermes = false` в `app/build.gradle`
   - Проверьте, что `isHermesEnabled()` возвращает `false` в `MainApplication.java`

## 📱 Тестирование

### Тестирование Backend API

```bash
# Тест сложения
curl -X POST http://localhost:8000/api/calculate \
  -H "Content-Type: application/json" \
  -d '{"a": 10, "b": 5, "operation": "+"}'

# Тест вычитания
curl -X POST http://localhost:8000/api/calculate \
  -H "Content-Type: application/json" \
  -d '{"a": 10, "b": 5, "operation": "-"}'

# Тест умножения
curl -X POST http://localhost:8000/api/calculate \
  -H "Content-Type: application/json" \
  -d '{"a": 10, "b": 5, "operation": "*"}'

# Тест деления
curl -X POST http://localhost:8000/api/calculate \
  -H "Content-Type: application/json" \
  -d '{"a": 10, "b": 5, "operation": "/"}'

# Тест поддерживаемых операций
curl http://localhost:8000/api/operations

# Тест health check
curl http://localhost:8000/api/health

# Тест истории
curl http://localhost:8000/api/history
```


## 🔧 Конфигурация

### Backend (.env)
```env
DB_CONNECTION=pgsql
DB_HOST=127.0.0.1
DB_PORT=5432
DB_DATABASE=calculator_db
DB_USERNAME=postgres
DB_PASSWORD=
```

### Frontend (MainActivity.java)
```java
private static final String API_BASE_URL = "http://10.0.2.2:8000/api";
```

### React Native (app/build.gradle)
```gradle
enableHermes = true  // Используем Hermes JavaScript engine
```


### Поток данных

1. **Нативный калькулятор** → API → PostgreSQL
2. **RN нативный калькулятор** → API → PostgreSQL  
3. **RN история** ← API ← PostgreSQL
4. **События** → RN компонент → обновление UI
