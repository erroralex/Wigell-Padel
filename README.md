# Wigell Padel – Java Backend (Mikrotjänst)

![Java](https://img.shields.io/badge/Java-24-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/Databas-MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Keycloak](https://img.shields.io/badge/Security-Keycloak_OAuth2-EBEBEB?style=for-the-badge&logo=keycloak&logoColor=blue)
![Postman](https://img.shields.io/badge/Testad_med-Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)

Ett RESTful API byggt med Java 24 och Spring Boot 3.x som en del av Wigellkoncernens nya webbportal. API:et hanterar affärslogiken för **Wigell Padel**, inklusive kunder, padelbanor, tillgänglighet och bokningar.

Projektet är integrerat med teamets gemensamma bibliotek (`wigell-shared-lib`) för Keycloak-säkerhet (JWT), automatisk AOP-filloggning och asynkron valutakonvertering via Project Loom (Virtual Threads).

---

## Funktioner (Endpoints)

### Padelbanor (Courts)
*Kräver rollen `ADMIN`*

| Metod  | Endpoint               | Beskrivning                                  |
|--------|------------------------|----------------------------------------------|
| `GET`  | `/api/v1/courts`       | Hämtar samtliga padelbanor                   |
| `GET`  | `/api/v1/courts/{id}`  | Hämtar en specifik padelbana                 |
| `POST` | `/api/v1/courts`       | Lägger till en ny padelbana i systemet       |
| `PUT`  | `/api/v1/courts/{id}`  | Uppdaterar information för en specifik bana  |
| `DELETE`| `/api/v1/courts/{id}` | Tar bort en padelbana                        |

### Kunder (Customers & Addresses)
*Kräver rollen `ADMIN`*

| Metod  | Endpoint                                     | Beskrivning                                      |
|--------|----------------------------------------------|--------------------------------------------------|
| `GET`  | `/api/v1/customers`                          | Lista alla kunder                                |
| `POST` | `/api/v1/customers`                          | Lägga till en ny kund                            |
| `PUT`  | `/api/v1/customers/{id}`                     | Uppdatera en befintlig kund                      |
| `DELETE`| `/api/v1/customers/{id}`                    | Ta bort en kund                                  |
| `POST` | `/api/v1/customers/{id}/addresses`           | Lägga till en ny adress för specifik kund        |
| `DELETE`| `/api/v1/customers/{id}/addresses/{addrId}` | Ta bort en specifik adress från en kund          |

### Bokningar (Bookings)
*Gemensamma endpoints för `USER` och `ADMIN`*

| Metod   | Endpoint                                   | Beskrivning                                                |
|---------|--------------------------------------------|------------------------------------------------------------|
| `POST`  | `/api/v1/bookings`                         | Boka en tid (Returnerar pris i SEK och EUR)                |
| `PATCH` | `/api/v1/bookings/{id}`                    | Uppdatera befintlig bokning (datum, tid, spelare, bana)    |
| `GET`   | `/api/v1/bookings?customerId={id}`         | Lista alla bokningar för en specifik kund                  |

*Exklusiva endpoints för `ADMIN`*

| Metod   | Endpoint               | Beskrivning                                  |
|---------|------------------------|----------------------------------------------|
| `GET`   | `/api/v1/bookings`     | Lista samtliga bokningar i systemet          |
| `GET`   | `/api/v1/bookings/{id}`| Hämta enskild bokning                        |
| `DELETE`| `/api/v1/bookings/{id}`| Ta bort en specifik bokning                  |

### Tillgänglighet (Availability)
*Gemensam endpoint för `USER` och `ADMIN`*

| Metod  | Endpoint                                               | Beskrivning                                      |
|--------|--------------------------------------------------------|--------------------------------------------------|
| `GET`  | `/api/v1/availability?date={YYYY-MM-DD}&courtId={id}`  | Returnerar en lista på lediga tider för en bana  |

---

##  Datamodell

Systemet är normaliserat och använder JPA/Hibernate för databasabstraktion. *(Lösenordshantering är bortkopplad då autentisering sker externt via Keycloak).*

### Booking
Hjärtat i applikationen. Kopplar samman kund och bana.
* `id` (PK)
* `customer_id` (FK -> Customer)
* `court_id` (FK -> Court)
* `bookingDate` (DATE)
* `startTime` (TIME)
* `numberOfPlayers` (INT, 1-4)
* `totalPriceSek` (DECIMAL)
* `totalPriceEur` (DECIMAL, uträknas asynkront via valutatjänst)

### Customer & Address
En kund (`Customer`) har en Många-till-En relation till `Address`.
* **Customer:** `username` (unikt), `role`, `firstName`, `lastName`.
* **Address:** `streetName`, `city`, `zipCode`, `country`.

### Court
* `name` (unikt), `description`, `isIndoor` (boolean), `pricePerHourSek`.

---

## Säkerhet (Keycloak OAuth2)

API:et är skyddat som en **OAuth2 Resource Server** via Spring Security. Det litar på JWT-tokens utfärdade av teamets Keycloak-server.

| Roll           | Beskrivning                                     |
|----------------|-------------------------------------------------|
| `ROLE_ADMIN`   | Full åtkomst till alla endpoints i systemet.    |
| `ROLE_USER`    | Åtkomst till att boka, se lediga tider och läsa egna bokningar. |

Autentiseringsuppgifter skickas med som en **Bearer Token** i headern:
`Authorization: Bearer <din_jwt_token>`

---

## Kom igång

### Krav
* Java 24
* Maven
* MySQL Server (port 3306)
* Det lokala biblioteket `wigell-shared-lib` måste vara byggt med `mvn clean install`.

### 1. Sätt upp databasen
1. Öppna din MySQL-klient (t.ex. MySQL Workbench).
2. Kör filen `src/main/resources/create.sql`. Detta skapar databasen `wigell_padel_db`, tabellerna och lägger in den testdata som krävs enligt specifikationen (5 kunder, 5 banor, 2 bokningar).

### 2. Starta applikationen
Klona repot och kör igång:
```bash
git clone [https://github.com/ditt-github-namn/wigell-padel.git](https://github.com/ditt-github-namn/wigell-padel.git)
cd wigell-padel
mvn spring-boot:run
```

Applikationen startar på http://localhost:8584.

---

## Testning med Postman

All kommunikation med API:et sker via **Postman**. Säkerställ att du hämtat en token från er Keycloak-instans innan du skickar anrop.

**Exempel – Se lediga tider:**
```http
GET http://localhost:8584/api/v1/availability?date=2026-04-14&courtId=1
Authorization: Bearer <din_jwt_token>
```

**Exempel – Skapa ny bokning (Beräknar EUR automatiskt):**
```http
POST http://localhost:8584/api/v1/bookings
Content-Type: application/json
Authorization: Bearer <din_jwt_token>

{
  "customerId": 2,
  "courtId": 1,
  "bookingDate": "2026-04-20",
  "startTime": "18:00:00",
  "numberOfPlayers": 4
}
```
---

## Felhantering

Felhanteringen dirigeras genom teamets gemensamma `GlobalExceptionHandler` i `shared-lib` och returnerar standardiserade JSON-svar med följande statuskoder:

| Statuskod          | Betydelse                                            |
|--------------------|----------------------------------------------------|
| `200 OK`           | Lyckad hämtning eller uppdatering                  |
| `201 Created`      | Ny resurs skapad (Returnerar alltid `Location`-header) |
| `204 No Content`   | Lyckad radering                                    |
| `403 Forbidden`    | Åtkomst nekad (t.ex. USER försöker nå ADMIN-endpoint) |
| `404 Not Found`    | `ResourceNotFoundException` (Resurs saknas i databasen) |
| `500 Server Error` | "Catch-all" för interna serverfel eller krascher     |

---

##️ Teknisk stack

* **Java 24** – Backend-språk, använder *Virtual Threads* för blixtsnabba externa HTTP-anrop.
* **Spring Boot 3.x** – Ramverk för REST-API och Dependency Injection.
* **Spring Data JPA / Hibernate** – ORM och databasabstraktion.
* **MySQL** – Relaterad databas för persistent lagring.
* **Keycloak (OAuth2)** – Identity and Access Management (IAM).
* **AspectJ (AOP)** – Automatisk övervakning och fil-loggning av datamutationer.
* **Actuators** – Systemövervakning och health checks.

---

## Licens

Distribueras under **MIT-licensen**. Fri för personligt och kommersiellt bruk.

---

<p align="center">
  <b>Utvecklad av</b><br>
  <img src="src/main/resources/alx_logo.png" width="120" alt="Alexander Nilsson Logo"><br>
  Copyright © 2026 Alexander Nilsson
</p>