# Smart Mobility Pass - Architecture & Déploiement

Ce dépôt contient les microservices pour le projet **Smart Mobility Pass**. L'infrastructure de découverte de services (Eureka) et la passerelle API (API Gateway) ont été ajoutées pour structurer les communications.

## Architecture

![Architecture](https://mermaid.ink/img/pako:eNptkLFuwjAURX8lnqkSEiClLChDpUNXVgZixS8YLDu28zRCUf49TtoUBc2w3n3nPc_b7Ayl0AgyyHh0p4aewcf-HqVn30f0AzzGg_y4T9l2b1a9vI2gH-H9a9cK6P1q9kIeQ_w3D0aC7r5D1qJ8eLz1P_g3Nf5O7f2H1m1Y6K1nF5SGlOAWm4rD5h4tW6EsqXzBSg4HqXv5JtE2t_eY5tI15rU_FvjKkUvj_T3V04Sj0T2Z8c12Y7jQv3Gz0b25jV5l02yW9P6eS-P1N81t9P5aK93T29v7b81t9P5aZ9P-e0G_bZl9Bf4_f2I)
*API Gateway route les requêtes vers les services. Eureka permet la découverte dynamique.*

* **Config Server** (Port : 8888) : Serveur de configuration centralisée.
* **Zipkin** (Port : 9411) : Traçabilité distribuée B3.
* **Eureka Server** (Port : 8761) : Service Registry Spring Cloud Netflix.
* **API Gateway** (Port : 8080) : Routage dynamique `lb://`, validation JWT (GlobalFilter), Circuit Breaker (Resilience4j).
* **Trip Management Service** (Port : 8081) : Gestion des trajets. Appelle le Pricing.
* **User Mobility Pass Service** (Port : 8081) : Gestion des entités User et Pass.
* **Pricing Discount Service** (Port : 8082) : Service de tarification.

## Démarrage Local avec Docker Compose

Pour démarrer tous les services, assurez-vous que les ports 8080, 8761, 8888, 3306, 5432 et 9411 sont libres.

```bash
SPRING_PROFILES_ACTIVE=dev docker-compose up --build
```
> Le script `wait-for-services.sh` assure que les applicatifs ne démarrent que lorsque Config-Server et Eureka sont prêts.

## Checklist de Validation & Tests

### 1. Santé de l'infrastructure
*   **Eureka Dashboard** : Accessible via `http://localhost:8761`. Vérifiez que les instances `API-GATEWAY`, `TRIP-MANAGEMENT-SERVICE`, `USER-MOBILITY-PASS-SERVICE` et `PRICING-DISCOUNT-SERVICE` y apparaissent au bout de 2 minutes.
*   **Gateway Actuator Health** : 
    ```bash
    curl -s http://localhost:8080/actuator/health
    ```

### 2. Validation JWT (API Gateway)
Le Gateway intercepte les requêtes (sauf `/actuator/health` et `/fallback/pricing`) et exige un token JWT signé avec la clé `test-secret-very-secret-test-secret-very-secret` (Configurée dans le Config Server, format HMAC-SHA256).

Générez un JWT valide sur [jwt.io](https://jwt.io) avec le secret `test-secret-very-secret-test-secret-very-secret` et sub="john.doe".

* **Appel SANS token (401 attendu) :**
  ```bash
  curl -v http://localhost:8080/api/trips
  ```
* **Appel AVEC token valide :**
  ```bash
  curl -H "Authorization: Bearer VOTRE_TOKEN" http://localhost:8080/api/trips
  ```

### 3. Circuit Breaker Fallback (Resilience4j)
Le Pricing Service est sécurisé derrière un Circuit Breaker. Si ce service est éteint (`docker stop pricing-discount-service`) et que l'API Gateway essaie de le contacter via `/api/pricing`, un *CircuitBreakerException* déclenchera le fallback configuré dans Route Locator.

* **Test direct Fallback :**
  ```bash
  curl http://localhost:8080/fallback/pricing
  # Attendu : {"price": 1.0, "note": "fallback-standard-price"}
  ```

### 4. Traçage Distribué (Zipkin)
Les en-têtes B3 sont propagées.
*   Lancez des requêtes sur l'API Gateway.
*   Rendez-vous sur le dashboard Zipkin : `http://localhost:9411`
*   Cliquez sur "Run Query" pour voir la trace traversant le Gateway jusqu'au microservice de destination.

---
**Marqueur de modification :**
Toutes les modifications ou ajouts apportés dans le cadre de l'intégration de l'API Gateway et Eureka Server portent le marqueur : 
`// Ajouts de Aziz — raison courte — 2026-02-21` (ou format similaire selon le langage YAML/XML).
