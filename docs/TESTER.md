# Tester le TP avant GitHub

La vérification locale a réussi : `mvn -B clean verify` a exécuté 17 tests sans échec ni erreur. Les essais HTTP et les captures Swagger ont été réalisés avec MySQL 8.0.44 sur une instance isolée au port 3307. Les instructions suivantes permettent de reproduire ces vérifications avec ta configuration.

Sur la machine de vérification, Maven utilisait initialement Java 11. Le JDK 17 a été sélectionné pour cette exécution. Un problème de certificat Maven a été résolu en utilisant le magasin de certificats Windows, sans désactiver la vérification TLS :

```powershell
$env:JAVA_HOME = 'C:/Program Files/Java/jdk-17'
$env:Path = "$env:JAVA_HOME/bin;$env:Path"
$env:MAVEN_OPTS = '-Djavax.net.ssl.trustStoreType=Windows-ROOT -Djavax.net.ssl.trustStore=NONE'
```

## 1. Préparer Java, Maven et MySQL

À la racine du projet, dans PowerShell :

```powershell
java -version
mvn -version
```

Vérifier que Maven utilise un JDK 17 ou supérieur. Dans IntelliJ IDEA, sélectionner ce même JDK dans Project SDK et dans les paramètres Maven, puis recharger le `pom.xml`.

Dans MySQL Workbench ou DBeaver, connecté à ton serveur local :

```sql
CREATE DATABASE IF NOT EXISTS studentdb
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Dans le terminal PowerShell qui servira à lancer l’application, adapter les identifiants :

```powershell
$env:DB_USERNAME = 'root'
$env:DB_PASSWORD = 'ton_mot_de_passe_mysql'
```

Si le mot de passe est vide, utiliser une chaîne vide. Pour un autre port MySQL :

```powershell
$env:DB_URL = 'jdbc:mysql://localhost:3307/studentdb?serverTimezone=UTC'
```

Ne pas recopier le mot de passe dans les fichiers versionnés. La configuration lit les variables d’environnement ; les valeurs par défaut sont `localhost:3306`, `root` et un mot de passe vide.

## 2. Lancer les tests unitaires et créer le JAR

```powershell
mvn clean verify
```

Résultat attendu : `BUILD SUCCESS`, sans erreur ni échec dans les tests. Les trois classes couvrent le contrôleur, les branches du service et les contraintes de validation. Elles utilisent Mockito ou Jakarta Validation sans charger le contexte Spring et sans connexion MySQL. Les rapports sont dans `target/surefire-reports/` et le JAR dans `target/`.

Les tests directs du contrôleur ne vérifient pas le routage HTTP ni l’activation de `@Valid` par Spring MVC : les essais HTTP ci-dessous complètent ces vérifications. Les requêtes JPA et les contraintes d’unicité doivent aussi être vérifiées avec MySQL.

## 3. Démarrer l’application

Dans le terminal où les variables MySQL sont définies :

```powershell
mvn spring-boot:run
```

Attendre `Started StudentManagementApplication`. Ouvrir `http://localhost:8080/swagger-ui.html`. La spécification JSON est disponible sur `http://localhost:8080/v3/api-docs`.

Dans Swagger, sélectionner une route puis **Try it out** et **Execute**. Dans Postman, utiliser l’URL de base `http://localhost:8080` et, pour les POST, **Body → raw → JSON**, avec `Content-Type: application/json`.

## 4. Créer trois étudiants

Effectuer trois requêtes `POST http://localhost:8080/students/save`, sans `id`, avec les corps suivants. Utiliser une base de TP vide pour obtenir les totaux indiqués ; sinon comparer avec le total initial. Si ces données existent déjà, changer les matricules et emails.

```json
{
  "nom": "AMRANI",
  "prenom": "Salma",
  "dateNaissance": "2003-05-17",
  "matricule": "ETU-001",
  "email": "salma.amrani@example.com",
  "filiere": "Informatique",
  "niveau": 3,
  "telephone": "+212612345678"
}
```

```json
{
  "nom": "BENALI",
  "prenom": "Yassine",
  "dateNaissance": "2003-10-22",
  "matricule": "ETU-002",
  "email": "yassine.benali@example.com",
  "filiere": "Informatique",
  "niveau": 2,
  "telephone": "0612345679"
}
```

```json
{
  "nom": "EL IDRISSI",
  "prenom": "Aya",
  "dateNaissance": "2004-02-08",
  "matricule": "ETU-003",
  "email": "aya.elidrissi@example.com",
  "filiere": "Gestion",
  "niveau": 1
}
```

Chaque réponse doit avoir le statut **201** et contenir l’`id` généré. Noter les trois identifiants : ne pas supposer qu’ils valent 1, 2 et 3.

## 5. Vérifier les lectures et statistiques

| Requête GET | Résultat attendu sur la base initialement vide |
| --- | --- |
| `/students/all` | 200, trois étudiants avec les nouveaux champs |
| `/students/count` | 200, `3` |
| `/students/byYear` | 200, `[[2003,2],[2004,1]]` |
| `/students/byFiliere?filiere=informatique` | 200, Salma puis Yassine |
| `/students/byFiliere?filiere=Medecine` | 200, `[]` |
| `/students/ID_SALMA` | 200, Salma ; remplacer `ID_SALMA` par son identifiant |
| `/students/2147483647` | 404, si cet identifiant est absent |

## 6. Vérifier la modification et la persistance

Renvoyer le JSON complet de Salma vers `POST /students/save` en ajoutant son véritable `id` et en remplaçant `niveau` par `4`. Résultat attendu : **200**, même identifiant, niveau modifié. Le total doit rester à 3.

Arrêter avec `Ctrl+C`, puis relancer `mvn spring-boot:run` dans le même terminal. Vérifier que les trois étudiants et le niveau modifié sont toujours présents.

Dans MySQL :

```sql
USE studentdb;
SELECT * FROM students ORDER BY id;
SELECT YEAR(date_naissance) AS annee, COUNT(*) AS nombre
FROM students
GROUP BY YEAR(date_naissance)
ORDER BY annee;
```

Hibernate crée la table `students` et traduit `dateNaissance` en colonne `date_naissance` avec la stratégie de nommage par défaut.

## 7. Vérifier les erreurs

À partir du premier JSON, effectuer les essais séparément :

| Essai POST `/students/save` | Résultat attendu |
| --- | --- |
| Nom vide ou absent | 400, erreur `nom` |
| Email `incorrect` | 400, erreur `email` |
| Niveau `6` ou `0` | 400, erreur `niveau` |
| Date de naissance future | 400, erreur `dateNaissance` |
| Téléphone `abc` | 400, erreur `telephone` |
| Matricule absent | 400, erreur `matricule` |
| Création avec matricule existant et email différent | 409 |
| Création avec email existant et matricule différent | 409 |
| JSON complet valide avec `id: 2147483647` absent | 404 |

Pour isoler chaque erreur, conserver tous les autres champs valides. Vérifier ensuite que `/students/count` renvoie toujours 3.

## 8. Vérifier la suppression

Effectuer `DELETE /students/delete/ID_AYA` avec le véritable identifiant d’Aya : **204**, corps vide. Répéter exactement la même requête : **404**.

Vérifier ensuite : `/students/count` → `2`, `/students/byYear` → `[[2003,2]]`, et `/students/ID_AYA` → **404**.

## 9. Résoudre les erreurs courantes

- `Access denied` : vérifier `DB_USERNAME` et `DB_PASSWORD` dans le terminal de démarrage.
- `Unknown database` : créer `studentdb` sur le serveur utilisé.
- `Communications link failure` : vérifier que MySQL tourne et que son port correspond à l’URL JDBC.
- Port 8080 occupé : définir `$env:SERVER_PORT = '8081'` avant de relancer et adapter les URL.
- Erreur de compilation Java : vérifier le JDK affiché par `mvn -version` et celui configuré dans l’IDE.
- Erreur 409 : choisir un matricule et un email uniques pour une nouvelle création.

## 10. Préparer le dépôt GitHub

1. Terminer les vérifications précédentes et ne déclarer les tests réussis qu’après les avoir exécutés.
2. Les 12 captures sont déjà ajoutées dans `docs/screenshots/` et affichées dans le README, sans légendes. Les deux premières présentent les sorties réelles mises en page dans le navigateur ; les dix suivantes proviennent de Swagger. Pour les remplacer après une nouvelle exécution, conserver les mêmes noms de fichiers.
3. Vérifier que les captures ne montrent pas de mot de passe ou de donnée personnelle réelle.
4. Ne pas ajouter `target/`, `.idea/`, `.env` ou de configuration locale contenant un secret. Ces fichiers sont prévus dans `.gitignore`.
5. Créer un dépôt GitHub vide, sans README initial, puis utiliser les commandes suivantes uniquement une fois le projet vérifié.

Si le dossier n’est pas encore un dépôt Git :

```powershell
git init
```

Préparer et examiner les fichiers :

```powershell
git add .
git status
git diff --cached
```

Après vérification du contenu :

```powershell
git commit -m "Réalisation du TP de gestion des étudiants"
git branch -M main
git remote add origin https://github.com/TON_COMPTE/TON_DEPOT.git
git push -u origin main
```

Remplacer l’URL par celle du dépôt. Si un remote `origin` existe déjà, vérifier `git remote -v` avant de choisir le dépôt de destination. Aucune de ces commandes Git n’a été exécutée pendant la préparation.
