package crawler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler extends JFrame {

    private static final String LINE_SEPARATOR = "\n";
    private static final int PADDING_LEFT = 20;
    private static final int PADDING_TOP = 20;
    private static final int SIZE_WIDTH = 550;
    private static final int SIZE_HEIGHT = 1000;
    private static final int TF_WIDTH = 400;
    private static final int SINGLE_LINE_ELEMENT_HEIGHT = 40;

    public WebCrawler() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(SIZE_WIDTH, SIZE_HEIGHT);
        setTitle("Simple Window");

        JTextField tf_URL = new JTextField();
        tf_URL.setBounds(PADDING_LEFT, PADDING_TOP, TF_WIDTH, SINGLE_LINE_ELEMENT_HEIGHT);
        tf_URL.setName("UrlTextField");

        JLabel l_Tittle = new JLabel();
        l_Tittle.setBounds(PADDING_LEFT, PADDING_TOP * 2 + SINGLE_LINE_ELEMENT_HEIGHT,
                SIZE_WIDTH - PADDING_LEFT * 2, SINGLE_LINE_ELEMENT_HEIGHT);
        l_Tittle.setName("TitleLabel");


        Object[] columnsHeader = new String[]{"URL", "Title"};

        JScrollPane[] scrollPane = new JScrollPane[1];

        final AtomicReference<DefaultTableModel>  tModel= new AtomicReference<>(new DefaultTableModel(new Object[][]{}, columnsHeader));

        final JTable table = new JTable(tModel.get());
        table.setName("TitlesTable");
        table.setEnabled(false);

        scrollPane[0] = new JScrollPane(table);
        scrollPane[0].setBounds(PADDING_LEFT, PADDING_TOP * 3 + SINGLE_LINE_ELEMENT_HEIGHT * 2, 500, 700);
        scrollPane[0].setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane[0].setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane[0]);

        //table[0].setFillsViewportHeight(true);


        JButton b_download = new JButton("Parse!");
        b_download.setBounds(425, PADDING_TOP, 100, SINGLE_LINE_ELEMENT_HEIGHT);
        b_download.setName("RunButton");


        b_download.addActionListener(actionEvent -> {
            try {
                final String url = tf_URL.getText();
                final String siteText = WebCrawler.this.getHTMLCode(url);
                final String title = WebCrawler.this.getTitle(siteText);
                if (title != null) {
                    l_Tittle.setText(title);
                }

                Object[][] crowlResults = getURLsAndTitles(siteText, url);
                while (tModel.get().getRowCount() > 0) {
                    tModel.get().removeRow(0);
                }

                for (Object[] crowlResult : crowlResults) {
                    tModel.get().addRow(crowlResult);
                }

                SwingUtilities.updateComponentTreeUI(WebCrawler.this);
            } catch (IOException e) {
                System.out.println(tf_URL.getText());
                e.printStackTrace();
            }
            System.out.println("processing finished");
        });


        add(tf_URL);
        add(l_Tittle);
        add(b_download);
        setLayout(null);
        setVisible(true);
    }

    private String getHTMLCode(String url) throws IOException {
        //final String url = tf_URL.getText();

        final InputStream inputStream = new URL(url).openStream();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        final StringBuilder stringBuilder = new StringBuilder();

        String nextLine;
        while ((nextLine = reader.readLine()) != null) {
            stringBuilder.append(nextLine);
            stringBuilder.append(LINE_SEPARATOR);
        }

        return stringBuilder.toString();
    }

    private Object[][] getURLsAndTitles(String HTMLCode, String path) {
        Map<String, String> results = new HashMap<>();
        Matcher linkMatcher = Pattern.compile("<a [^>]*href=[\"'][^ \"'>]*[\"']").matcher(HTMLCode);

        //search for links
        while (linkMatcher.find()) {
            String link = linkMatcher.group();
            link = link.substring(link.indexOf("href=") + "href='".length());
            link = link.substring(0, getClosestQuote(link));

            link = makeLinkAbsolute(path, link);

            //add html links with it's title to results
            if (isLinkTypeHTML(link)) {
                results.put(link, getTitleByLink(link));
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

        //URLToTitles[results.size()] = new String[] {"https://localhost:25555/UnavailablePage", "UnavailablePage"};

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

    private String getTitleByLink(String link) {
        try {
            return getTitle(getHTMLCode(link));
        } catch (IOException e) {
            System.out.println("Error in getTitleByLink, link: " + link);
            e.printStackTrace();
        }
        return null;
    }

    private boolean isLinkTypeHTML(String link) {
        try {
            String connectionType = new URL(link).openConnection().getContentType();
            if (connectionType != null) {
                return connectionType.contains("text/html");
            }
        } catch (IOException e) {
            System.out.println("Error in isLinkTypeHTML, link: " + link);
            e.printStackTrace();
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

    private String getTitle(String htmlCode) {
        Matcher title = Pattern.compile("<title>.*</title>").matcher(htmlCode);
        if (title.find()) {
            return title.group().replaceAll("<[/]?title>", "");
        } else {
            return null;
        }
    }
}

