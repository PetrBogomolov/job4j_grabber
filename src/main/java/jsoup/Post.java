package jsoup;

import java.util.Date;
import java.util.Objects;

public class Post {

    private String nameAd;
    private String textAd;
    private String author;
    private int answers;
    private int views;
    private Date date;

    public Post() {
    }

    public Post(String nameAd, String textAd, String author, Date date) {
        this.nameAd = nameAd;
        this.textAd = textAd;
        this.author = author;
        this.date = date;
    }

    public String getNameAd() {
        return nameAd;
    }

    public String getTextAd() {
        return textAd;
    }

    public String getAuthor() {
        return author;
    }

    public int getAnswers() {
        return answers;
    }

    public int getViews() {
        return views;
    }

    public Date getDate() {
        return date;
    }

    public void setNameAd(String nameAd) {
        this.nameAd = nameAd;
    }

    public void setTextAd(String textAd) {
        this.textAd = textAd;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setAnswers(int answers) {
        this.answers = answers;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Post post = (Post) o;
        return Objects.equals(nameAd, post.nameAd) && Objects.equals(textAd, post.textAd);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameAd, textAd);
    }

    @Override
    public String toString() {
        return "Post{"
               + "nameAd='" + nameAd
               + ", textAd='" + textAd
               + ", author='" + author
               + ", answers=" + answers
               + ", views=" + views
               + ", date=" + date
               + '}';
    }

    public static void main(String[] args) {
        Post post = new Post("new post", "job", "Petr", new Date());
        System.out.println(post);
    }
}
