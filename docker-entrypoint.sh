#!/bin/sh
set -e

# Pastikan folder uploads dan subfoldernya ada dengan permission yang tepat
mkdir -p /app/uploads/events /app/uploads/reports /app/uploads/general

# Jika dijalankan sebagai root (saat container start), atur ownership dan permission ke user wargacare
if [ "$(id -u)" = '0' ]; then
    chown -R wargacare:wargacare /app/uploads
    chmod -R 775 /app/uploads
    exec su-exec wargacare java \
        -XX:+UseContainerSupport \
        -XX:MaxRAMPercentage=75.0 \
        -Djava.security.egd=file:/dev/./urandom \
        -jar app.jar "$@"
fi

# Fallback jika dijalankan langsung tanpa root
exec java \
    -XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -Djava.security.egd=file:/dev/./urandom \
    -jar app.jar "$@"
