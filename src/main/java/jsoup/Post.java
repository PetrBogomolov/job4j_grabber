package jsoup;

import java.util.Date;
import java.util.Objects;

public class Post {

    private String name;
    private String text;
    private String link;
    private Date created;

    public Post() {
    }

    public Post(String name, String text, String link, Date created) {
        this.name = name;
        this.text = text;
        this.link = link;
        this.created = created;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
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
        return Objects.equals(name, post.name)
                && Objects.equals(text, post.text)
                && Objects.equals(link, post.link)
                && Objects.equals(created, post.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, text, link, created);
    }

    @Override
    public String toString() {
        return "Post{"
                + "name='" + name
                + ", text='" + text
                + ", link='" + link
                + ", created=" + created
                + '}';
    }
}
