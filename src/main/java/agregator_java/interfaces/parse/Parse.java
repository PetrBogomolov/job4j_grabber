package agregator_java.interfaces.parse;

import agregator_java.data_model.Post;

import java.util.List;

public interface Parse {

    /*
        Метод загружает список всех постов
     */
    List<Post> list(String link);

    /*
        Метод загружает детали одного поста
     */
    Post detail(String link, int idPost);
}
