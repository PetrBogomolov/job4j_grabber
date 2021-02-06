package agregator.interfaces.parse;

import agregator.model.Post;
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

public class SqlRuParse implements Parse {
    private final static String YESTERDAY = "вчера";
    private final static String TODAY = "сегодня";
    private static final SimpleDateFormat PATTERN_OF_DATA = new SimpleDateFormat("d MMM yy");
    private static final SimpleDateFormat PATTERN_OF_DATA_FOR_CONVERT =
            new SimpleDateFormat("d MMM yy, HH:mm", Locale.ENGLISH);
    private static final Pattern DATE_PATTERN = Pattern.compile(".*, \\d{2}:\\d{2}");
    private static final Map<String, String> MONTHS = Map.ofEntries(
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
    private static final List<String> SKIP_LINKS = List.of(
            "https://www.sql.ru/forum/485068/soobshheniya-ot-moderatorov-zdes-vy-mozhete-uznat-prichiny-udaleniya-topikov",
            "https://www.sql.ru/forum/484798/pravila-foruma",
            "https://www.sql.ru/forum/1196621/shpargalki"
    );

    /*
        Метод преобразует String формат даты к Date.
        Для этого функция меняет названия месяцев с русских на английские,
        затем производит смену формата
     */
    public static Date convertStringToDate(String date) throws ParseException {
        Date result = new Date();
        for (String month : MONTHS.keySet()) {
            if (date.contains(month)) {
                date = date.replace(month, MONTHS.get(month));
                result = PATTERN_OF_DATA_FOR_CONVERT.parse(date);
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
        elementOfDate[0] = PATTERN_OF_DATA.format(calendar.getTime()).replace(".", "");
        return String.format("%s,%s", elementOfDate[0], elementOfDate[1]);
    }

    /*
        Метод меняет запись вчерашней даты с прописного формата на числовой
     */
    private static String formatYesterdayDate(String yesterday) {
        String[] elementOfDate = yesterday.split(",");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        elementOfDate[0] = PATTERN_OF_DATA.format(calendar.getTime()).replace(".", "");
        return String.format("%s,%s", elementOfDate[0], elementOfDate[1]);
    }

    /*
        Метод записывает все ссылки со страницы, которую мы парсим в сет
     */
    public static List<String> getLinkPosts(Document doc) {
        List<String> allLinks = new ArrayList<>();
        Elements row = doc.select(".postslisttopic");
        for (Element td : row) {
            Element href = td.child(0);
            if (!SKIP_LINKS.contains(href.attr("href"))) {
                allLinks.add(href.attr("href"));
            }
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
        String name = ad.select(".messageHeader").get(0).text();
        String text = ad.select(".msgBody").get(1).text();
        Date date = null;
        Matcher matcher = DATE_PATTERN.matcher(ad.select(".msgFooter").get(0).text());
        if (matcher.find()) {
            try {
                date = convertStringToDate(setSingleStringDateStandard(matcher.group()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return new Post(idPost, name, text, link, date);
    }
}
