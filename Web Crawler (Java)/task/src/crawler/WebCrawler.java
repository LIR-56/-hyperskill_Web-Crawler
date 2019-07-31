package crawler;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebCrawler extends JFrame {
    private static final String LINE_SEPARATOR = "\n";

    public WebCrawler() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 1000);
        setTitle("Simple Window");


        JTextArea ta_htmlCode = new JTextArea();
        ta_htmlCode.setBounds(20, 80, 525, 500);
        ta_htmlCode.setName("HtmlTextArea");
        ta_htmlCode.setEnabled(false);


        JTextField tf_URL = new JTextField();
        tf_URL.setBounds(20,20,400,40);
        tf_URL.setName("UrlTextField");

        JButton b_download = new JButton("Get text!");
        b_download.setBounds(425,20,100,40);
        b_download.setName("RunButton");
        b_download.addActionListener(actionEvent -> {
            try {
                final String url = tf_URL.getText();

                final InputStream inputStream = new URL(url).openStream();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                final StringBuilder stringBuilder = new StringBuilder();

                String nextLine;
                while ((nextLine = reader.readLine()) != null) {
                    stringBuilder.append(nextLine);
                    stringBuilder.append(LINE_SEPARATOR);
                }

                final String siteText = stringBuilder.toString();
                ta_htmlCode.setText(siteText);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        add(ta_htmlCode);
        add(tf_URL);
        add(b_download);
        setLayout(null);
        setVisible(true);
    }
}

