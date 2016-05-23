package mapGeneration;

import graphGeneration.generation.GenGraphs;
import mapGeneration.html.HTMLBuildMap;
import mapGeneration.html.HTMLBuildMapImpl;
import mapGeneration.layout.FruchtermanReingold;
import mapGeneration.layout.LayoutAlgorithm;


/**
 * Apply layout algorithm on graphs and generate HTML view of maps
 * @author Loann Neveu
 */
public class GenMaps {

    public static void execute(String output_directory, String resources_directory) {
        // apply algo
        LayoutAlgorithm algorithm = new FruchtermanReingold();
        algorithm.apply(GenGraphs.getSaisonGraph(), 200);
        algorithm.apply(GenGraphs.getAuteurGraph(), 150);
        algorithm.apply(GenGraphs.getKeywordsGraph(), 150);


        // generate html view
        HTMLBuildMap htmlBuilder = new HTMLBuildMapImpl(output_directory, resources_directory);
        htmlBuilder.create(GenGraphs.getSaisonGraph(), "saisons.html");
        htmlBuilder.create(GenGraphs.getAuteurGraph(), "auteurs.html");
        htmlBuilder.create(GenGraphs.getKeywordsGraph(), "keywords.html");
    }
}
