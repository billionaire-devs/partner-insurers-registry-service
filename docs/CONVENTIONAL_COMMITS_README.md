# Conventional Commits & Releases 🚀

## Pourquoi ce guide existe 🤔

- **Objectif** Ce guide aligne toute l'équipe sur le flux de release automatisé défini dans
  `.github/workflows/release.yml` et `.releaserc`.
- **Résultat** En suivant ces règles, `semantic-release` et `gradle-semantic-release-plugin` créent automatiquement les
  bons tags, la bonne section de changelog et mettent à jour la version Gradle.

## Anatomie d'un commit ✍️
```
<type>[scope optionnelle]: <sujet>

[corps optionnel]
[footer(s) optionnel(s)]
```
- **Type** Décrit la nature du changement (voir les tableaux plus bas).
- **Scope** (optionnel) Cible la partie impactée (ex. `api`, `docs`).
- **Sujet** Résumé court, à l'impératif, sans point final.
- **Corps** (optionnel) Apporte du contexte ; utile pour les BREAKING CHANGES.
- **Footers** (optionnels) Référencent des tickets ou ajoutent la mention `BREAKING CHANGE:`.

## Branches ➡️ Canaux de release 🌱
| Motif de branche | Canal de release  | Suffixe de tag | Environnement        |
|------------------|-------------------|----------------|----------------------|
| `main`           | Release stable    | *(aucun)*      | Production           |
| `develop`        | Prérelease Beta   | `-beta.<n>`    | Dev                  |
| `release/*`      | Release candidate | `-RC.<n>`      | Staging              |
| `feature/*`      | Prérelease Alpha  | `-alpha.<n>`   | Prévisualisation dev |
| `fix/*`          | Prérelease Bugfix | `-bugfix.<n>`  | Correctifs dev       |
| `hotfix/*`       | Prérelease Patch  | `-patch.<n>`   | Correctifs urgents   |

> Pousser un commit avec un type « releasable » sur l'une de ces branches déclenche le workflow de release et génère les
> tags / entrées de changelog du canal associé.

## Type de commit ➡️ Impact version 🔁
| Type / Règle                          | Description                      | Impact sur la version              |
|---------------------------------------|----------------------------------|------------------------------------|
| `feat`                                | Nouvelle fonctionnalité          | Mineure (ou équivalent prérelease) |
| `fix`                                 | Correction de bug                | Corrective                         |
| `feat!` / `fix!` / `BREAKING CHANGE:` | Changement incompatible          | Majeure                            |
| `chore`, `docs`                       | Par défaut : pas de release      | Aucun                              |
| `docs(README)`                        | Règle spéciale dans `.releaserc` | Corrective                         |
| `refactor`                            | Règle surchargée                 | Corrective                         |
| `style`                               | Règle surchargée                 | Corrective                         |
| `chore(no-release)`                   | Publier inutile                  | Aucun                              |

## Emojis prêts à l'emploi 😀

- **✨ feat:** ✨ add payment webhook
- **🐛 fix:** 🐛 fix(auth): patch token refresh
- **💄 style:** 💄 style: format kotlin data classes
- **♻️ refactor:** ♻️ refactor(api): simplify validation logic
- **📝 docs:** 📝 docs(readme): clarify setup instructions
- **🧹 chore:** 🧹 chore(no-release): update build cache
- **⚠️ breaking:** ⚠️ feat!: deprecate legacy API
- **🚀 release:** 🚀 chore(release): bump version to 1.2.0
- **⚡ perf:** ⚡ perf: optimize database queries
- **🧪 test:** 🧪 test: add integration tests for auth flow
- **🔒 security:** 🔒 fix(security): patch JWT validation
- **📦 build:** 📦 build: upgrade gradle to 8.5
- **👷 ci:** 👷 ci: add code quality workflow

> Les emojis sont facultatifs mais recommandés — placez-les **au début** du sujet pour une meilleure lisibilité.

## Checklist pratique ✅
- **Choisir le type** Sélectionnez le type Conventional Commit adapté au changement.
- **Scope avec parcimonie** Préférez des scopes courts (`api`, `db`, `docs`, `ci`) ou omettez-les si doute.
- **Sujet clair** Gardez ≤ 72 caractères, à l'impératif (ex. « add », « fix »).
- **Emoji au début** Placez l'emoji au début du sujet pour une meilleure visibilité.
- **Signaler les breaking changes** Ajoutez `!` après le type ou un footer `BREAKING CHANGE:`.
- **Sauter une release** Utilisez le scope `no-release` pour les commits de maintenance.

## Avant de merger 🚦
- **Dry run** `npx semantic-release --dry-run` (avec `GH_TOKEN`) pour prévisualiser tags et notes.
- **Vérifier la pipeline** Assurez-vous que `.github/workflows/deploy.yml` et `.github/workflows/release.yml` passent.
- **Vérifier la version Gradle** `semantic-release` met à jour `gradle.properties` automatiquement ; pas d'édition
  manuelle.

## Exemples de commits 💡

### Commits de fonctionnalités

```bash
✨ feat: add partner agreement expiration task
✨ feat(api): implement new payment webhook endpoint
✨ feat!: migrate to new authentication system

BREAKING CHANGE: The old JWT format is no longer supported
```

### Corrections de bugs

```bash
🐛 fix: resolve null pointer in payment processing
🐛 fix(auth): patch token refresh logic
🔒 fix(security): validate user permissions properly
```

### Maintenance et documentation

```bash
📝 docs(README): clarify setup instructions
🧼 style: format kotlin data classes
🔧 refactor(domain): simplify entity validation
🧹 chore(no-release): update dependencies
```

### CI/CD et infrastructure

```bash
👷 ci: add automated code quality checks
📦 build: upgrade to Spring Boot 3.2
🚀 chore(release): prepare for v2.0.0
```

## FAQ 💡

- **Q :** Que se passe-t-il si j'utilise un type inconnu ?
  - **R :** Il est ignoré (« no release »). Suivez le tableau ci-dessus ou étendez `releaseRules` dans `.releaserc`.
- **Q :** Puis-je regrouper plusieurs changements ?
  - **R :** Oui, mais séparez les commits si les types diffèrent pour garder des releases cohérentes.
- **Q :** Comment fonctionnent les tags prérelease ?
    - **R :** Chaque branche a son propre suffixe : `develop` → `-beta.1`, `feature/*` → `-alpha.1`, etc.
- **Q :** Quand utiliser `feat!` vs `BREAKING CHANGE:` ?
    - **R :** `feat!` pour les changements courts, `BREAKING CHANGE:` dans le footer pour plus de détails.
- **Q :** Les emojis sont-ils obligatoires ?
    - **R :** Non, mais recommandés pour améliorer la lisibilité. Placez-les toujours au début du sujet.

## Besoin d'aide ? 🙋
- **Workflows** Consultez `.github/workflows/release.yml` et `.github/workflows/deploy.yml`.
- **Configuration des releases** Référez-vous à `.releaserc` pour les règles par branche / canal.
- **Versionning Gradle** Voir `build.gradle.kts` et `gradle.properties` pour l'intégration semantic-release.
- **Plugin Gradle** Le projet utilise `gradle-semantic-release-plugin` pour la synchronisation des versions.

Bons commits ! 🚀
