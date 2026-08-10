-- Create categories table for dynamic category CRUD
CREATE TABLE categories (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL, -- e.g. EVENT, REPORT
    description VARCHAR(255) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Alter image_url in events to TEXT to support longer URLs or paths
ALTER TABLE events ALTER COLUMN image_url TYPE TEXT;

-- Seed default categories
INSERT INTO categories (name, type, description) VALUES
('Kerja Bakti & Kebersihan', 'EVENT', 'Kegiatan kerja bakti dan gotong royong warga'),
('Rapat & Musyawarah', 'EVENT', 'Rapat koordinasi warga dan pengurus RT/RW'),
('Kesehatan & Posyandu', 'EVENT', 'Pemeriksaan kesehatan, posyandu balita dan lansia'),
('Olahraga & Seni', 'EVENT', 'Kegiatan keolahragaan dan kesenian warga'),
('Jalan Rusak', 'REPORT', 'Laporan perbaikan jalan dan fasilitas lingkungan'),
('Pengelolaan Sampah', 'REPORT', 'Laporan tumpukan sampah dan pengangkutan'),
('Lampu Penerangan Jalan', 'REPORT', 'Laporan penerangan jalan mati atau rusak'),
('Banjir / Drainase', 'REPORT', 'Laporan saluran air mampet dan genangan'),
('Keamanan / Ketertiban', 'REPORT', 'Laporan pos kamling dan gangguan keamanan'),
('Lainnya', 'REPORT', 'Laporan pengaduan kategori umum');
