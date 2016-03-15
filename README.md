# AutomateEtatFini
JAVA automates à états finis, analyse lexical

Mode d'emploi

commande
  Se positionner dans le dossier Automate_1.1
  java -jar ./dist/Automate_1.1

opérations
  -d : applique l'algorithme de détermination d'un automate
  
  -c : complète l'automate

entrées
  -f : prend le nom du fichier .descr pour construire l'automate
  
  -r : prend une expression rationnelle pour construire l'automate
les opérateurs pris en charges sont le ET '.' le OU '|' et '*'
mettre la chaîne de caractères entre quottes ou guillemets pour que l'interpréteur
bash n'interprète pas ces caractères spéciaux

sorties
  -e : exporte l'automate générer au format .descr dans le dossier inpout-outpout-
  files/descr une fois toutes les opérations effectuées
  options
  
  -l : pour lire des mots à partir de l'automate générer à partir d'un fichier .descr
  exemples
    java -jar ./dist/Automate_1.1 -d -c -e -r 'a.b|(a.b).c*'
    java -jar ./dist/Automate_1.1 -l -f FICHIER.descrRemarques Importantes

Un paquet doit être installé ( ImageMagik ou GraphicsMagik ) pour utiliser la
commande convert qui créé les fichiers .gif associé à la lecture d'un mot.
Cette commande est utilisé lors de l'appel du script make_gif.sh pour crée un gif
animé associé à la lecture d'un mot.

Le répertoire /inpout-outpout-files et son arborescence doit être présente à l'endroit
de l’exécution, les chemins sont préciser en variables globales de l'objet AEF.java
La syntaxe à respecter pour construire les expressions rationnelles doit toujours
comporter l'opérateur de concaténation ( par soucis de simplicité du code ) .
Les différents opérateur pris en charge sont :
• concaténation : '.'
• alternative : '|'
• fermeture : '*'

Un gros bug remarqué si l'on tente de lire des mots à partir de l'automate
déterminé et complété construit à partir d'une expression rationnelle :
java -jar ./dist/Automate_1.1 -l -d -c -r 'RegEx'
le parcours ne se fait pas correctement, des transitions incohérentes apparaissent
alors que si
l'on exporte l'automate au format .descr
et que l'on tente ensuite de le lire à partir de ce fichier
java -jar ./dist/Automate_1.1 -e -d -c -r 'RegEx'
java -jar ./dist/Automate_1.1 -l -f MonFichierCree.descr
alors la lecture s’exécute correctement
Je n'ai pas encore trouvé d'explication...
