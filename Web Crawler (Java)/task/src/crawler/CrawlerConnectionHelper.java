package crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CrawlerConnectionHelper {

    private static final String LINE_SEPARATOR = "\n";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0";

    final private String url;
    private String siteText;
    private URLConnection connection;

    CrawlerConnectionHelper(String url) {
        this.url = url;
        try {
            this.connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", USER_AGENT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getHTMLCode() {
        if (connection == null) return null;
        final InputStream inputStream;
        try {
            inputStream = connection.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        final StringBuilder stringBuilder = new StringBuilder();

        String nextLine = null;
        while (true) {
            try {
                if ((nextLine = reader.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            stringBuilder.append(nextLine);
            stringBuilder.append(LINE_SEPARATOR);
        }

        return stringBuilder.toString();
    }

    String getTitle() {
        if (siteText == null) {
            this.siteText = getHTMLCode();
        }
        Matcher title = Pattern.compile("<title>.*</title>").matcher(siteText);
        if (title.find()) {
            return title.group().replaceAll("<[/]?title>", "");
        } else {
            return null;
        }
    }

    Object[][] getURLsAndTitles() {
        Map<String, String> results = new HashMap<>();
        if (siteText == null) {

            this.siteText = getHTMLCode();

        }
        Matcher linkMatcher = Pattern.compile("<a [^>]*href=[\"'][^ \"'>]*[\"']").matcher(siteText);

        //search for links
        while (linkMatcher.find()) {
            String link = linkMatcher.group();
            link = link.substring(link.indexOf("href=") + "href='".length());
            link = link.substring(0, getClosestQuote(link));

            link = makeLinkAbsolute(url, link);

            //add html links with it's title to results
            CrawlerConnectionHelper connectionHelper = new CrawlerConnectionHelper(link);
            if (connectionHelper.isLinkTypeHTML()) {
                results.put(link, connectionHelper.getTitle());
            }
            System.out.println(results.size() + ", last link processed: " + link);
        }

        //transform result from Map to Object[][]
        Object[][] URLToTitles = new String[results.size()][];
        int i = 0;

        for (String link : results.keySet()) {
            URLToTitles[i] = new String[]{link, results.get(link)};
            i++;
        }

        return URLToTitles;
    }

    private int getClosestQuote(String link) {
        int indexOfSingleQuote = link.indexOf("'");
        int indexOfDoubleQuote = link.indexOf("\"");
        if (indexOfSingleQuote != -1 && indexOfSingleQuote < indexOfDoubleQuote) {
            return indexOfSingleQuote;
        } else if (indexOfDoubleQuote != -1) {
            return indexOfDoubleQuote;
        } else return indexOfSingleQuote;
    }

    private String makeLinkAbsolute(String path, String link) {
        if (!isLinkAbsolute(link)) {
            if (isLinkRelative(link)) {
                link = addFullPathToLink(link, path);
            } else {
                link = addProtocolToLink(link, path);
            }
        }
        return link;
    }


    private boolean isLinkTypeHTML() {
        if (connection == null) return false;

        String connectionType = connection.getContentType();
        if (connectionType != null) {
            return connectionType.contains("text/html");
        }
        return false;

    }

    private String addFullPathToLink(String link, String path) {
        return path.substring(0, path.lastIndexOf("/") + 1) + link;
    }

    private String addProtocolToLink(String link, String path) {
        String result = link;

        if (!link.startsWith("/")) {
            result = "//" + result;
        }
        if (link.startsWith("/") && !link.startsWith("//")) {
            result = path.substring(0, path.indexOf("/", path.indexOf("//")) - 1) + link;
        }
        if (path.startsWith("https")) {
            result = "https:" + result;
        } else {
            result = "http:" + result;
        }
        return result;
    }

    private boolean isLinkRelative(String link) {
        return !link.contains("/");
    }

    private boolean isLinkAbsolute(String link) {
        return link.startsWith("https://") || link.startsWith("http://");
    }
}
