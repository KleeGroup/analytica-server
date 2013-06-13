# Projet Analytica développé par Klee Group.

## Client HTML / Javascript 
Ce client utilise l'api JSON de Analytica fournie par le serveur analytica server.
Le fichiers sont dans le répertoire :
``` analytica-server/src/main/java/web ```

### Utilisation des templates côté client.

Afin de pouvoir faire du templating côté client, nous utilisons Handlebars: http://handlebarsjs.com/

Les templates sont dans le dossier 
``` analytica-server/src/main/java/web/app/scripts/template ```

Les templates sont décrits dans les fichiers possésant une extension .handlebars.

Afin de 'compiler' ces templates dans des fonctions javascripts il faut installer node js puis installer Handlebars.
``` npm install handlebars -g ```
Ensuite il faut aller dans le répertoire des template et utilser la commande suivante:
``` handlebars *.handlebars -f templates.js ```
Ceci aura pour effet de générer un fichier javascript contenant l'ensemble de vos templates.
Si le template se nomme templatename.handlebars alors il sera ensuite utilisable via la fonction templatename stockée dans l'objet javascript Handlebars.templates:
``` Handlebars.templates.templatename() ```
Si on veut lui injecter des données JSON: 
``` Handlebars.templates.templatename(jsonData) ```