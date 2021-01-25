package jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

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

    public static void main(String[] args) throws IOException {
        Document doc = Jsoup.connect("https://www.sql.ru/forum/job-offers").get();
        Elements row = doc.select(".postslisttopic");
        for (Element td : row) {
            Element href = td.child(0);
            Element data = td.parent().child(5);
            System.out.println(href.text());
            System.out.printf("%s %s\n", href.attr("href"), formatDate(data.text()));
        }
    }
}
