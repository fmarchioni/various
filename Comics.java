/*
 * REQUIRES JSoup library
 *  
 *  
 */
package comics;

import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.io.File;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 *
 * @author francesco
 */
public class Comics {
private static int SUNDAY=1;
private static String PATH="/home/francesco/utils/peanuts/";

    public static void main(String[] args) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        // Peanuts first strip October 02, 1950
        // Peanuts last strip February 13, 2000
        Date startDate = formatter.parse("1961/10/02");
        Date endDate = formatter.parse("1965/12/31");

        Calendar start = Calendar.getInstance();
        start.setTime(startDate);
        Calendar end = Calendar.getInstance();
        end.setTime(endDate);

        for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {

            try {
 
               downloadURL(date);
            } catch (Exception exc) {
                exc.printStackTrace();
                System.out.println(exc.getMessage() + " - Error with date " + date);

            }

        }

    }

    public static void downloadURL(Date date) throws Exception {

        SimpleDateFormat dt = new SimpleDateFormat("yyyy/MM/dd");
        String dateFinal = dt.format(date);
        int dayOfWeek= getDayOfTheWeek(date);
        String year = dateFinal.substring(0, dateFinal.indexOf("/"));
        
        String suffix = ".gif";

        // Patch per strip della Domenica (formato diverso)
        if (dayOfWeek == SUNDAY || Integer.parseInt(year) > 1951)
        suffix = ".jpg";
 
        Document doc = Jsoup.connect("http://www.gocomics.com/peanuts/" + dateFinal).get();

        // Get all img tags
        Elements img = doc.getElementsByTag("img");

        int counter = 0;

        // Loop through img tags
        for (Element el : img) {
            // Ex tag: http://assets.amuniversal.com/1dea371020f9012ea5cb00163e41dd5b --Alt: Peanuts -- Classstrip
            if (el.attr("alt").equals("Peanuts") && el.attr("class").equals("strip")) {
                downloadImage(el.attr("src"), dateFinal, year, suffix);
            }
        }

    }

    public static void downloadImage(String img, String date, String year, String suffix) throws Exception {
        //System.out.println(year +">>>>>Date "+date);
        File dir = new File(PATH  + year);
        if (!dir.exists()) {
            dir.mkdir();
        }
        date = date.replaceAll("/", "-");
        URL website = new URL(img);
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(PATH + year + "/" + date + suffix);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }

    public static int getDayOfTheWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        return dayOfWeek;
    }
}
