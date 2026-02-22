#!/bin/bash
# Ajouts de Aziz — Script Wait-for-services pour Docker — 2026-02-21

set -e

echo "Waiting for Config Server to be available..."
until curl -s http://config-server:8888/actuator/health | grep '"status":"UP"'; do
  printf '.'
  sleep 5
done
echo " Config Server is UP!"

echo "Waiting for Eureka Server to be available..."
until curl -s http://eureka-server:8761/actuator/health | grep '"status":"UP"'; do
  printf '.'
  sleep 5
done
echo " Eureka Server is UP!"

echo "Starting original command..."
exec "$@"
