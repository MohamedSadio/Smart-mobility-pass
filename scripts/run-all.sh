#!/bin/bash
# ==============================================================================
# run-all.sh — Orchestration séquentielle locale Smart-mobility-pass
# fix(scripts): run-all refactorisé 2026-02-22 — localhost, Java 17, native config
# ==============================================================================

set -euo pipefail
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
LOG_DIR="$ROOT/logs"
mkdir -p "$LOG_DIR"

# ── Variables d'environnement ──────────────────────────────────────────────────
export JAVA_HOME="${JAVA_HOME:-$(/usr/libexec/java_home -v 17 2>/dev/null || echo '')}"
export EUREKA_URL="http://localhost:8761/eureka/"
export DB_USERNAME="${DB_USERNAME:-postgres}"
export DB_PASSWORD="${DB_PASSWORD:-postgres}"

echo "============================================"
echo " Smart-mobility-pass — Démarrage local"
echo " Java : $(java -version 2>&1 | head -1)"
echo " Root : $ROOT"
echo "============================================"

# ── Fonction utilitaire : attendre qu'un endpoint health réponde UP ───────────
wait_for_health() {
  local name="$1"
  local url="$2"
  local max_attempts="${3:-30}"
  local attempt=0
  echo "   ⏳ Attente $name ($url) ..."
  until curl -sf "$url" 2>/dev/null | grep -q '"status":"UP"'; do
    attempt=$((attempt + 1))
    if [ "$attempt" -ge "$max_attempts" ]; then
      echo "   ❌ TIMEOUT $name après ${max_attempts} tentatives"
      echo "   Dernières lignes de log :"
      ls "$LOG_DIR/${name}.log" 2>/dev/null && tail -30 "$LOG_DIR/${name}.log" || true
      return 1
    fi
    sleep 3
  done
  echo "   ✅ $name est UP"
}

# ── 1. Config Server ─────────────────────────────────────────────────────────
echo ""
echo "1) Démarrage Config Server (port 8888)..."
cd "$ROOT/config-server/config-server"
mvn -q spring-boot:run \
  > "$LOG_DIR/config-server.log" 2>&1 &
CONFIG_PID=$!
cd "$ROOT"
wait_for_health "config-server" "http://localhost:8888/actuator/health" 30

# ── 2. Eureka Server ─────────────────────────────────────────────────────────
echo ""
echo "2) Démarrage Eureka Server (port 8761)..."
cd "$ROOT/eureka-server"
SPRING_PROFILES_ACTIVE=dev mvn -q spring-boot:run \
  > "$LOG_DIR/eureka-server.log" 2>&1 &
EUREKA_PID=$!
cd "$ROOT"
wait_for_health "eureka-server" "http://localhost:8761/actuator/health" 30

# ── 3. Services métiers (parallèle) ──────────────────────────────────────────
echo ""
echo "3) Démarrage services métiers en parallèle..."

cd "$ROOT/user-mobility-pass-service/user-mobility-pass-service"
SPRING_PROFILES_ACTIVE=dev mvn -q spring-boot:run \
  > "$LOG_DIR/user-mobility-pass-service.log" 2>&1 &
USER_PID=$!
cd "$ROOT"

cd "$ROOT/trip-management-service/trip-management-service/trip-management-service"
SPRING_PROFILES_ACTIVE=dev mvn -q spring-boot:run \
  > "$LOG_DIR/trip-management-service.log" 2>&1 &
TRIP_PID=$!
cd "$ROOT"

cd "$ROOT/pricing-discount-service/pricing-discount-service/pricing-discount-service"
SPRING_PROFILES_ACTIVE=dev mvn -q spring-boot:run \
  > "$LOG_DIR/pricing-discount-service.log" 2>&1 &
PRICING_PID=$!
cd "$ROOT"

echo "   ⏳ Attente 20s pour les services métiers..."
sleep 20

# ── 4. API Gateway ────────────────────────────────────────────────────────────
echo ""
echo "4) Démarrage API Gateway (port 8080)..."
cd "$ROOT/api-gateway"
SPRING_PROFILES_ACTIVE=dev mvn -q spring-boot:run \
  > "$LOG_DIR/api-gateway.log" 2>&1 &
GATEWAY_PID=$!
cd "$ROOT"
wait_for_health "api-gateway" "http://localhost:8080/actuator/health" 40

# ── 5. Résumé ─────────────────────────────────────────────────────────────────
echo ""
echo "============================================"
echo " ✅ Tous les services sont démarrés !"
echo ""
echo " Config Server : http://localhost:8888/actuator/health"
echo " Eureka UI     : http://localhost:8761"
echo " API Gateway   : http://localhost:8080/actuator/health"
echo ""
echo " Test JWT secret:"
echo "   curl -s http://localhost:8888/api-gateway/dev | python3 -m json.tool | grep secret"
echo ""
echo " Appuyer sur Ctrl+C pour arrêter tous les services"
echo "============================================"

# Attendre tous les processus
wait $CONFIG_PID $EUREKA_PID $USER_PID $TRIP_PID $PRICING_PID $GATEWAY_PID
