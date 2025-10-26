# Smart Grid – Système de Gestion Intelligente de l'Énergie

Ce projet simule un **Smart Grid** – un réseau énergétique intelligent capable de collecter, traiter et visualiser des données de production d’énergie provenant de différents capteurs (panneaux solaires et éoliennes).

---

## Objectif du Projet
Développer un système complet capable de :
- Recevoir des données provenant de capteurs distribués.
- Gérer différents protocoles de communication (UDP & HTTP).
- Stocker et agréger les informations dans une base de données PostgreSQL.
- Visualiser les données en temps réel via une interface web.
- Déployer le tout sous **Docker** pour un fonctionnement modulaire et portable.

---

## Architecture Générale

Le système se compose de plusieurs modules :

| Composant | Description |
|------------|-------------|
| **Backend (Java – Vert.x)** | Serveur central qui gère les requêtes et la logique métier. |
| **Capteurs Solaires** | Envoient leurs données via **UDP**. |
| **Capteurs Éoliens** | Envoient leurs données via **HTTP POST**. |
| **Base de Données (PostgreSQL)** | Stocke les informations sur les capteurs et la production totale. |
| **Frontend (Web)** | Interface graphique permettant de visualiser les grilles et les mesures. |
| **Docker** | Chaque composant (base, backend, capteurs, interface) tourne dans son propre conteneur. |

---

## Technologies Utilisées

- **Langage :** Java (Vert.x)
- **Base de données :** PostgreSQL
- **Conteneurisation :** Docker & Docker Compose
- **Communication :** UDP / HTTP
- **Frontend :** Interface web simple (HTML / JS)
- **Tests :** Curl, Postman, navigateur

---

## Structure du Projet

.
├── src/                     # Code source Java (handlers, VertxServer.java, etc.)

├── build.gradle             # Configuration du projet Gradle

├── docker-compose.yml       # Définition des conteneurs Docker

├── init_database.sql        # Script SQL d’initialisation de la base

├── backend_routes.md        # Documentation des routes backend

├── gradlew / gradlew.bat    # Scripts de build Gradle

└── README.md                # Documentation du projet

---

## Fonctionnement du Backend

Le cœur du système est le fichier **`VertxServer.java`**.  
C’est lui qui :
- Configure le serveur HTTP
- Initialise les routes (REST API)
- Gère la communication entre les capteurs et la base PostgreSQL

### Principales Routes

| Route | Méthode | Description |
|-------|----------|-------------|
| `/grids` | `GET` | Récupère la liste des grilles disponibles |
| `/sensor` | `GET` / `POST` / `PUT` / `DELETE` | Gestion des capteurs |
| `/ingress` | `POST` | Réception des données des capteurs via HTTP |
| `/udp` | `UDP` | Réception des données envoyées par les panneaux solaires |

Chaque route utilise un **handler spécifique**, par exemple :
- `GetGridIdsHandler` → Récupère les IDs des grilles  
- `PostSensorDataHandler` → Reçoit les données envoyées par les capteurs  
- `PutSensorHandler`, `DeleteSensorHandler`, etc.  

### Fonctionnement des Handlers

1. Connexion à la base via **EntityManager**
2. Lecture / écriture / mise à jour / suppression des données
3. Sauvegarde ou récupération des informations dans **PostgreSQL**

---

## Tests et Validation

- Les requêtes **GET** (`/grids`, `/sensor`) peuvent être testées directement dans le navigateur.  
- Les requêtes **POST**, **PUT** et **DELETE** nécessitent un corps JSON :
  - Testables via **curl** ou **Postman**
  - Exemple :
curl -X POST http://localhost:8080/sensor \
  -H "Content-Type: application/json" \
  -d '{"id": "sensor-1", "type": "solar", "temperature": 28.5, "power": 12.3}'

---

## Déploiement avec Docker
Tous les services tournent dans des conteneurs Docker.

**Lancer le projet :**
bash
docker-compose up --build

**Arrêter le projet :**
docker-compose down

## Équipe du Projet:
Sara El Bari – Développement backend, intégration Vert.x & PostgreSQL

## Licence
Projet académique réalisé dans le cadre de l’IMT Atlantique.
Distribué sous licence Apache 2.0.

## Résumé
Ce projet démontre la mise en place d’une architecture distribuée avec gestion multi-protocole, stockage persistant et visualisation des données en temps réel.
Il illustre parfaitement l’intégration logiciel embarqué, cloud et réseau au sein d’un même système.

## Pre-requis

- un JRE pour faire tourner gradle
- Docker : guide d'installation [ici](https://docs.docker.com/engine/install/)

## Mise en place

- Placez vous dans ce répertoire et exécutez la commande `docker compose up -d` pour lancer le serveur postgresql
- Lancez le projet avec `./gradlew.bat run` (utilisez `gradlew` sur macOS / linux)

Le backend est accessible sur le port `8080`, le frontend est accessible [ici](http://localhost:8082).
Une interface web pour administrer la base de données est accessible [ici](http://localhost:80801), sélectionnez `PostgreSQL` comme système, `db` comme serveur et `test` comme utilisateur/mot de passe/base de données. 
