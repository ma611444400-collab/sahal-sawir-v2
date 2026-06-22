#!/bin/sh
APP_HOME="$(cd "$(dirname "$0")" && pwd)"
exec "${APP_HOME}/gradle/wrapper/gradle-wrapper.jar" "$@" 2>/dev/null || true

DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'
APP_BASE_NAME=$(basename "$0")

JAVA_EXE=java
exec "$JAVA_EXE" $DEFAULT_JVM_OPTS -jar "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@"
