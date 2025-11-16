# Kata-delivery

# Description

Kata delivery est une  application qui permet aux clients de choisir leur mode de livraison ainsi que leur jour et créneau horaire pour la réception de leurs commandes. Elle gère plusieurs types de livraison et intègre un système de réservation de créneaux partagé entre tous les utilisateurs.


## Sommaire

- [Pré-requis](#pré-requis)
- [Installation](#Installation)
- [Utilisation](#Utilisation)
- [Documentation](#Documentation)
- [Règles-Métier](#Règles-Métier)
- [Tests](#Tests)
- [Stack-technique](#Stack-Technique)
- [Architecture](#Architecture)
- [Docker](#Docker)

# Pré-requis

- Docker et Docker compose

# Installation

1. Cloner le projet :

```  
git clone https://github.com/smissaoui/kata-delivery.git
```   
2. S'assurer que Docker est correctement installé.
3. Démarrer les services à l'aide de Docker Compose.

# Utilisation

L’application expose trois contrôleurs principaux : AuthController, TimeSlotController et ReservationController.
Ci-dessous, une présentation succincte de leur rôle et des endpoints associés.

### AuthController

Contrôleur d’authentification permettant de générer un token JWT.

Endpoint :

```  
POST /auth/login
```   

Usage :

- Envoyer un username et un password (ceux définis dans application.yml).

- Récupérer le token JWT retourné.

- Ajouter ce token dans chaque requête protégée :

```  
Authorization: Bearer <token>
```   

Ce contrôleur est nécessaire avant d’appeler les autres endpoints.



### TimeSlotController

Permet de consulter les créneaux horaires disponibles en fonction du mode de livraison et d’une date.

Endpoint :

```  
GET /v1/timeslots?mode=DRIVE&date=2025-01-01
```   

Usage :

- Passer les paramètres mode (DRIVE, DELIVERY, DELIVERY_TODAY, DELIVERY_ASAP) et date (YYYY-MM-DD).

- Le token JWT est obligatoire.

- Le contrôleur renvoie la liste des créneaux disponibles pour ces critères.

- Ce contrôleur sert à afficher les créneaux avant qu’un client ne réalise une réservation.


### ReservationController

Gère la création et l’annulation des réservations.

--> Créer une réservation :

Endpoint :

```  
POST /v1/reservations
```   

Payload :

```  
{
"customerId": "12345",
"timeSlotId": 10
}
``` 

Utilisation :

- Envoyer un ID client et un ID de créneau.

- Le contrôleur renvoie la réservation créée.

- Token JWT obligatoire.

--> Annuler une réservation : 

Endpoint :

```  
DELETE /v1/reservations/{customerId}/{timeSlotId}
```   

Utilisation :

- Fournir l’identifiant du client et le créneau concerné.

- Retourne 204 No Content en cas de succès.

- Token JWT obligatoire.

# Documentation

Pour accéder à la documentation :

** http://localhost:8080/webjars/swagger-ui/index.html **

# Règles-Métier

Les créneaux sont produits dynamiquement en respectant les règles suivantes :

- DRIVE : créneaux disponibles uniquement en journée (entre 9h et 18h)

- DELIVERY : réservation possible jusqu’à 24 heures à l’avance.

- DELIVERY_TODAY : réservation limitée au jour même, jusqu’à 15h.

- DELIVERY_ASAP : créneau de livraison immédiat, sous 120 minutes.

# Tests

Le projet est fourni avec une suite de tests unitaires afin de garantir la qualité du code.
Ils peuvent être lancés via :

```  
mvn test
```   

Pour les lancer en local, il faudra installer les dependances necessaires (Maven, Java..)

# Stack-Technique

- Backend: Spring Boot 3.2.5, Java 21
- Test: JUnit, Mockito et WebTestClient pour les tests Webflux
- API : REST avec spring WebFlux
- BDD: PostgreSQL (utilisation de R2DBC pour les opérations non-bloquantes), liquibase.
- Cache : L’application utilise Caffeine Cache pour optimiser les performances en mettant en cache certaines données et en réduisant les accès inutiles à la base de données.
- Sécurité : La sécurité de l’API repose sur un système d’authentification JWT garantissant que seuls les utilisateurs autorisés peuvent accéder aux endpoints protégés.

# Architecture

Le projet repose sur une architecture hexagonale (Ports & Adapters).
Cette approche vise à séparer clairement le domaine métier du reste de l’application (API, base de données, frameworks…), en organisant le code autour d’un noyau central indépendant des technologies externes.


# Docker

Le projet est dockerisé pour pouvoir le lancer rapidement.

Le projet inclut un Dockerfile qui compile l’application et génère son image Docker. Un fichier docker-compose.yml est également fourni pour lancer l’application accompagnée d’une base PostgreSQL, avec un volume persistant afin de conserver les données entre les redémarrages.

### Build et démarrage de l'application :

Pour builder l'image docker et lancer l'application il suffit d'executer la commande : 

```  
docker compose up --build
```   