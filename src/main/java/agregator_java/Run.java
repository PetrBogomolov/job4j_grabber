package agregator_java;

import agregator_java.interfaces.parse.SqlRuParse;

public class Run {
    public static void main(String[] args) {
        SqlRuParse sqlRu = new SqlRuParse();
        for (int numberOfPage = 1; numberOfPage <= 5; numberOfPage++) {
            String url = String.format("https://www.sql.ru/forum/job-offers/%d", numberOfPage);
            sqlRu.list(url).forEach(System.out::println);
        }
    }
}
