package master;

import articleGeneration.GenArticles;
import graphGeneration.generation.GenGraphs;
import mapGeneration.GenMaps;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;

/**
 * MASTER CLASS - Entry point of the program
 */
public class Master {

    //private static final String DEFAULT_OUTPUT_MAPS_DIRECTORY = " ";
    //Pour les tests en local
    private static final String DEFAULT_OUTPUT_MAPS_DIRECTORY = "data/maps/";
    private static final String DEFAULT_OUTPUT_ARTICLES_DIRECTORY = "data/articles/";
    private static final String DEFAULT_SEASONS_DIRECTORY = "data/tmp/";
    private static final String DEFAULT_RESOURCES_DIRECTORY = "resources/maps/";
    //NonCheisda_Code : private static final String DEFAULT_THUMBS_BASEURL = "data/img/";
    private static final String DEFAULT_THUMBS_BASEURL = "maps/thumbs";
    //me :private static final String DEFAULT_THUMBS_BASEURL = "data/maps/thumbs/";
   // le vrai private static final String DEFAULT_ARTICLES_BASEURL = "data/articles/";
    //ancienne lingne de code : private static final String DEFAULT_ARTICLES_BASEURL = "data/articles/";
    private static final String DEFAULT_ARTICLES_BASEURL = "/articles/";


    public static void main(String[] args) throws IOException {

        // create the command line parser
        CommandLineParser parser = new DefaultParser();

        // create the Options
        Options options = new Options();
        options.addOption( "d", "db", true, "database to use (required)");
        options.addOption( "m", "maps", true, "output maps directory (default: '" + DEFAULT_OUTPUT_MAPS_DIRECTORY + "')");
        options.addOption( "a", "articles", true, "output articles directory (default: '" + DEFAULT_OUTPUT_ARTICLES_DIRECTORY + "')");
        options.addOption( "s", "seasons", true, "seasons url file directory (default: '" + DEFAULT_SEASONS_DIRECTORY + "')");
        options.addOption( "r", "resources", true, "resources directory (must contains epilogue.html and prologue.html) (default: '" + DEFAULT_RESOURCES_DIRECTORY + "')");
        options.addOption( "bui", "baseurlimages", true, "thumbnails baseurl (default: '" + DEFAULT_THUMBS_BASEURL + "')");
        options.addOption( "bua", "baseurlarticles", true, "articles baseurl (default: '" + DEFAULT_ARTICLES_BASEURL + "')");



        options.addOption( "h", "help", false, "show help");

        // automatically generate the help statement
        HelpFormatter formatter = new HelpFormatter();

        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );

            if(line.hasOption("h"))
            {
                formatter.printHelp( "Strabic Maps & Articles Generator", options );
            }
            else
            {
                String db_path = line.getOptionValue("d");
                if(db_path== null)
                {
                    System.out.println( "A database is required!\nPlease, use -d <db_path>");
                    return;
                }

                String output_maps_directory = ensureTrailingSlash(line.getOptionValue("m", DEFAULT_OUTPUT_MAPS_DIRECTORY));
                String output_articles_directory = ensureTrailingSlash(line.getOptionValue("a", DEFAULT_OUTPUT_ARTICLES_DIRECTORY));
                String seasons_file_directory = ensureTrailingSlash(line.getOptionValue("s", DEFAULT_SEASONS_DIRECTORY));
                String resources_directory = ensureTrailingSlash(line.getOptionValue("r", DEFAULT_RESOURCES_DIRECTORY));
                String thumbs_baseurl = ensureTrailingSlash(line.getOptionValue("bui", DEFAULT_THUMBS_BASEURL));
                String articles_baseurl = ensureTrailingSlash(line.getOptionValue("bua", DEFAULT_ARTICLES_BASEURL));


                // generate graphs (saisons, auteurs, keyword)
                GenGraphs.execute(db_path, seasons_file_directory, articles_baseurl, thumbs_baseurl, false); // false = don't create graphml files


                // apply algorithm layout and generate HTML file for each graph
                GenMaps.execute(output_maps_directory, resources_directory);

                // generate HTML file for earch article
                GenArticles.execute(output_articles_directory);

                // frame test
                //new UITest(GenGraphs.getSaisonGraph());
            }
        }
        catch( ParseException exp ) {
            System.out.println( "Unexpected exception:" + exp.getMessage() );
        }
    }

    /**
     * Ensure trailing slash in path
     * @param path
     * @return path with trailing slash
     */
    private static String ensureTrailingSlash(String path)
    {
        if(path.charAt(path.length()-1)!= File.separatorChar) path += File.separator;
        return path;
    }

}
