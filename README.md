# 🏡 WargaCare — Sistem Pengaduan & Bantuan Warga RT/RW

> **WargaCare** adalah sistem REST API backend modern untuk pengelolaan pengaduan, pengumuman, kegiatan warga, serta integrasi AI Virtual Assistant di tingkat RT/RW/Desa. 
> 
> Dibangun menggunakan **Java 21**, **Spring Boot 3.3**, **PostgreSQL**, **Flyway**, dan **Google Gemini AI**.

---

## 🚀 Fitur Utama

- 🔐 **Authentication & Authorization**: Registration, Login JWT Token, RBAC (`WARGA`, `ADMIN_RT`, `RELAWAN`).
- 📝 **Manajemen Laporan Pengaduan**: Buat, lihat, update status pengaduan warga, lokasi (latitude/longitude), dan lampiran foto bukti.
- 📢 **Pengumuman & Kegiatan (Event)**: Informasi resmi RT/RW dan agenda kegiatan warga.
- 🤖 **AI Assistant (Google Gemini)**: Chatbot interaktif seputar administrasi RT/RW/Desa dan Karang Taruna.
- 📁 **File Upload**: Layanan unggah foto laporan, kegiatan, dan bukti penyelesaian.
- 📄 **Dokumentasi API Interactive**: Swagger UI & OpenAPI 3.

---

## 🛠️ Tech Stack

| Teknologi | Versi | Keterangan |
|---|---|---|
| **Java** | 21 | Bahasa Pemrograman Utama |
| **Spring Boot** | 3.3.0 | Framework Utama |
| **Spring Security** | 6.x | Keamanan & JWT Authentication |
| **Spring Data JPA** | 3.3.0 | Persistence & ORM |
| **PostgreSQL** | 16+ | Database Utama |
| **Flyway** | 10.x | Database Migration (V1 - V7) |
| **Google Gemini API** | v1beta | AI Virtual Assistant (`gemini-3.5-flash`) |
| **Swagger / OpenAPI** | 2.5.0 | Dokumentasi REST API |
| **Maven** | 3.9+ | Build Tool & Dependency Manager |

---

## ⚡ Panduan Cepat Menjalankan Project

### 📋 Prasyarat Sistem
- **Java 21** (JDK 21)
- **PostgreSQL** (Database `warga-care` atau buat database baru `wargacare`)
- **Maven** (atau gunakan `./run.sh` yang sudah menyertakan wrapper)

---

### 1️⃣ Clone & Masuk ke Folder Project

```bash
git clone https://github.com/username/warga-care.git
cd warga-care
```

---

### 2️⃣ Konfigurasi Environment (`.env`)

Buat file `.env` di root project atau gunakan file `.env` yang sudah disediakan:

```ini
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/wargacare
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
JWT_SECRET=wargacare-local-development-secret-key-256bit-long
JWT_EXPIRATION=86400000
GEMINI_API_KEY=your-gemini-api-key-here
```

> **Note**: Konfigurasi di atas akan otomatis dimuat oleh Spring Boot melalui file `.env`.

---

### 3️⃣ Jalankan Database (PostgreSQL)

Pastikan PostgreSQL sudah berjalan dan database target sudah dibuat:

```sql
-- Untuk PostgreSQL 18+ / ICU provider:
CREATE DATABASE wargacare TEMPLATE template0;
```

---

### 4️⃣ Menjalankan Aplikasi

#### Cara 1: Menggunakan Script `run.sh` (Linux/macOS)

```bash
chmod +x run.sh
./run.sh
```

#### Cara 2: Menggunakan Maven Langsung

```bash
mvn clean spring-boot:run
```

#### Cara 3: Menggunakan Docker Compose

```bash
docker-compose up -d
```

---

### 5️⃣ Akses Dokumentasi & API

Setelah aplikasi berjalan, buka browser dan akses:

| Akses | URL |
|---|---|
| 📖 **Swagger UI** | [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) |
| 📄 **OpenAPI Specs** | [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs) |
| ⚡ **API Base URL** | `http://localhost:8080/api` |

---

## 📡 REST API Overview

### 🔑 Authentication (`/api/auth`)
- `POST /api/auth/register` — Registrasi akun warga baru
- `POST /api/auth/login` — Login & dapatkan token JWT
- `GET /api/auth/me` — Ambil profil user yang sedang login

### 📋 Reports / Laporan (`/api/reports`)
- `POST /api/reports` — Buat laporan pengaduan (WARGA)
- `GET /api/reports` — Lihat semua laporan (dengan filter kategori/status/RT/RW)
- `GET /api/reports/{id}` — Detail laporan
- `PATCH /api/reports/{id}/status` — Update status laporan (ADMIN_RT)
- `PATCH /api/reports/{id}/assign` — Assign laporan ke relawan (ADMIN_RT)
- `POST /api/reports/{id}/comments` — Tambah komentar pada laporan

### 🤖 AI Virtual Assistant (`/api/ai`)
- `POST /api/ai/chat` — Tanya jawab dengan AI Virtual Assistant RT/RW

### 📢 Pengumuman & Kegiatan (`/api/announcements`, `/api/events`)
- `GET /api/announcements` — Daftar pengumuman RT/RW
- `POST /api/announcements` — Buat pengumuman baru (ADMIN_RT)
- `GET /api/events` — Agenda kegiatan warga
- `POST /api/events` — Buat kegiatan baru (ADMIN_RT)

### 📁 Upload File (`/api/upload`)
- `POST /api/upload/reports` — Unggah foto bukti laporan
- `POST /api/upload/events` — Unggah foto kegiatan

---

## 🧪 Menjalankan Test

```bash
# Jalankan unit test dan integration test
mvn test
```

---

## 📂 Struktur Folder Project

```text
warga-care/
├── k8s/                     # Manifest Deployment Kubernetes
├── src/
│   ├── main/
│   │   ├── java/com/wargacare/
│   │   │   ├── ai/          # Controller & Service Gemini AI
│   │   │   ├── announcement/# Modul Pengumuman
│   │   │   ├── auth/        # Auth & JWT Service
│   │   │   ├── category/    # Category Management
│   │   │   ├── common/      # Exception Handler & Response DTO
│   │   │   ├── dashboard/   # Dashboard Statistics
│   │   │   ├── event/       # Modul Kegiatan Warga
│   │   │   ├── report/      # Modul Pengaduan Warga
│   │   │   ├── security/    # Filter & Security Config
│   │   │   ├── upload/      # Controller File Storage
│   │   │   └── user/        # Management User & Roles
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/migration/# Migration Script Flyway (V1-V7)
│   └── test/                # Unit & Integration Tests
├── Dockerfile
├── mise.toml
├── pom.xml
└── run.sh
```

---

## 📝 Lisensi & Kontribusi

Dipublikasikan untuk keperluan tata kelola lingkungan warga RT/RW/Desa. Kontribusi dan Pull Request sangat dialu-alukan! 🚀

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
