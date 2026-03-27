# Webbportal med Spring Boot

## Bakgrund

Wigellkoncernen har beslutat att samla några av affärstjänsterna under en portal för enklare administration och underhåll. Två grupper jobbar med projektet där en grupp ansvarar för användargränssnittet och en annan för de mikrotjänster som användargränssnittet kommunicerar med.

Ditt team arbetar med de mikrotjänster som ska utvecklas med Java Spring Boot

Lansering av portalen sker **Måndag 13/4 kl 23:59** så samtliga projekt måste vara inlämnade då!

## Arbetet består av två delar

### 1. En individuell del

Du ansvarar själv över din egen mikrotjänst. Den ska fungera oberoende av de övrigas REST API:er och uppfylla betygskraven enligt nedan.

Du redovisar din mikrotjänst i samband med gruppredovisningen.

Du blir individuellt bedömd på din mikrotjänst

### 2. Ett grupparbete

Inom gruppen ska ni tillsammans planera och genomföra de delarna som krävs för att ert mikrotjänstprojekt ska fungera som en helhet.

Det betyder att er grupp ska utveckla en dashboard där ni kan övervaka ert system.

Det betyder att er grupp ska utveckla en gateway som samtliga requests ska passera igenom.

Det betyder också att om ni vill använda gemensam funktionalitet i någon form så kan den med fördel inkluderas i era api:er via en dependency, tänk shared-lib.

Det betyder att ni redovisar portalen i grupp, och att även det momentet vägs in i det slutgiltiga betyget

---

## Kravspecifikation Wigell Padel

### Funktionella krav

API:et ska ha funktionalitet för både kunder och admin

### Tekniska krav

- Skapa de klasser som behövs för att lösa uppgiften och är vettiga designmässigt från informationen i kraven.
- Strukturera applikationen med lämpliga paket så som controller, service, entity osv.
- Data ska lagras i en MySQL-databas. Databasen ska heta `wigell_padel_db`.
- Verifiera med Postman att alla endpoints går att använda enligt specifikation.
- För att visa totalt pris i SEK och EUR använder du en fast växelkurs (G), eller teamets egen mikrotjänst som hanterar konverteringen (VG)

### Säkerhet

- För G ska Basic security användas för autentisering och auktorisering.
- För VG ska Keycloak användas för autentisering och auktorisering.
- All admin funktionalitet ska kräva att användare är inloggad och har rollen ADMIN.
- All kundfunktionalitet ska kräva inloggning med en användare som har rollen USER.
- Skapa minst en användare med rollen USER.
- Skapa minst en användare med rollen ADMIN.
- Användarens id och lösenord hämtas från databasen. Lösenorden ska även vara krypterade med bcrypt (G)

### Wigell Padel - Systemöversikt

Systemet ska kunna hantera kunder, bokningar, se tillgänglighet och information om banorna. Kunder ska ha unika ID-nummer, användarnamn och kunduppgifter som namn och adress. Bokningar ska inkludera antal spelare, bana, datum, tid och totalpris. Priset ska presenteras i SEK och EUR.

#### Kunder

Kunderna ska kunna göra ett antal aktiviteter med följande endpoints:

- Lista lediga tider `GET /api/v1/availability?date={YYYY-MM-DD}&courtId={courtId}`
- Boka tid `POST /api/v1/bookings`
- Uppdatera bokning `PATCH /api/v1/bookings/{bookingId}` (tillåtna fält: datum, tid, antal spelare, bana)
- Lista bokningar `GET /api/v1/bookings?customerId={customerId}`

#### Admin

Administratörer ska kunna göra ett antal aktiviteter med följande endpoints:

- Lista kunder `GET /api/v1/customers`
- Lägga till kund `POST /api/v1/customers`
- Ta bort kund `DELETE /api/v1/customers/{customerId}`
- Uppdatera kund `PUT /api/v1/customers/{customerId}`
- Hämta padelbanor `GET /api/v1/courts`
- Hämta padelbana `GET /api/v1/courts/{courtId}`
- Lägga till padelbana `POST /api/v1/courts`
- Uppdatera information om banan `PUT /api/v1/courts/{courtId}`
- Ta bort padelbana `DELETE /api/v1/courts/{courtId}`
- Ta bort en bokning `DELETE /api/v1/bookings/{bookingId}`
- Lista samtliga bokningar `GET /api/v1/bookings`
- Hämta enskild bokning `GET /api/v1/bookings/{bookingId}`
- Lägga till adress `POST /api/v1/customers/{customerId}/addresses`
- Ta bort adress `DELETE /api/v1/customers/{customerId}/addresses/{addressId}`

**Viktigt** att alla endpoints följer specifikationen eftersom de ska fungera med användargränssnittet.

### Loggning

Applikationen ska logga info till konsolen (G) eller till fil (VG) när något skapas, ändras eller tas bort. Skriv ett meddelande som talar om vad som händer t.ex. "admin deleted booking xxx"

### Övrigt

- Ingen frontend ska implementeras. All kommunikation med API:et ska ske via Postman
- API:et ska nås på port 8584
- Java 24 och Spring Boot 3.x ska användas
- Lombok är ej tillåtet att använda
- Se till att det finns minst 5 kunder, 5 banor och två bokningar i databasen vid redovisningens start
- Datalagret använder Spring Data JPA; API följer REST-praxis (resurser, statuskoder, Location vid 201)
- Actuators