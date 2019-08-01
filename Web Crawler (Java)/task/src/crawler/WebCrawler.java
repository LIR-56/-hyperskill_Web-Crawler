package crawler;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler extends JFrame {
    private static final String LINE_SEPARATOR = "\n";
    private static int PADDING_LEFT = 20;
    private static int PADDING_TOP = 20;
    private static int SIZE_WIDTH = 550;
    private static int SIZE_HEIGHT = 1000;
    private static int TF_WIDTH = 400;
    private static int SINGLE_LINE_ELEMENT_HEIGHT = 40;

    public WebCrawler() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(SIZE_WIDTH, SIZE_HEIGHT);
        setTitle("Simple Window");

        JTextField tf_URL = new JTextField();
        tf_URL.setBounds(PADDING_LEFT, PADDING_TOP, TF_WIDTH, SINGLE_LINE_ELEMENT_HEIGHT);
        tf_URL.setName("UrlTextField");

        JLabel l_Tittle = new JLabel();
        l_Tittle.setBounds(PADDING_LEFT, PADDING_TOP*2 + SINGLE_LINE_ELEMENT_HEIGHT,
                SIZE_WIDTH - PADDING_LEFT*2, SINGLE_LINE_ELEMENT_HEIGHT);
        l_Tittle.setName("TitleLabel");

        JTextArea ta_htmlCode = new JTextArea();
        ta_htmlCode.setBounds(PADDING_LEFT, PADDING_TOP*3 + SINGLE_LINE_ELEMENT_HEIGHT*2, 525, 900);
        ta_htmlCode.setName("HtmlTextArea");
        ta_htmlCode.setEnabled(false);

        JButton b_download = new JButton("Get text!");
        b_download.setBounds(425, PADDING_TOP, 100, SINGLE_LINE_ELEMENT_HEIGHT);
        b_download.setName("RunButton");
        b_download.addActionListener(actionEvent -> {
            try {
                final String siteText = getHTMLCode(tf_URL);
                ta_htmlCode.setText(siteText);
                final String title = getTitle(siteText);
                if (title != null) {
                    l_Tittle.setText(title);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        add(ta_htmlCode);
        add(tf_URL);
        add(l_Tittle);
        add(b_download);
        setLayout(null);
        setVisible(true);
    }

    private String getHTMLCode(JTextField tf_URL) throws IOException {
        final String url = tf_URL.getText();

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

    private String getTitle(String htmlCode) {
        Matcher title = Pattern.compile("<title>.*</title>").matcher(htmlCode);
        if (title.find()) {
            return title.group().replaceAll("<[/]?title>","");
        } else {
            return null;
        }
    }
}

