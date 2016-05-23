package articleGeneration;

import graphGeneration.generation.Article;

/**
 * HTML Builder interface
 * @author Arno Simon
 */
public interface HTMLBuildArticle {

    /**
     * Create the HTML file for the given article
     * @param : Article
     */
    public void create(Article article);
}

