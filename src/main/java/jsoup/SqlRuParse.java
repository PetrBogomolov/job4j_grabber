package jsoup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Map.entry;

public class SqlRuParse  implements Parse {
    private final static String YESTERDAY = "вчера";
    private final static String TODAY = "сегодня";
    private static final SimpleDateFormat patternOfData = new SimpleDateFormat("d MMM yy");
    private static final SimpleDateFormat patternOfDataForConvert =
            new SimpleDateFormat("d MMM yy, HH:mm", Locale.ENGLISH);
    private static final Pattern DATE_PATTERN = Pattern.compile(".*, \\d{2}:\\d{2}");
    private static final Map<String, String> months = Map.ofEntries(
                    entry("янв", "jun"),
                    entry("фев", "feb"),
                    entry("мар", "mar"),
                    entry("апр", "apr"),
                    entry("май", "may"),
                    entry("июн", "jun"),
                    entry("июл", "jul"),
                    entry("авг", "aug"),
                    entry("сен", "sep"),
                    entry("окт", "oct"),
                    entry("ноя", "nov"),
                    entry("дек", "dec")
    );

    /*
        Метод преобразует String формат даты к Date.
        Для этого функция меняет названия месяцев с русских на английские, затем производит смену формата
     */
    public static Date convertStringToDate(String date) throws ParseException {
        Date result = new Date();
        for (String month : months.keySet()) {
            if (date.contains(month)) {
                date = date.replace(month, months.get(month));
                result = patternOfDataForConvert.parse(date);
            }
        }
        return result;
    }

    /*
        Метод приводит записи всех дат, полученных с сайта, к одному формату "d MMM yy, HH:mm"
     */
    public static String setSingleStringDateStandard(String date) {
        if (date.contains(TODAY)) {
            return formatTodayDate(date);
        }
        if (date.contains(YESTERDAY)) {
          return formatYesterdayDate(date);
        }
        return date;
    }

    /*
        Метод меняет запись сегодняшней даты с прописного формата на числовой
     */
    private static String formatTodayDate(String today) {
        String[] elementOfDate = today.split(",");
        Calendar calendar = new GregorianCalendar();
        elementOfDate[0] = patternOfData.format(calendar.getTime()).replace(".", "");
        return String.format("%s,%s", elementOfDate[0], elementOfDate[1]);
    }

    /*
        Метод меняет запись вчерашней даты с прописного формата на числовой
     */
    private static String formatYesterdayDate(String yesterday) {
        String[] elementOfDate = yesterday.split(",");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar. DAY_OF_MONTH, -1);
        elementOfDate[0] = patternOfData.format(calendar.getTime()).replace(".", "");
        return String.format("%s,%s", elementOfDate[0], elementOfDate[1]);
    }

    public static Set<String> getLinkPosts(Document doc) {
        LinkedHashSet<String> allLinks = new LinkedHashSet<>();
        Elements row = doc.select(".postslisttopic");
        for (Element td : row) {
            Element href = td.child(0);
            allLinks.add(href.attr("href"));
        }
        return allLinks;
    }

    @Override
    public List<Post> list(String link) {
        List<Post> posts = new ArrayList<>();
        Document ad = null;
        int idPost = 1;
        try {
            ad = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String post : getLinkPosts(ad)) {
            posts.add(detail(post, idPost));
            idPost++;
        }
        return posts;
    }

    @Override
    public Post detail(String link, int idPost) {
        Document ad = null;
        try {
            ad = Jsoup.connect(link).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String name = ad.select(".messageHeader").get(1).text();
        String text = ad.select(".msgBody").get(1).text();
        Date date = null;
        Matcher matcher = DATE_PATTERN.matcher(ad.select(".msgFooter").get(1).text());
        if (matcher.find()) {
            try {
                date = convertStringToDate(setSingleStringDateStandard(matcher.group()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return new Post(idPost, name, text, link, date);
    }

    public static void main(String[] args) throws IOException, ParseException {
        SqlRuParse sqlRu = new SqlRuParse();
        for (int numberOfPage = 1; numberOfPage <= 5; numberOfPage++) {
            String url = String.format("https://www.sql.ru/forum/job-offers/%d", numberOfPage);
            sqlRu.list(url).forEach(System.out::println);
        }
    }
}
