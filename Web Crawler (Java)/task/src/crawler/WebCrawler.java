package crawler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.concurrent.atomic.AtomicReference;


public class WebCrawler extends JFrame {


    private static final int PADDING_LEFT = 10;
    private static final int PADDING_TOP = 10;
    private static final int FRAME_SIZE_WIDTH = 900;
    private static final int FRAME_SIZE_HEIGHT = 700;
    private static final int LABEL_WIDTH = 50;
    private static final int BUTTON_WIDTH = 100;
    private static final int TF_PADDING = LABEL_WIDTH + PADDING_LEFT * 2;

    private static final int TF_WIDTH = FRAME_SIZE_WIDTH - LABEL_WIDTH - BUTTON_WIDTH - PADDING_LEFT * 4;
    private static final int BUTTON_PADDING = TF_WIDTH + LABEL_WIDTH + PADDING_LEFT * 3;
    private static final int SINGLE_LINE_ELEMENT_HEIGHT = 40;
    private static final int SCROLL_PANE_HEIGHT = FRAME_SIZE_HEIGHT - 5 * PADDING_TOP - 3 *SINGLE_LINE_ELEMENT_HEIGHT - 30;
    private static final int SCROLL_PANE_WIDTH = FRAME_SIZE_WIDTH - PADDING_LEFT*2;

    //private static final int SINGLE_LINE_ELEMENT_HEIGHT = 40;


    public WebCrawler() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_SIZE_WIDTH, FRAME_SIZE_HEIGHT);
        setTitle("Simple Window");


        JLabel l_URL = new JLabel();
        l_URL.setBounds(PADDING_LEFT,
                PADDING_TOP,
                LABEL_WIDTH, SINGLE_LINE_ELEMENT_HEIGHT);
        l_URL.setText("URL:");

        JTextField tf_URL = new JTextField();
        tf_URL.setBounds(TF_PADDING, PADDING_TOP, TF_WIDTH, SINGLE_LINE_ELEMENT_HEIGHT);
        tf_URL.setName("UrlTextField");


        JLabel l_tittle = new JLabel();
        l_tittle.setBounds(PADDING_LEFT,
                PADDING_TOP,
                LABEL_WIDTH, SINGLE_LINE_ELEMENT_HEIGHT);
        l_tittle.setText("URL:");


        JLabel l_tittleContent = new JLabel();
        l_tittleContent.setBounds(PADDING_LEFT,
                PADDING_TOP * 2 + SINGLE_LINE_ELEMENT_HEIGHT,
                FRAME_SIZE_WIDTH - PADDING_LEFT * 3 - LABEL_WIDTH,
                SINGLE_LINE_ELEMENT_HEIGHT);
        l_tittleContent.setName("TitleLabel");


        Object[] columnsHeader = new String[]{"URL", "Title"};


        final AtomicReference<DefaultTableModel> tModel = new AtomicReference<>(new DefaultTableModel(new Object[][]{}, columnsHeader));

        final JTable table = new JTable(tModel.get());
        table.setName("TitlesTable");
        table.setEnabled(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(PADDING_LEFT,
                PADDING_TOP * 3 + SINGLE_LINE_ELEMENT_HEIGHT * 2,
                SCROLL_PANE_WIDTH,
                SCROLL_PANE_HEIGHT);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);


        //table[0].setFillsViewportHeight(true);


        JButton b_download = new JButton("Parse");
        b_download.setBounds(BUTTON_PADDING,
                PADDING_TOP,
                BUTTON_WIDTH,
                SINGLE_LINE_ELEMENT_HEIGHT);
        b_download.setName("RunButton");


        b_download.addActionListener(actionEvent -> {
            final String url = tf_URL.getText();
            CrawlerConnectionHelper connectionHelper = new CrawlerConnectionHelper(url);
            //final String siteText = connectionHelper.getHTMLCode();
            final String title = connectionHelper.getTitle();
            if (title != null) {
                l_tittleContent.setText(title);
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


        JLabel l_export = new JLabel();
        l_export.setBounds(PADDING_LEFT,
                PADDING_TOP*4 + SINGLE_LINE_ELEMENT_HEIGHT*2 + SCROLL_PANE_HEIGHT,
                LABEL_WIDTH,
                SINGLE_LINE_ELEMENT_HEIGHT);
        l_export.setText("Export:");

        JTextField tf_fileName = new JTextField();
        tf_fileName.setBounds(TF_PADDING,
                PADDING_TOP*4 + SINGLE_LINE_ELEMENT_HEIGHT*2 + SCROLL_PANE_HEIGHT,
                TF_WIDTH,
                SINGLE_LINE_ELEMENT_HEIGHT);
        tf_fileName.setName("ExportUrlTextField");

        JButton b_saveData = new JButton("Save");
        b_saveData.setBounds(BUTTON_PADDING,
                PADDING_TOP*4 + SINGLE_LINE_ELEMENT_HEIGHT*2 + SCROLL_PANE_HEIGHT,
                BUTTON_WIDTH,
                SINGLE_LINE_ELEMENT_HEIGHT);
        b_saveData.setName("ExportButton");
        b_saveData.addActionListener(actionEvent -> {
            DataSaverToFile dstf = new DataSaverToFile(tf_fileName.getText(), tModel.get().getDataVector());
            dstf.save();
        });

        add(l_URL);
        add(tf_URL);
        add(l_tittle);
        add(l_tittleContent);
        add(b_download);
        add(scrollPane);

        add(l_export);
        add(tf_fileName);
        add(b_saveData);


        setLayout(null);
        setVisible(true);
    }


}

