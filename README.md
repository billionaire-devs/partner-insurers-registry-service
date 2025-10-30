# Gestion des Assureurs Partenaires

# Microservice : `partner-insurer-core-service`

**Version : 1.0**

**Date : Octobre 2025**

**Auteur: MAKOSSO Loïck Esdras**

---

## 1. Objectif du microservice

`partner-insurer-core-service` est le **registre maître des assureurs partenaires** de la plateforme Bamboo Assur.

Il gère :

- les **informations légales et administratives** des assureurs partenaires,
- leurs **contacts de référence**,
- les **accords de partenariat (broker agreements)**, y compris la configuration de paiement,
- la **publication d’événements métier** cohérents pour les autres microservices.

Ce service **ne traite ni authentification, ni autorisation**, ni intégration technique.

Il dépend du **`identity-service`** pour la validation des utilisateurs et délègue toute communication technique au **`insurer-integration-service`**.

---

## 2. Rôle dans l’écosystème

| Microservice | Type d’interaction | Données échangées |
| --- | --- | --- |
| `partner-portal-service` | REST (lecture seule) | Liste et détails des partenaires / contrats |
| `document-exchange-service` | Events (RabbitMQ) | Références de documents associés à un contrat |
| `insurer-integration-service` | Events | Notification création / activation partenaires |
| `claims-service` | Events | Association sinistres ↔ assureur |
| `identity-service` | REST | Introspection JWT, validation des rôles |

Le `partner-insurer-core-service` est une **source de vérité métier**, mais **non technique**.

---

## 3. Domaine fonctionnel

Chaque fonctionnalité est identifiée par un **ID unique PIS-COR-XXX**.

Ce service couvre **trois sous-domaines** :

1. Gestion des partenaires assureurs
2. Gestion des contacts (VO)
3. Gestion des accords de partenariat (agreements)

---

### A. Gestion des partenaires assureurs

| ID | Fonctionnalité | Description |
| --- | --- | --- |
| **PIS-COR-101** | Création d’un partenaire | Créer un assureur avec code unique, nom légal, NIF, adresse, logo, statut `ONBOARDING`. |
| **PIS-COR-102** | Consultation | Lecture d’un partenaire par ID ou code. |
| **PIS-COR-103** | Recherche filtrée | Pagination et filtres (status, nom, date). |
| **PIS-COR-104** | Mise à jour | Modification d’informations (nom, adresse, logo). |
| **PIS-COR-105** | Changement de statut | Transition de statut (ONBOARDING → ACTIVE / SUSPENDED / REVOKED). |
| **PIS-COR-106** | Suppression logique | Marquage `deleted_at`, `deleted_by`. |

**Événements générés :**

- `PartnerInsurerCreated`
- `PartnerInsurerActivated`
- `PartnerInsurerSuspended`
- `PartnerInsurerRevoked`

---

### B. Gestion des contacts du partenaire

| ID | Fonctionnalité | Description |
| --- | --- | --- |
| **PIS-COR-120** | Ajout de contact | Enregistrement d’un contact associé au partenaire. |
| **PIS-COR-121** | Mise à jour d’un contact | Modification des champs (nom, email, téléphone, rôle). |
| **PIS-COR-122** | Suppression logique de contact | Soft delete avec traçabilité. |
| **PIS-COR-123** | Liste des contacts | Récupération paginée pour un partenaire. |

---

### C. Gestion des accords de partenariat

| ID | Fonctionnalité | Description |
| --- | --- | --- |
| **PIS-COR-140** | Création d’accord | Création d’un enregistrement `broker_partner_insurer_agreement` avec toutes ses métadonnées. |
| **PIS-COR-141** | Mise à jour d’accord | Mise à jour des champs non immuables (titre, dates, pénalités, etc.). |
| **PIS-COR-142** | Changement de statut d’accord | DRAFT → ACTIVE / EXPIRED / TERMINATED. |
| **PIS-COR-143** | Expiration automatique | Tâche planifiée pour marquer les accords expirés et émettre un event. |
| **PIS-COR-144** | Consultation d’accords | Liste ou consultation par ID. |
| **PIS-COR-145** | Validation du `payment_method` | Vérification structurelle du JSON (BankTransfer, MobileMoney, Manual). |

**Événements générés :**

- `PartnerAgreementCreated`
- `PartnerAgreementUpdated`
- `PartnerAgreementExpired`
- `PartnerAgreementTerminated`

---

### D. Audit et événements métier

| ID | Fonctionnalité | Description |
| --- | --- | --- |
| **PIS-COR-180** | Enregistrement d’événement métier | Insertion dans `outbox` table dans la même transaction. |
| **PIS-COR-181** | Dispatcher Outbox → RabbitMQ | Traitement des événements PENDING → SENT / FAILED. |
| **PIS-COR-182** | Audit trail interne | Journalisation des changements CRUD dans les tables partenaires et accords. |

---

## 4. Structure de données

Les schémas sont issus directement des migrations SQL existantes (`V1__Create_partner_insurer_schema.sql`, `V1.1__Create_outbox_table.sql`).

### 4.1 partner_insurers

```sql
CREATE TABLE partner_insurers (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  partner_insurer_code VARCHAR(50) NOT NULL UNIQUE,
  legal_name VARCHAR(255) NOT NULL,
  tax_identification_number VARCHAR(50) NOT NULL,
  logo_url TEXT,
  address JSONB,
  status VARCHAR(20) NOT NULL DEFAULT 'ONBOARDING',
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted_at TIMESTAMP WITH TIME ZONE,
  deleted_by UUID
);

```

### 4.2 partner_insurer_contacts

```sql
CREATE TABLE partner_insurer_contacts (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  partner_insurer_id UUID NOT NULL REFERENCES partner_insurers(id) ON DELETE CASCADE,
  full_name VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  phone VARCHAR(50) NOT NULL,
  contact_role VARCHAR(100) NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted_at TIMESTAMP WITH TIME ZONE,
  deleted_by UUID
);

```

### 4.3 broker_partner_insurer_agreements

```sql
CREATE TABLE broker_partner_insurer_agreements (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  partner_insurer_id UUID NOT NULL REFERENCES partner_insurers(id) ON DELETE CASCADE,
  agreement_code VARCHAR(100) NOT NULL UNIQUE,
  agreement_title VARCHAR(255) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE,
  status agreement_status NOT NULL DEFAULT 'DRAFT',
  covered_branches JSONB NOT NULL,
  commission_rate DECIMAL(5, 4),
  payment_frequency payment_frequency NOT NULL DEFAULT 'MONTHLY',
  payment_method JSONB NOT NULL,
  payment_delay_days INTEGER NOT NULL,
  late_payment_penalty_rate DECIMAL(5, 4),
  document_ref_url TEXT,
  signed_at DATE NOT NULL,
  created_at TIMESTAMP WITH TIME ZONE NOT NULL,
  updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
  deleted_at TIMESTAMP WITH TIME ZONE,
  deleted_by UUID
);

```

### 4.4 outbox

Utilisée pour la publication d’événements RabbitMQ transactionnels.

---

## 5. Communications externes

### 5.1 REST API (extraits)

| Méthode | URI | Description |
| --- | --- | --- |
| POST | `/partner-insurers` | Créer un partenaire |
| GET | `/partner-insurers` | Rechercher partenaires |
| GET | `/partner-insurers/{id}` | Lire un partenaire |
| PATCH | `/partner-insurers/{id}` | Modifier partenaire |
| PATCH | `/partner-insurers/{id}/status` | Modifier statut |
| POST | `/partner-insurers/{id}/contacts` | Ajouter un contact |
| GET | `/partner-insurers/{id}/contacts` | Lister contacts |
| POST | `/partner-insurers/{id}/agreements` | Créer un accord |
| GET | `/partner-insurers/{id}/agreements` | Lister accords |
| PATCH | `/agreements/{id}` | Modifier un accord |
| PATCH | `/agreements/{id}/status` | Modifier statut accord |

**Authentification :**

JWT Bearer token validé par `identity-service` (scopes : `partner:read`, `partner:write`).

---

### 5.2 Événements RabbitMQ

**Exchange :** `bamboo.partner.events`

**Type :** `topic`

| Routing Key | Event | Description |
| --- | --- | --- |
| `partner.created` | `PartnerInsurerCreated` | Création d’un assureur |
| `partner.status.changed` | `PartnerInsurerStatusChanged` | Changement de statut |
| `partner.agreement.created` | `PartnerAgreementCreated` | Création d’accord |
| `partner.agreement.expired` | `PartnerAgreementExpired` | Fin d’accord |

---

## 6. Règles métier

1. `partner_insurer_code` et `agreement_code` sont uniques et immuables.
2. Un partenaire ne peut être **activé** (`ACTIVE`) que si au moins un **accord actif** existe.
3. Tout accord sans `end_date` est considéré comme actif indéfiniment.
4. La suppression physique des partenaires, contacts ou accords est interdite (soft delete).
5. Chaque modification crée un **event Outbox** + une **entrée d’audit**.
6. `payment_method` JSON doit suivre le format documenté dans `payment_method_examples.json`.

---

## 7. Stack et contraintes techniques

| Domaine | Détail |
| --- | --- |
| Langage | Kotlin 2.x |
| Framework | Spring Boot WebFlux + Coroutines |
| Base de données | PostgreSQL (R2DBC) |
| Migration | Flyway |
| Messaging | RabbitMQ + Transactional Outbox pattern |
| Sécurité | JWT via `identity-service`/`user-service` |
| Observabilité | Micrometer, Prometheus (à définir) |
| Tests | JUnit 5 + Testcontainers |
| CI/CD | GitHub Actions + Docker |

---

## 8. Livrables attendus

1. Code Kotlin structuré [***Architecture Explicite***](https://herbertograca.com/2017/11/16/explicit-architecture-01-ddd-hexagonal-onion-clean-cqrs-how-i-put-it-all-together/)
    
    [DDD, Hexagonal, Onion, Clean, CQRS, … How I put it all together](https://herbertograca.com/2017/11/16/explicit-architecture-01-ddd-hexagonal-onion-clean-cqrs-how-i-put-it-all-together/)
    
    <aside>
    💡
    
    Cette architecture place le cœur applicatif/domaine au centre, isolé des mécanismes techniques (UI, base de données, services externes) via des ports et adaptateurs — ce qui permet de changer l’infrastructure sans impacter la logique métier.
    Elle organise le code en couches concentriques (Onion/Clean), en veillant à ce que les dépendances pointent toujours vers l’intérieur (du plus spécifique au générique), renforçant cohésion et testabilité.
    Elle adopte le principe de séparation des commandes et des requêtes (CQRS) ainsi que celui du « modèle centré domaine » (DDD) pour structurer clairement les use-cases, entités et services métier, en évitant que le domaine ne soit pollué par les détails de l’infrastructure
    
    </aside>
    
2. Scripts Flyway :
    - `V1__Create_partner_insurer_schema.sql`
    - `V1.1__Create_outbox_table.sql`
3. Documentation OpenAPI 3.0.

---

## Résumé

`partner-insurer-core-service` :

- est **purement métier**,
- ne gère ni authentification ni connecteurs techniques,
- est conçu pour être le **noyau DDD** du domaine des partenaires assureurs.
