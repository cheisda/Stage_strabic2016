# Strabic

Librairies externes :
- jsoup-1.8.3.jar
- blueprints-core-2.2.0.jar
- pdfbox-1.8.8.jar
- commons-logging-1.2.jar
- fontbox-1.8.2.jar
- org.ow2.sat4j.core-2.3.5.jar
- sqlite-jdbc-3.8.7.jar
- commons-lang-2.6.jar
- commons-cli-1.3.1.jar
- guava-18.0.jar (because of JDK < 1.7)


## Usage
```
 -a,--articles <arg>            output articles directory (default: 'data/articles/')
 -bua,--baseurlarticles <arg>   articles baseurl (default: 'data/articles/')
 -bui,--baseurlimages <arg>     thumbnails baseurl (default: 'data/img/')
 -d,--db <arg>                  database to use (required)
 -h,--help                      show help
 -m,--maps <arg>                output maps directory (default: 'data/maps/')
 -r,--resources <arg>           resources directory (must contains epilogue.html and prologue.html) (default: 'resources/maps/')
 -s,--seasons <arg>             seasons url file directory (default: 'data/tmp/')
 ```

## Master
(point d'entrée du programme)

- Crée les graphs
- Génère les maps (format HTML)
- Génère les articles (format HTML)

## Génération des graphs
(package 'graphGeneration')

- Récupération des articles via la base de données
- Analyse conceptuelle des articles
- Création des graphs
- Génération des fichiers graphml
- Génération des fichiers txt contenant le "corps" des articles (facultatif)

## Génération des maps
(package 'mapGeneration')

- Application de l'algorithme de placement sur chaque graph
- Génération des maps (HTML) à partir de chaque graph (rép. destination: data/maps)

## Génération des articles
(package 'articleGeneration')

- Génération des articles (HTML) à partir de chaque articles de la base (rép. destination data/articles)



