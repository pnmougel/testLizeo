# Documentation de l'API 'testLizeo' pour l'équipe FrontEnd

## Formats des données

Les données sont transmises en utilisant le format de données JSON et l'encodage de caractères UTF-8.

## Modèles des données

### Station

```
{
    // Identifiant unique pour une station
    id: String, 
    
    // Identifiant unique pour un vendeur associé à une station
    dealerId: String,
    
    // Code du pays
    countryCode: String,
    
    // Libellé de la station
    stationName: String,
    
    // Latitude de la position de la station
    latitude: Double,
    
    // Longitude de la position de la station
    longitude: Double,
    
    // Addresse
    address: String,
    
    // Code postale
    postalCode: String,
    
    // Ville
    city: String
}
```

### Dealer (Vendeur)
```
{
    // Identifiant unique pour un vendeur
    id: String, 
    
    // Libellé du vendeur
    name: String,
    
    // Identifiant number du type de vendeur
    dealerType: Number
}
```

### Notes
 * Le référentiel géographique pour les valeurs longitude et latitude d'une station n'est pas défini et devra être vérifié avant l'ajout et la modification des données. 
 * Le champ ```dealerType``` n'est pas associé à une autre entité.

## Codes de retours

Tous les endpoints peuvent retourner une réponse avec le code d'erreur 500 en cas de défaillance du serveur. Un message spécifique peut être renvoyés en fonction du niveau de défaillance de l'application.

Les autres cas d'erreurs sont détaillés pour chaque endpoint spécifiquement. 

En cas de succès d'une requête, le code de retour est toujours 200. Les autres code de retour http 2xx ne sont pas utilisés, même pour les endpoints de création d'un document.

## Endpoints 

### Lecture de la liste complète des stations

```GET /stations```

Retourne une objet JSON avec la liste des stations.

#### Format de la réponse :

```
[Station] // Tableau de Station
```

### Recherche d'une station par identifiant

```GET /stations/id/{stationId}```

Retourne la station ayant l'identifiant ```stationId```.  

#### Codes d'erreur :

404 Not Found
L'identifiant {stationId} ne correspond pas à une station existante dans la base de données

### Recherche des stations associées à un vendeur

```GET /dealer/{dealerId}/stations```

Retourne un tableau JSON contenant la liste de toutes les stations associées au vendeur ayant l'identifiant {dealerId}.

#### Paramètres

* {dealerId} dans l'url : Identifiant numérique du vendeur

#### Réponse

```
[Station] // Tableau de Station
```

#### Codes d'erreur :

404 Not Found
L'identifiant {dealerId} ne correspond pas à un vendeur existant dans la base de données

### Recherche des stations associées à un pays

```GET /stations/country?country=[countryCode]```

Retourne un tableau JSON contenant la liste de toutes les stations associées au pays {countryCode}.

```
[Station] // Tableau de Station
```

#### Paramètres

Paramètre de requête :
* {countryCode} : Le code du pays . Par default "FR" si le paramètre n'est pas spécifié dans l'url.

#### Notes

* Le format du code du pays n'est pas vérifié par le serveur. Le serveur renvera une statut 200 et un tableau vide même si le code du pays n'est pas un code ISO à deux caractères valide.
* Le format du code du pays est sensible à la casse.

### Aggrégation des stations par vendeur

```GET /stations/countBy/dealer```

Retourne un objet JSON contenant pour chaque identifiant de vendeur (clé) le nombre de stations qui lui ai associé (valeur)

```
{
    dealerId[Number]: numberOfStation[Number]
}
```

#### Notes

* L'identifiant d'un vendeur qui n'est pas associé à une station ne sera pas retourné par cette requête. 



