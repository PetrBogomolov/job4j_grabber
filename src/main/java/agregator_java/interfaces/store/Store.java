package agregator_java.interfaces.store;

import agregator_java.data_model.Post;
import java.util.List;

public interface Store {

    /*
     * сохраняет объявление в базе данных
     */
    void save(Post post);

    /*
     * позволяет извлечь все объявления из базы данных
     */
    List<Post> getAll();

    /*
     * позволяет извлечь объявление из базы данных по id
     *
     */
    Post findById(String id);
}
