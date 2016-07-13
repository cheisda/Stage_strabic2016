package articleGeneration;

import graphGeneration.generation.Article;
import graphGeneration.generation.GenGraphs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Generate HTML articles
 * @author Arno Simon
 */
public class GenArticles {

    public static void execute(String output_directory)
    {
        HTMLBuildArticle build = new HTMLBuildArticleImpl(output_directory);
        List<Article> articles = GenGraphs.getAllEntries().getArticlelist();

        // build html view for each article
        int nbe = 0;
        for(Article a : articles)
        {
            build.create(a);
            nbe++;
        }
        System.out.println(nbe + " articles written");
    }
}
