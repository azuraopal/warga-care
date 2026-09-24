#!/bin/sh
set -e

mkdir -p /app/uploads/events /app/uploads/reports /app/uploads/general

DEFAULT_JVM_OPTS="-XX:+UseContainerSupport \
-XX:MaxRAMPercentage=50.0 \
-XX:InitialRAMPercentage=20.0 \
-Xss512k \
-XX:+UseG1GC \
-XX:G1PeriodicGCInterval=15000 \
-XX:+G1PeriodicGCSys \
-XX:+UseStringDeduplication \
-XX:+ExitOnOutOfMemoryError \
-Djava.security.egd=file:/dev/./urandom"

JVM_OPTS="${JAVA_OPTS:-$DEFAULT_JVM_OPTS}"

if [ "$(id -u)" = '0' ]; then
    chown -R wargacare:wargacare /app/uploads
    chmod -R 775 /app/uploads
    exec su-exec wargacare java $JVM_OPTS -jar app.jar "$@"
fi

exec java $JVM_OPTS -jar app.jar "$@"
