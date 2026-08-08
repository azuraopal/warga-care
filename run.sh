#!/bin/bash

# ============================================================
# WargaCare — Script untuk menjalankan Spring Boot
# Cara pakai: ./run.sh
# Pastikan PostgreSQL sudah berjalan dan database sudah dibuat.
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

echo ""
echo "[INFO] PostgreSQL akan digunakan dari environment/local host."
echo "[INFO] Starting Spring Boot di http://localhost:8080 ..."
echo "[INFO] Swagger UI: http://localhost:8080/swagger-ui.html"
echo "================================================"
echo ""

SPRING_DATASOURCE_URL="${SPRING_DATASOURCE_URL:-jdbc:postgresql://127.0.0.1:5432/mac}" \
SPRING_DATASOURCE_USERNAME="${SPRING_DATASOURCE_USERNAME:-postgres}" \
SPRING_DATASOURCE_PASSWORD="${SPRING_DATASOURCE_PASSWORD:-postgres}" \
JWT_SECRET="wargacare-local-development-secret-key-256bit-long" \
JWT_EXPIRATION="86400000" \
mvn clean spring-boot:run
