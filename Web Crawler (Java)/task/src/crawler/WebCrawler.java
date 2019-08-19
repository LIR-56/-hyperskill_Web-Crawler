package crawler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.concurrent.atomic.AtomicReference;


public class WebCrawler extends JFrame {


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
            final String url = tf_URL.getText();
            CrawlerConnectionHelper connectionHelper = new CrawlerConnectionHelper(url);
            //final String siteText = connectionHelper.getHTMLCode();
            final String title = connectionHelper.getTitle();
            if (title != null) {
                l_Tittle.setText(title);
            }

            Object[][] crawlResults = connectionHelper.getURLsAndTitles();
            while (tModel.get().getRowCount() > 0) {
                tModel.get().removeRow(0);
            }

            for (Object[] crawlResult : crawlResults) {
                tModel.get().addRow(crawlResult);
            }

            SwingUtilities.updateComponentTreeUI(WebCrawler.this);
            System.out.println("processing finished");
        });


        add(tf_URL);
        add(l_Tittle);
        add(b_download);
        setLayout(null);
        setVisible(true);
    }






}

