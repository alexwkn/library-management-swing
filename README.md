# library-management-swing

Application de bureau de gestion du **réseau des bibliothèques de Namur** : membres, livres, emprunts et statistiques.

Projet de groupe réalisé en 2e année de Bachelier en développement d'applications (Henallux, 2024-2025).

## Fonctionnalités

- **Gestion des membres** : création, modification, suppression, avec validation des saisies et contrôle des doublons (email, pseudo)
- **Gestion des livres** : ISBN, auteurs (créés à la volée), catégories, bibliothèque, disponibilité, prix…
- **Recherches**
  - livres d'un auteur dans une bibliothèque
  - livres publiés à une date donnée, filtrables par catégorie
  - emprunts effectués sur une période
- **Statistiques**
  - auteurs les plus lus sur une période
  - catégories préférées des membres par tranche d'âge
- **Animation** exécutée dans un thread séparé, avec mise à jour de l'interface sur l'EDT de Swing

## Architecture

Application en couches, chaque couche ne dépendant que de la suivante :

```
view        Interface Swing (pages, formulaires, tableaux)
  ↓
controller  Point d'entrée unique des vues
  ↓
business    Règles métier et validation des données
  ↓
dao         Accès aux données en JDBC (interfaces + implémentations MySQL)
  ↓
MySQL
```

Quelques choix techniques :

- **Requêtes préparées** partout (aucune concaténation de saisie utilisateur dans le SQL)
- **Transactions** pour l'enregistrement d'un livre et de ses associations (auteurs, catégories)
- **Mots de passe hachés** avec PBKDF2-HMAC-SHA256 et un sel aléatoire ; ils ne sont jamais relus depuis la base
- **DAO injectables** : la couche métier est testée avec des implémentations en mémoire, sans base de données
- **Configuration externe** de la connexion (variables d'environnement)

## Stack technique

| | |
|---|---|
| Langage | Java 17 |
| Interface | Swing |
| Base de données | MySQL 8.4 (JDBC, `mysql-connector-j`) |
| Build | Maven |
| Tests | JUnit 5 |
| Environnement | Docker Compose (base de démonstration) |

## Lancer le projet

### Prérequis

- JDK 17 ou plus récent
- [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- Maven, ou un IDE qui l'intègre (IntelliJ IDEA, Eclipse, VS Code)

### 1. Démarrer la base de données

```bash
git clone https://github.com/alexwkn/library-management-swing.git
cd library-management-swing
docker compose up -d
```

MySQL démarre sur le port **3307** et est initialisé automatiquement avec les données de démonstration (`database/librarynamur.sql`).

### 2. Lancer l'application

**Depuis un IDE** : ouvrir le dossier (le projet Maven est détecté automatiquement) puis exécuter `com.librarymanagement.view.Main`.

**En ligne de commande** :

```bash
mvn compile exec:java
```

ou en construisant un JAR exécutable :

```bash
mvn package
java -jar target/library-management-swing.jar
```

Pour arrêter la base : `docker compose down` (ajouter `-v` pour repartir des données de démonstration).

### Configuration

Par défaut, l'application se connecte à la base lancée par Docker. Pour utiliser une autre base, définir ces variables d'environnement :

| Variable | Valeur par défaut |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3307/librarynamur?allowPublicKeyRetrieval=true&useSSL=false` |
| `DB_USER` | `library` |
| `DB_PASSWORD` | `library` |

## Tests

```bash
mvn test
```

- Les **tests unitaires** (règles métier, validation, hachage des mots de passe) s'exécutent sans base de données.
- Les **tests d'intégration** (`DatabaseIntegrationTest`) interrogent la base de démonstration en lecture seule ; ils sont ignorés automatiquement si elle n'est pas démarrée.

## Données de démonstration

La base contient les 4 bibliothèques du réseau namurois, une soixantaine de titres, 75 membres et leurs emprunts. Les membres sont **fictifs** (adresses email en `example.com`) et leurs mots de passe sont stockés hachés.

## Structure du projet

```
.
├── database/librarynamur.sql          # Schéma et données de démonstration
├── docker-compose.yml                 # Base MySQL de démonstration
├── pom.xml
└── src
    ├── main/java/com/librarymanagement
    │   ├── view/                      # Interface Swing
    │   ├── controller/
    │   ├── business/                  # Règles métier
    │   ├── dao/                       # Accès JDBC
    │   ├── model/
    │   ├── exception/
    │   └── util/                      # Validation, hachage des mots de passe
    ├── main/resources/assets/
    └── test/java/com/librarymanagement
```

## Auteurs

- Alexandre Wenkin – [@alexwkn](https://github.com/alexwkn)
- Antoine-Vi Son Nguyen – [@Drastae](https://github.com/Drastae)
- [@nini2802](https://github.com/nini2802)
