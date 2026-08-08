# WargaCare — Sistem Pengaduan & Bantuan Warga RT/RW

> Backend REST API untuk sistem pengaduan dan bantuan warga RT/RW, dibangun menggunakan Java 21, Spring Boot, PostgreSQL, dan Docker.

[![Java](https://img.shields.io/badge/Java-21-orange?logo=java)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-green?logo=springboot)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue?logo=postgresql)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Local%20Dev-blue?logo=docker)](https://docs.docker.com/)

---

## 📦 Tech Stack

| Teknologi | Keterangan |
|---|---|
| Java 21 | Bahasa pemrograman utama |
| Spring Boot 3.3 | Framework backend |
| Spring Security + JWT | Autentikasi dan otorisasi |
| Spring Data JPA + Hibernate | ORM untuk akses database |
| PostgreSQL 16 | Database relasional |
| Flyway | Database migration |
| Swagger / OpenAPI 3 | Dokumentasi REST API |
| Docker | Local development environment |
| Docker | Containerization |
| Kubernetes | Orchestration (portfolio) |
| GitHub Actions | CI/CD pipeline |

---

## 🚀 Cara Menjalankan dengan PostgreSQL Lokal

### Prasyarat
- PostgreSQL terinstall dan berjalan
- Database `wargacare` sudah dibuat
- Java 21 JDK (untuk development dari host)
- Maven (opsional, bisa menggunakan `./run.sh`)

### 1. Set koneksi database

```bash
# Sesuaikan kredensial dengan PostgreSQL lokal kamu
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/wargacare
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres
export JWT_SECRET=wargacare-local-secret-key-256bit-long
```

### 2. Jalankan aplikasi

```bash
./run.sh
```

Kalau kamu mau menjalankan langsung tanpa script:

```bash
mvn spring-boot:run
```

> **💡 Tip IntelliJ IDEA / VS Code**: Set environment variable di Run Configuration agar tidak perlu export setiap kali.

### 4. Akses Aplikasi

| URL | Keterangan |
|---|---|
| `http://localhost:8080` | Spring Boot API |
| `http://localhost:8080/swagger-ui.html` | Swagger UI |
| `http://localhost:8080/v3/api-docs` | OpenAPI JSON |

---

## 🗄️ Akses Database

### MySQL CLI via DDEV

```bash
# Buka MySQL CLI (MariaDB) di dalam DDEV
ddev mysql

# Atau jalankan query langsung
ddev mysql -e "SHOW TABLES;"
ddev mysql -e "SELECT * FROM users;"
```

### Koneksi dari Database Client (TablePlus, DBeaver, dsb.)

```bash
# Cek informasi koneksi
ddev describe

# Gunakan:
# Host: localhost
# Port: (lihat output ddev describe, kolom db)
# Database: db
# Username: db
# Password: db
```

---

## 📡 REST API Endpoints

### Authentication

| Method | Endpoint | Auth | Keterangan |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Registrasi pengguna baru |
| POST | `/api/auth/login` | Public | Login dan dapatkan JWT |
| GET | `/api/auth/me` | 🔒 JWT | Data pengguna saat ini |

### Reports (Tahap 2)

| Method | Endpoint | Auth | Keterangan |
|---|---|---|---|
| POST | `/api/reports` | 🔒 WARGA | Buat laporan baru |
| GET | `/api/reports` | 🔒 JWT | Daftar laporan |
| GET | `/api/reports/{id}` | 🔒 JWT | Detail laporan |
| PATCH | `/api/reports/{id}/status` | 🔒 ADMIN_RT | Update status |
| PATCH | `/api/reports/{id}/assign` | 🔒 ADMIN_RT | Assign ke relawan |
| POST | `/api/reports/{id}/comments` | 🔒 JWT | Tambah komentar |
| GET | `/api/reports/{id}/comments` | 🔒 JWT | Lihat komentar |

### Announcements (Tahap 3)

| Method | Endpoint | Auth | Keterangan |
|---|---|---|---|
| POST | `/api/announcements` | 🔒 ADMIN_RT | Buat pengumuman |
| GET | `/api/announcements` | 🔒 JWT | Daftar pengumuman |
| GET | `/api/announcements/{id}` | 🔒 JWT | Detail pengumuman |
| DELETE | `/api/announcements/{id}` | 🔒 ADMIN_RT | Hapus pengumuman |

### Events (Tahap 3)

| Method | Endpoint | Auth | Keterangan |
|---|---|---|---|
| POST | `/api/events` | 🔒 ADMIN_RT | Buat jadwal kegiatan |
| GET | `/api/events` | 🔒 JWT | Daftar kegiatan |
| GET | `/api/events/{id}` | 🔒 JWT | Detail kegiatan |
| DELETE | `/api/events/{id}` | 🔒 ADMIN_RT | Hapus kegiatan |

### Dashboard (Tahap 3)

| Method | Endpoint | Auth | Keterangan |
|---|---|---|---|
| GET | `/api/dashboard/summary` | 🔒 ADMIN_RT | Statistik laporan |

---

## 👤 Role Pengguna

| Role | Keterangan |
|---|---|
| `WARGA` | Pengguna biasa. Bisa membuat dan melihat laporan miliknya |
| `ADMIN_RT` | Admin RT/RW. Full akses ke semua fitur |
| `RELAWAN` | Relawan. Bisa update progress laporan yang ditugaskan |

> **Catatan**: Semua pengguna yang register mendapat role `WARGA` secara default. Role lain harus diset manual di database oleh admin.

---

## 🔑 Contoh Penggunaan API

### 1. Register

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Budi Santoso",
    "email": "budi@example.com",
    "password": "password123",
    "rt": "001",
    "rw": "005",
    "phone": "08123456789",
    "address": "Jl. Merdeka No. 1"
  }'
```

### 2. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "budi@example.com",
    "password": "password123"
  }'
```

### 3. Akses Protected Endpoint

```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer {JWT_TOKEN_DARI_LOGIN}"
```

---

## 🧪 Testing

```bash
# Jalankan semua test (membutuhkan Docker untuk Testcontainers)
mvn test

# Jalankan hanya unit test
mvn test -Dtest=AuthServiceTest

# Jalankan hanya integration test
mvn test -Dtest=AuthIntegrationTest
```

> **Testcontainers** akan otomatis menarik image `postgres:16-alpine` dari Docker Hub saat test dijalankan.

---

## 🐳 Alternatif: Docker Compose (tanpa DDEV)

```bash
# Jalankan Spring Boot + PostgreSQL dengan docker-compose
docker-compose up -d

# Cek log
docker-compose logs -f app

# Stop
docker-compose down
```

---

## ☸️ Kubernetes (Portfolio)

```bash
# Apply semua manifest
kubectl apply -f k8s/

# Cek status
kubectl get pods -n wargacare
kubectl get services -n wargacare
```

---

## 📁 Struktur Project

```
warga-care/
├── src/main/java/com/wargacare/
│   ├── WargaCareApplication.java
│   ├── config/
│   │   ├── OpenApiConfig.java       # Swagger config
│   │   └── SecurityConfig.java      # Spring Security config
│   ├── security/
│   │   ├── JwtUtil.java             # JWT generate & validate
│   │   ├── JwtAuthenticationFilter.java
│   │   └── UserDetailsServiceImpl.java
│   ├── auth/
│   │   ├── AuthController.java      # REST endpoints
│   │   ├── AuthService.java         # Business logic
│   │   └── dto/
│   │       ├── RegisterRequest.java
│   │       ├── LoginRequest.java
│   │       └── AuthResponse.java
│   ├── user/
│   │   ├── User.java                # JPA Entity
│   │   ├── UserRepository.java
│   │   ├── UserRole.java            # Enum
│   │   └── dto/
│   │       └── UserResponse.java
│   └── common/
│       ├── ApiResponse.java         # Generic response wrapper
│       └── GlobalExceptionHandler.java
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/
│       └── V1__init_users_table.sql
├── src/test/java/com/wargacare/auth/
│   ├── AuthIntegrationTest.java     # Testcontainers
│   └── AuthServiceTest.java         # Mockito
├── .ddev/
│   └── config.yaml                  # DDEV config (MariaDB 10.11)
├── Dockerfile                       # Multi-stage build
├── docker-compose.yml               # Alternatif tanpa DDEV
├── k8s/                             # Kubernetes manifests
├── .github/workflows/ci.yml         # GitHub Actions
├── README.md
└── pom.xml
```

---

## 🔧 Environment Variables

| Variable | Default (local) | Keterangan |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/wargacare` | Database URL |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `postgres` | Database password |
| `JWT_SECRET` | *(wajib diset di production)* | JWT signing secret |
| `JWT_EXPIRATION` | `86400000` | Token expiry dalam ms (24 jam) |

> **⚠️ Production**: Ganti semua default value dengan nilai yang aman dan simpan di Kubernetes Secret atau environment variable terenkripsi.

---

## 📋 DDEV Commands Cheatsheet

```bash
ddev start          # Start semua service (web + db)
ddev stop           # Stop semua service
ddev restart        # Restart service
ddev describe       # Lihat status, URL, dan port
ddev mysql          # Buka MariaDB CLI
ddev ssh            # SSH ke dalam container
ddev exec <cmd>     # Jalankan command di dalam container
ddev logs           # Lihat log container
ddev poweroff       # Matikan semua DDEV project
ddev delete --omit-snapshot  # Hapus project DDEV (hati-hati!)
```

---

## 📝 Catatan Pengembang

### Flyway Migration
- File migration ada di `src/main/resources/db/migration/`
- Penamaan: `V{nomor}__{deskripsi}.sql` (contoh: `V1__init_users_table.sql`)
- Jangan pernah ubah migration file yang sudah dijalankan di production

### JWT Secret
- Untuk development, set `JWT_SECRET` lewat environment variable atau Run Configuration
- Untuk production, **wajib** set `JWT_SECRET` via environment variable
- Secret harus minimal 256-bit (32 karakter) untuk HMAC-SHA256

### Password
- Password di-hash menggunakan BCrypt (strength 10)
- Password tidak pernah direturn dalam response API

---

*Dibuat untuk portofolio Backend Developer — Java, Spring Boot, PostgreSQL, Docker, Kubernetes*
