package crawler;

import javax.swing.*;

public class WebCrawler extends JFrame {
    public WebCrawler() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 300);
        setTitle("Simple Window");


        JTextArea area = new JTextArea("HTML code?");
        area.setBounds(40, 40, 250, 500);
        area.setName("TextArea");
        area.setEnabled(false);
        add(area);


        setLayout(null);
        setVisible(true);
    }
}

