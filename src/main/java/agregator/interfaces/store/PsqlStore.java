package agregator.interfaces.store;

import agregator.model.Post;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PsqlStore implements Store, AutoCloseable {

    private Connection cnn;

    public PsqlStore() {
        Properties cfg = getConfig();
        try {
            Class.forName(cfg.getProperty("driver"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            cnn = DriverManager.getConnection(
                    cfg.getProperty("url"),
                    cfg.getProperty("login"),
                    cfg.getProperty("password")
            );
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    private Properties getConfig() {
        Properties cfg = new Properties();
        ClassLoader loader = PsqlStore.class.getClassLoader();
        try {
            cfg.load(loader.getResourceAsStream("agregator.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cfg;
    }

    @Override
    public void save(Post post) {
        if (checkForUniquenessPost(post.getLink())) {
            try (PreparedStatement statement =
                         cnn.prepareStatement(
                                 "INSERT INTO post (name, text, link, created) VALUES (?, ?, ?, ?)")
            ) {
                statement.setString(1, post.getName());
                statement.setString(2, post.getText());
                statement.setString(3, post.getLink());
                statement.setTimestamp(4, new Timestamp(post.getCreated().getTime()));
                statement.executeUpdate();
                try (ResultSet generatedKey = statement.getGeneratedKeys()) {
                    while (generatedKey.next()) {
                        post.setId(generatedKey.getInt(1));
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    private boolean checkForUniquenessPost(String link) {
        Post post = null;
        try (PreparedStatement statement =
                     cnn.prepareStatement("SELECT * FROM post WHERE link = ?")) {
            statement.setString(1, link);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    post = new Post(
                            resultSet.getInt("id"),
                            resultSet.getString("name"),
                            resultSet.getString("text"),
                            resultSet.getString("link"),
                            resultSet.getDate("created")
                    );
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return post == null;
    }

    @Override
    public List<Post> getAll() {
        List<Post> posts = new ArrayList<>();
        try (PreparedStatement statement =
                cnn.prepareStatement("SELECT * FROM post")) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    posts.add(
                            new Post(
                                    resultSet.getInt("id"),
                                    resultSet.getString("name"),
                                    resultSet.getString("text"),
                                    resultSet.getString("link"),
                                    resultSet.getDate("created")
                            )
                    );
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return posts;
    }

    @Override
    public Post findById(String id) {
        Post post = new Post();
        try (PreparedStatement statement =
                     cnn.prepareStatement("SELECT * FROM post WHERE id = ?")) {
            statement.setInt(1, Integer.parseInt(id));
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    post = new Post(
                                    resultSet.getInt("id"),
                                    resultSet.getString("name"),
                                    resultSet.getString("text"),
                                    resultSet.getString("link"),
                                    resultSet.getDate("created")
                    );
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return post;
    }

    @Override
    public void close() throws Exception {
        if (cnn != null) {
            cnn.close();
        }
    }
}
