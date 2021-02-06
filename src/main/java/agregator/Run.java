package agregator;

import agregator.interfaces.parse.SqlRuParse;

public class Run {
    public static void main(String[] args) {
        SqlRuParse parseSqlRu = new SqlRuParse();
        for (int numberOfPage = 2; numberOfPage <= 5; numberOfPage++) {
            String url = String.format("https://www.sql.ru/forum/job-offers/%d", numberOfPage);
            parseSqlRu.list(url).forEach(System.out::println);
        }
    }
}
