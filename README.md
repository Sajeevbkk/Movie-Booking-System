# CineMagic - Movie Ticket Booking System

A full-stack cinema management and ticket booking solution featuring:
- **Backend**: Spring Boot 3 REST API server with MySQL persistence, storing high-resolution movie posters as binary data (`LONGBLOB`), Caffeine in-memory caching for low latency, secure stateless JWT authentication, and centralized credentials via `app.properties`.
- **Admin Web Dashboard**: Modern, responsive HTML5/Bootstrap 5 administration portal served directly by Spring Boot, enabling cinema administrators to manage movies, upload movie posters directly into MySQL, schedule showtimes, manage theaters, and monitor live bookings and revenue.
- **Desktop Application**: Modern Java Swing application styled with the **FlatLaf Light** look & feel, custom cinema UI components, interactive auditorium seat selection map with real-time seat availability, dual-tier client caching (L1 memory + L2 disk), checkout flow, and customer booking history.

---

## Architecture Overview

```
+-----------------------------------------------------------------------------------------+
|                                  CineMagic Architecture                                 |
+-----------------------------------------------------------------------------------------+
|                                                                                         |
|   +----------------------------+                     +------------------------------+   |
|   |    Admin Web Dashboard     |                     |    Java Swing Desktop App    |   |
|   |  - HTML5, CSS3, JS         |                     |  - FlatLaf Light Theme       |   |
|   |  - Bootstrap 5             |                     |  - Custom Cards & Seat Map   |   |
|   |  - Poster Upload Preview   |                     |  - Dual-Tier Image Cache     |   |
|   +--------------+-------------+                     +--------------+---------------+   |
|                  |                                                  |                   |
|                  | REST (HTTP / JSON / Multipart)                   | REST (HTTP / JSON)|
|                  +------------------------+  +----------------------+                   |
|                                           |  |                                          |
|                                           v  v                                          |
|                          +------------------------------------+                         |
|                          |      Spring Boot Backend (8080)    |                         |
|                          |  - RESTful API Controllers         |                         |
|                          |  - Spring Security & JWT Auth      |                         |
|                          |  - Caffeine In-Memory Caching      |                         |
|                          |  - Centralized app.properties      |                         |
|                          +-----------------+------------------+                         |
|                                            |                                            |
|                                            | JPA / Hibernate                            |
|                                            v                                            |
|                          +------------------------------------+                         |
|                          |            MySQL 8 Database        |                         |
|                          |  - Schema: movie_booking_db        |                         |
|                          |  - Posters: LONGBLOB binary data   |                         |
|                          |  - Theaters, Seats & Showtimes     |                         |
|                          |  - Bookings & Users                |                         |
|                          +------------------------------------+                         |
|                                                                                         |
+-----------------------------------------------------------------------------------------+
```

---

## Prerequisites

Ensure the following tools are installed on your machine:
- **Java Development Kit (JDK)**: JDK 21 or later (`java -version`, `javac -version`)
- **Apache Maven**: Version 3.8+ (`mvn -v`)
- **MySQL Server**: Version 8.0+ running on port `3306`

---

## 1. Database & Backend Configuration (`backend/app.properties`)

The backend is configured via `app.properties` (located in both `backend/app.properties` and `backend/src/main/resources/app.properties`):

```properties
# ===================================================================
# Database & Admin Credentials Configuration File (app.properties)
# ===================================================================

# Database Settings
db.url=jdbc:mysql://localhost:3306/movie_booking_db?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=root
db.password=password
db.driver=com.mysql.cj.jdbc.Driver

# Initial System Administrator Credentials
# (Auto-seeded into the database upon initial application launch)
admin.username=admin
admin.password=admin123
admin.email=admin@cinemagic.com
admin.fullname=Cinema Administrator

# JWT Security Configuration
jwt.secret=movieBookingSecureSuperLongKey2026WithSufficientEntropyForHS256Algorithm
jwt.expirationMs=86400000

# Cache Tuning (Caffeine)
cache.caffeine.spec=maximumSize=1000,expireAfterWrite=600s
```

### Auto Database Creation & Seeding
- The database schema `movie_booking_db` is created automatically on startup (`createDatabaseIfNotExist=true`).
- On initial startup, the backend automatically seeds:
  - The administrator account defined in `app.properties` (`admin` / `admin123`)
  - A test customer account (`john_doe` / `user123`)
  - Two auditoriums: *Screen 1 - Dolby Atmos 4K* and *Screen 2 - IMAX Laser 3D* with complete regular and VIP seat grids
  - Blockbuster movies (*Inception*, *Interstellar*, *The Dark Knight*, *Avatar: The Way of Water*) with generated binary posters saved in MySQL `LONGBLOB`
  - Upcoming showtimes for today and tomorrow

---

## 2. Desktop Application Configuration (`app/app.properties`)

The desktop client connects to the backend REST API using settings defined in `app.properties` (located in `app/app.properties` and `app/src/main/resources/app.properties`):

```properties
# ===================================================================
# CineMagic Desktop Application Configuration (app.properties)
# ===================================================================

# Backend Server REST API Base URL
# When running locally: http://localhost:8080/api
# When deploying to production, point this to your production backend URL, e.g.:
# api.base.url=https://your-production-server.com/api
api.base.url=http://localhost:8080/api

# Connection & Request Timeout (in seconds)
api.timeout.seconds=10
```

### Changing the Backend Link in Production
You can redirect the desktop app to a production server without modifying or recompiling the code:
1. **External Override (Zero Recompilation)**: Place an `app.properties` file in the same directory as the executable JAR `movie-booking-app-1.0.0.jar`. The application checks the current working directory first and dynamically overrides the API target:
   ```properties
   api.base.url=https://api.yourcinemadomain.com/api
   api.timeout.seconds=15
   ```
2. **Built-in Resource (Pre-package)**: Edit `app/src/main/resources/app.properties` before packaging, then run `mvn clean package -DskipTests` to embed the production URL into the JAR.
3. **Configuration Loading Precedence**:
   $$\text{External } \texttt{./app.properties} \longrightarrow \text{Bundled } \texttt{classpath:/app.properties} \longrightarrow \text{Default Fallback } (\texttt{http://localhost:8080/api})$$

---

## 3. Compiling & Running the Backend Server

Navigate to the `backend/` directory:

```bash
cd backend
```

### Option A: Run directly with Maven
```bash
mvn clean spring-boot:run
```

### Option B: Package into a JAR and Run
```bash
mvn clean package -DskipTests
java -jar target/movie-booking-backend-1.0.0.jar
```

The server will start on **`http://localhost:8080`**.

---

## 4. Accessing the Admin Web Dashboard

Once the backend is running, open any web browser:

1. **Admin Login Page**:
   - URL: **`http://localhost:8080/admin/login.html`**
   - Username: `admin` (or configured value in `app.properties`)
   - Password: `admin123` (or configured value in `app.properties`)
2. **Dashboard Features**:
   - **Executive Dashboard** (`/admin/index.html`): Real-time metrics (Active Movies, Showtimes, Bookings, Total Revenue) and recent transactions.
   - **Movie Catalog & Poster Upload** (`/admin/movies.html`): View movies, add new movies, upload poster image files directly into MySQL binary `LONGBLOB` columns with live preview, and toggle movie active status.
   - **Showtime Scheduling** (`/admin/showtimes.html`): Schedule screening dates/times, assign screens, and configure regular & VIP ticket prices.
   - **Bookings Registry** (`/admin/bookings.html`): Search and monitor all transactions, seats booked, and execute cancellations/refunds.
   - **User Accounts & Removal** (`/admin/users.html`): View registered customer accounts and their booking frequency. Cinema administrators have full control to permanently delete user accounts (with cascade deletion of associated bookings).

---

## 5. Compiling & Running the Desktop Application (Java Swing)

Navigate to the `app/` directory:

```bash
cd app
```

### Option A: Run directly with Maven
```bash
mvn compile exec:java
```

### Option B: Package and Run the Shaded Executable JAR
```bash
mvn clean package -DskipTests
java -jar target/movie-booking-app-1.0.0.jar
```

> **Production Tip**: To deploy the desktop app to client computers, copy `target/movie-booking-app-1.0.0.jar` along with an `app.properties` file containing your production backend URL (`api.base.url=https://your-domain.com/api`).

---

## 6. Desktop Application Features

- **Maximized Window on Launch**: Automatically opens in full maximized mode (`JFrame.MAXIMIZED_BOTH`) for an immersive desktop booking experience.
- **Modern FlatLaf Light Look & Feel**: Clean typography, subtle rounded corners, crisp cards, and responsive high-DPI scaling.
- **Dual-Tier Image Caching (`PosterCache`)**:
  - **L1 RAM Cache**: LRU cache storing decoded `ImageIcon` objects in memory for 0-latency rendering.
  - **L2 Local Disk Cache**: Persists downloaded posters into `~/.cinemagic/cache/posters/` across app restarts to avoid redundant network transfers.
- **Interactive Cinema Seat Map**:
  - Curved screen visual representation (*"All Eyes This Way"*)
  - Clear sectioning: Regular vs. VIP seats
  - Real-time seat status: Available, Selected (emerald green), Booked (disabled / gray)
  - Live seat price counter updating instantly as you select or deselect seats.
- **Order Checkout & Confirmation**:
  - Price summary breakdown
  - Payment method selection (Cards, UPI, Counter)
  - Instant confirmation dialog with Reference Number.
- **User Authentication & My Bookings**:
  - Sign in as `john_doe` / `user123` or register a new customer account.
  - Access **My Bookings** to view past and upcoming tickets.
  - Cancel tickets directly from the app; released seats immediately become available for other customers.

---

## 7. Caching Architecture

| Layer | Technology | Target Data | Invalidation / Eviction Policy |
| :--- | :--- | :--- | :--- |
| **Backend In-Memory** | Caffeine Cache (`@Cacheable`) | Active movies, movie details, binary posters, showtimes | Evicted on movie create/update/delete, showtime creation, or booking |
| **HTTP Transport** | HTTP Headers | `GET /api/movies/{id}/poster` | `Cache-Control: public, max-age=86400`, `ETag` validation (HTTP 304 Not Modified) |
| **Desktop Client L1** | `PosterCache` (LRU Memory) | Decoded poster `ImageIcon`s | Max 50 items, evicted on LRU basis |
| **Desktop Client L2** | `PosterCache` (Disk Cache) | Binary poster byte streams | Cached in `~/.cinemagic/cache/posters/` |
| **Desktop Client Data** | `DataCache` | Movie catalog list | 3-minute TTL or manual "Refresh" button click |

---

## 8. REST API Documentation

### Public Endpoints
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Authenticate user or admin, returns JWT token |
| `POST` | `/api/auth/register` | Register new customer account |
| `GET` | `/api/movies` | Get cached list of active movies |
| `GET` | `/api/movies/{id}` | Get movie details by ID |
| `GET` | `/api/movies/{id}/poster` | Stream movie poster binary BLOB with ETag |
| `GET` | `/api/theaters` | Get list of theaters / auditoriums |
| `GET` | `/api/showtimes/movie/{id}` | Get upcoming showtimes for a movie |
| `GET` | `/api/showtimes/{id}/seats` | Get visual seat map with booked status |

### Customer Authenticated Endpoints (`Authorization: Bearer <token>`)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/auth/me` | Get currently logged-in user profile |
| `POST` | `/api/bookings` | Book tickets for showtime and seat IDs |
| `GET` | `/api/bookings/my` | View authenticated user's ticket bookings |
| `POST` | `/api/bookings/{id}/cancel` | Cancel booking and release seats |

### Admin Endpoints (`ROLE_ADMIN` required)
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `GET` | `/api/admin/stats` | Dashboard metrics (movies, bookings, revenue) |
| `GET` | `/api/admin/movies` | Get all movies (active & inactive) |
| `POST` | `/api/admin/movies` | Create movie with multipart poster upload |
| `PUT` | `/api/admin/movies/{id}` | Update movie and replace poster |
| `DELETE` | `/api/admin/movies/{id}` | Delete movie |
| `GET` | `/api/admin/showtimes` | List all scheduled screenings |
| `POST` | `/api/admin/showtimes` | Schedule new screening |
| `DELETE` | `/api/admin/showtimes/{id}` | Remove showtime |
| `GET` | `/api/admin/bookings` | View all customer bookings |
| `GET` | `/api/admin/users` | View all registered accounts |
| `DELETE` | `/api/admin/users/{id}` | Permanently remove user and cascade delete bookings |
