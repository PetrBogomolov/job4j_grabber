package jsoup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Post {

    private int id;
    private String name;
    private String text;
    private String link;
    private Date created;
    private static final SimpleDateFormat patternOfDataForConvert =
            new SimpleDateFormat("d MMM yy, HH:mm");

    public Post() {
    }

    public Post(int id, String name, String text, String link, Date created) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.link = link;
        this.created = created;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return id == post.id && Objects.equals(name, post.name)
                && Objects.equals(text, post.text)
                && Objects.equals(link, post.link)
                && Objects.equals(created, post.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, text, link, created);
    }

    @Override
    public String toString() {
        return "Post:" + System.lineSeparator()
                + "id - " + id + System.lineSeparator()
                + "name - " + name + System.lineSeparator()
                + "text - " + text + System.lineSeparator()
                + "link - " + link + System.lineSeparator()
                + "created - " + patternOfDataForConvert.format(created) + System.lineSeparator();
    }
}
