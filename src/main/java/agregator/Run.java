package agregator;

import agregator.interfaces.parse.SqlRuParse;
import agregator.interfaces.store.PsqlStore;
import agregator.model.Post;
import java.text.ParseException;

public class Run {
    public static void main(String[] args) throws ParseException {
        SqlRuParse parseSqlRu = new SqlRuParse();
        PsqlStore db = new PsqlStore();
        for (int numberOfPage = 2; numberOfPage <= 5; numberOfPage++) {
            String url = String.format("https://www.sql.ru/forum/job-offers/%d", numberOfPage);
            for (Post post : parseSqlRu.list(url)) {
                try {
                    db.save(post);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        db.getAll().forEach(System.out::println);
        System.out.println(db.findById("1"));
    }
}
