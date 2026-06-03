#!/bin/bash

# ============================================================
# WargaCare — Script untuk menjalankan Spring Boot
# Cara pakai: ./run.sh
# ============================================================

set -e

JAVA_HOME_PATH="$HOME/.local/share/mise/installs/java/21.0.2"
MVN_HOME_PATH="$HOME/.local/share/mise/installs/maven/3.9.9/apache-maven-3.9.9"

export JAVA_HOME="$JAVA_HOME_PATH"
export PATH="$JAVA_HOME_PATH/bin:$MVN_HOME_PATH/bin:$PATH"

echo "================================================"
echo "  WargaCare — Spring Boot Runner"
echo "================================================"

if ! java --version &>/dev/null; then
    echo "[ERROR] Java 21 tidak ditemukan di: $JAVA_HOME_PATH"
    echo "Jalankan: mise install java@21.0.2 && mise use java@21.0.2"
    exit 1
fi

echo "[INFO] Java version:"
java --version

if ! ddev describe &>/dev/null; then
    echo ""
    echo "[INFO] DDEV belum running. Menjalankan ddev start..."
    ddev start
fi

DB_PORT=$(ddev describe 2>/dev/null | grep "3306 ->" | grep -oP '127\.0\.0\.1:\K[0-9]+')

if [ -z "$DB_PORT" ]; then
    echo "[ERROR] Tidak dapat menemukan port MariaDB dari DDEV."
    echo "Coba: ddev start, lalu jalankan script ini lagi."
    exit 1
fi

echo ""
echo "[INFO] MariaDB DDEV port: $DB_PORT"
echo "[INFO] Starting Spring Boot di http://localhost:8080 ..."
echo "[INFO] Swagger UI: http://localhost:8080/swagger-ui.html"
echo "================================================"
echo ""

SPRING_DATASOURCE_URL="jdbc:mariadb://127.0.0.1:${DB_PORT}/db" \
SPRING_DATASOURCE_USERNAME="db" \
SPRING_DATASOURCE_PASSWORD="db" \
JWT_SECRET="wargacare-ddev-development-secret-key-256bit-long" \
JWT_EXPIRATION="86400000" \
mvn clean spring-boot:run
