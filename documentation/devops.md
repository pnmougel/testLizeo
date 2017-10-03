# Documentation de l'API 'testLizeo' pour l'équipe DevOps

La procédure d'installation considère une mise en production sur des serveurs Ubuntu 16.04 et devrait être facilement adaptable pour des distribution Linux utilisant le gestionnaire de paquets Debian. L'installation requiert un accès root sur la machine.

Cette procédure de déploiement ne couvre pas :
 * La mise en place d'un proxy http comme apache ou nginx
 * Le monitoring de l'application (redémarage automatique du serveur, surveillance de la consommation mémoire / CPU / espace disque)
 * La gestion des logs
 * La gestion des versions
 * La sécurisation du serveur
 * La mise en place et la configuration d'un cluster de serveurs elasticsearch


## Serveur de l'application

### Création du package depuis les sources 

```bash
apt-get install git sbt openjdk-8-jre
git clone repourl
cd testTechnique
sbt assembly
```

Le fichier app.jar contenant l'application est situé dans le dossier ```target/scala-2.12``` et contient toutes les dépendances nécessaires au fonctionement de l'application.
 
### Execution des tests

```bash
sbt test
```
### Configuration de l'application

Un fichier de configuration ```prod.conf``` pour la production doit être mis en place sur le serveur et être accessible en lecture pour l'utilisateur executant l'application.

#### Example de fichier de configuration en production

```hocon
elasticsearch {
  # Nom du cluster pour le serveur elasticsearch
  # Correspond au champ cluster.name dans le fichier de configuration elasticsearch
  clusterName: "elastic-prod"
  
  # Port tcp pour la communication avec elasticsearch, par default 9300.
  # Correspond au champ tcp.port dans le fichier de configuration elasticsearch
  port: 9300,
  
  # Nom de domaine du serveur elasticsearch 
  serverDomain: "localhost"
}

server {
  # Port utilisé par l'application
  port: 8080
}

mode: "prod"
```

### Lancement de l'application

```bash
java -jar app.jar -Dconfig.file=PATH_TO_CONFIG_FILE
```


## Serveur elasticsearch

### Installation du serveur

```bash
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-5.1.2.deb
dpkg -i elasticsearch-5.1.2.deb
service elasticsearch start
```

### Configuration du serveur elasticsearch

Le fichier de configuration d'elasticsearch est par defaut situé dans le dossier ```/etc/elasticsearch``` et est accessible en lecture/écriture uniquement par un utilisateur ayant les droits root.

Quelques points sur la configuration d'un serveur elasticsearch sont présentée [ici](https://www.elastic.co/guide/en/elasticsearch/reference/5.1/settings.html)

Dans le cas ou l'application est executée sur le même serveur que le serveur elasticsearch la configuration par default est suffisante.


