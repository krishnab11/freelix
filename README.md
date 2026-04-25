# Freelix — Freelancer Marketplace System

A production-ready, full-stack freelancer marketplace built with **Spring Boot**, **Thymeleaf**, **Bootstrap 5**, and **SQLite**.

---

## 🚀 Quick Start

### Prerequisites
- **Java 17+** (JDK, not JRE)
- **Maven 3.8+**
- Internet connection (for Google Fonts, Bootstrap Icons CDN on first load)

### Run the Application

```bash
cd "c:\Users\Admin\Desktop\freelix"
mvn spring-boot:run
```

Then open: **http://localhost:8080**

### Build a JAR

```bash
mvn clean package -DskipTests
java -jar target/freelix-1.0.0.jar
```

---

## 🔑 Default Credentials

| Role       | Email                   | Password  |
|------------|-------------------------|-----------|
| Admin      | admin@freelix.com       | admin123  |
| Client     | client@demo.com         | demo123   |
| Freelancer | freelancer@demo.com     | demo123   |

> These are auto-created on first startup via `DataInitializer.java`

---

## ⚙️ Configuration (application.properties)

### Cloudinary (required for file uploads)
1. Create a free account at https://cloudinary.com
2. Open `src/main/resources/application.properties`
3. Replace:
```properties
cloudinary.cloud-name=YOUR_CLOUD_NAME
cloudinary.api-key=YOUR_API_KEY
cloudinary.api-secret=YOUR_API_SECRET
```

### Email (optional — app works without it)
1. Get a Gmail App Password (Google Account → Security → App Passwords)
2. Replace in `application.properties`:
```properties
spring.mail.username=YOUR_EMAIL@gmail.com
spring.mail.password=YOUR_APP_PASSWORD
```

---

## 📂 Project Structure

```
freelix/
├── pom.xml
└── src/main/
    ├── java/com/freelix/
    │   ├── FreelixApplication.java
    │   ├── config/
    │   │   ├── SecurityConfig.java         # Spring Security + BCrypt
    │   │   ├── CloudinaryConfig.java       # Cloudinary bean
    │   │   └── DataInitializer.java        # Seed admin/demo accounts
    │   ├── entity/                          # JPA entities (7 tables)
    │   ├── enums/                           # Role, ProjectStatus, etc.
    │   ├── repository/                      # Spring Data JPA repos
    │   ├── service/                         # Business logic (9 services)
    │   ├── controller/                      # MVC controllers (10)
    │   ├── security/                        # CustomUserDetails
    │   └── dto/                             # RegisterDto, ProjectDto, etc.
    └── resources/
        ├── application.properties
        ├── templates/                       # Thymeleaf HTML pages
        │   ├── auth/                        # login, register
        │   ├── client/                      # dashboard, projects, applicants
        │   ├── freelancer/                  # dashboard, browse, apply, applications
        │   ├── chat/                        # real-time chat
        │   ├── profile/                     # profile + file uploads
        │   ├── admin/                       # dashboard, users, projects
        │   └── payment/                     # invoice
        └── static/
            ├── css/style.css               # Full custom CSS + dark mode
            └── js/
                ├── chat.js                 # AJAX polling (2s interval)
                └── darkmode.js             # Dark mode toggle
```

---

## 🧩 Modules Implemented

| # | Module          | Features                                                              |
|---|-----------------|-----------------------------------------------------------------------|
| 1 | User            | Register, login, logout, BCrypt, roles, profile management           |
| 2 | Project         | Create, edit, delete, view applicants, select freelancer             |
| 3 | Application     | Apply with proposal + bid, status tracking, accept/reject            |
| 4 | Chat            | One-to-one project chat, AJAX polling every 2 seconds                |
| 5 | File Upload     | Cloudinary: profile photo, resume, certificates, project files       |
| 6 | Payment         | Simulated payment, auto invoice number, printable HTML invoice       |
| 7 | Review          | Star ratings, feedback, auto-recalculated average                    |
| 8 | Admin           | View all users, all projects, system management                      |
| 9 | Analytics       | Chart.js bar + doughnut charts, revenue stats                        |
|10 | Email           | Async notifications: welcome, assignment, payment (JavaMailSender)   |

---

## 🗄️ Database

SQLite file (`freelix.db`) is created automatically in the project root on first run.

Hibernate `ddl-auto=update` creates all tables automatically from JPA entities.

Tables:
- `users` — all users with role
- `projects` — posted projects with client/freelancer FKs
- `applications` — freelancer proposals
- `messages` — chat messages per project
- `payments` — payment records with invoice numbers
- `reviews` — star ratings with feedback
- `file_records` — Cloudinary file URLs

---

## 🎨 UI Features

- **Dark mode** toggle (persisted in localStorage)
- **Glassmorphism** auth pages with gradient background
- **Sidebar navigation** (role-aware)
- **Stats cards** with color-coded accent bars
- **Chat bubbles** with sent/received styling
- **Printable invoice** page
- **Bootstrap 5** responsive grid
- **Chart.js** admin analytics

---

## 🔄 Typical User Flow

```
CLIENT  →  Register  →  Post Project  →  View Applicants  →  Accept Freelancer
                                                              ↓
FREELANCER →  Browse Projects  →  Apply with Proposal  ←→  Chat  →  Complete work
                                                              ↓
CLIENT  →  Mark as Paid (Invoice generated)  →  Leave Review  →  Freelancer rating updated
ADMIN   →  View all users/projects  →  See analytics charts
```

---

## ⚠️ Notes

- **File uploads** will fail gracefully with an error message if Cloudinary is not configured.  
- **Emails** will silently fail if SMTP is not configured — the app still works fully.  
- The database file `freelix.db` is created in the directory where you run the app.  
- For production, change `spring.jpa.hibernate.ddl-auto=update` → `validate`.
