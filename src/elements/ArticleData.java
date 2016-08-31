package elements;

/**
 * Class representing article data and attached to a vertex)
 */
public class ArticleData {

    private String name;
    private String title;
    private String author;
    private String url_article;
    private String url_season;
    private String thumbnail;
    private String filename;


    public ArticleData(String name, String title, String author, String url_article, String url_season, String thumbnail, String filename) {
        this.name = name;
        this.title = title;
        this.author = author;
        this.url_article = url_article;
        this.url_season = url_season;
        this.thumbnail = thumbnail;
        this.filename = filename;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getUrl_article() {
        return url_article;
    }

    public String getUrl_season() {
        return url_season;
    }

    public String getThumbnail() { return  thumbnail; }

    public String getFilename() {
        return filename;
    }

    @Override
    public String toString() {
        return "ArticleData{" +
                "name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", url_article='" + url_article + '\'' +
                ", url_season='" + url_season + '\'' +
               // ", thumbnail='" + thumbnail + '\'' +
                ", thumbnail='" + thumbnail + '/' +
                ", filename='" + filename + '\'' +
                '}';
    }
}
