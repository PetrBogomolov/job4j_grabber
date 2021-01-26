package jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class SqlRuParse {
    public static String formatDate(String date) {
        SimpleDateFormat patternOfData = new SimpleDateFormat("d MMM yy");
        if (date.contains("сегодня")) {
            String[] elementOfDate = date.split(",");
            Calendar calendar = new GregorianCalendar();
            elementOfDate[0] = patternOfData.format(calendar.getTime()).replace(".", "");
            date = String.format("%s,%s", elementOfDate[0], elementOfDate[1]);
        }
        if (date.contains("вчера")) {
            String[] elementOfDate = date.split(",");
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar. DAY_OF_MONTH, -1);
            elementOfDate[0] = patternOfData.format(calendar.getTime()).replace(".", "");
            date = String.format("%s,%s", elementOfDate[0], elementOfDate[1]);
        }
        return date;
    }

    public static List<String> selectPage(Document doc, String cssQuerry, int numberPage) throws IOException {
        List<String> result = new ArrayList<>();
        Elements pages = doc.select(cssQuerry);
        for (Element el : pages.select("a")) {
            while (Integer.parseInt(el.text()) <= numberPage) {
                result.add(el.attr("href"));
                break;
            }
        }
        return result;
    }

    public static void showVacancies(Document doc, String cssQuery) {
        Elements row = doc.select(cssQuery);
        for (Element td : row) {
            Element href = td.child(0);
            Element data = td.parent().child(5);
            System.out.println(href.text());
            System.out.printf("%s %s\n", href.attr("href"), formatDate(data.text()));
        }
    }

    public static void main(String[] args) throws IOException {
        String url = "https://www.sql.ru/forum/job-offers";
        String filterPage = ".sort_options";
        String vacancy = ".postslisttopic";
        Document pageOfWebsite = Jsoup.connect(url).get();
        showVacancies(pageOfWebsite, vacancy);
        for (String page : selectPage(pageOfWebsite, filterPage, 5)) {
            url = page;
            pageOfWebsite = Jsoup.connect(url).get();
            showVacancies(pageOfWebsite, ".postslisttopic");
        }
    }
}
