# Earthquake Assignment – Documentation

## Project Setup Instructions

### Prerequisites
- Java JDK 25
- Node.js (v25.8.1 or later) and npm
- PostgreSQL (v17 or higher)
- Maven (included via mvnw wrapper)

---

## How to Run Backend and Frontend

### Backend
```bash
cd earthquake
./mvnw spring-boot:run
```
Backend runs at: `http://localhost:8080`

Or from IntelliJ:
- Open the project
- Run the main class (`EarthquakeApplication`)
- Backend will start on `http://localhost:8080`

### Frontend
```bash
cd earthquake_frontend
npm install
npm run dev
```
Frontend runs at: `http://localhost:5173`

---

## Database Configuration Steps

The application uses PostgreSQL to store earthquake data.

- Create a new database named `earthquake_db`
- Open pgAdmin and connect to your local PostgreSQL server
- Alternatively, you can connect to the database directly through IntelliJ IDEA via View → Tool Windows → Database, without needing pgAdmin.
- Configure credentials in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/earthquake_db
spring.datasource.username=postgres
spring.datasource.password=your_password
```

Once the backend is started, Hibernate will automatically create the `earthquakes` table based on the entity definition.

---

## Assumptions Made

- The application uses a REST API architecture
- Backend is built with Spring Boot
- Frontend is built with React
- Earthquake data is fetched from the USGS public GeoJSON API
- Existing records are deleted before each new fetch to avoid duplicates
- The time filter parameter expects UTC format: `yyyy-MM-ddTHH:mm:ss`

---

## Optional Improvements Implemented

**Map Visualization** — earthquake locations are displayed on an interactive map using React Leaflet and OpenStreetMap, with popups showing place, magnitude and depth.

**Delete** — individual earthquake records can be deleted directly from the table in the UI.

**Combined Filtering** — magnitude and time filters can be applied separately or together.
