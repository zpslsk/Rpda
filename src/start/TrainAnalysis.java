package start;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.IOException;
import java.util.prefs.Preferences;
import java.awt.event.*;
import java.io.File;
import localmess.FileChooserRu;
import blocks32.ArrBlock32;
import java.nio.file.Paths;
import reports.HexTablePan;
import reports.SecondsInfoPan;

public class TrainAnalysis extends JDialog {

    private Preferences root = Preferences.userRoot();
    private Preferences node = root.node("/avpt/arm/rpda/analysis");
    private FileChooserRu fileChooser;
    private ArrBlock32 arrBlock32;
    private JMenuItem showHexItem;
    private JMenuItem showSecondsInfo;
    private JLabel labelStatus;
    private HexTablePan hexTablePan;
    private SecondsInfoPan secondsInfoPan;
    private Cursor hourglassCursor;
    private Cursor defaultCursor;

    public TrainAnalysis(JFrame owner) {
        super(owner, "Анализ поездки", true);
        hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
        defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        setLayout(new BorderLayout());
        // расположение окна
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int left = node.getInt("left", 0);
        int top = node.getInt("top", 0);
        int width = node.getInt("width", (int)screenSize.getWidth() - (int)screenSize.getWidth() / 25);
        int height = node.getInt("height", (int)screenSize.getHeight() - (int)screenSize.getHeight() / 25);
        setBounds(left, top, width, height);
        // файл поездки
        fileChooser = new FileChooserRu();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Файлы поездок", "img", "dat", "bin"));
        fileChooser.setDialogTitle("Открыть файл поездки");
        // статус-панель
        labelStatus = new JLabel(" ");
        add(labelStatus, BorderLayout.SOUTH);
        labelStatus.setBorder(BorderFactory.createEtchedBorder());
        // обработка закрытия окна
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent event) {
                node.putInt("left", getX());
                node.putInt("top", getY());
                node.putInt("width", getWidth());
                node.putInt("height", getHeight());
            }
        });
        createMenuBar();
    }
    // меню
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        //----------------------------------
        JMenu fileMenu = new JMenu("Файл");
        menuBar.add(fileMenu);
        //-----------------------------------
        JMenuItem openItem = new JMenuItem("Открыть...");
        openItem.addActionListener(new OpenListener());
        showHexItem = new JMenuItem("Таблица-Hex");
        showHexItem.addActionListener(new ShowTableListener());
        showHexItem.setEnabled(false);
        showSecondsInfo = new JMenuItem("Таблица-время");
        showSecondsInfo.addActionListener(new ShowSecondsInfoListener());
        showSecondsInfo.setEnabled(false);
        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                System.exit(0);
            }
        });
        //-----------------------------------
        fileMenu.add(openItem);
        fileMenu.add(showHexItem);
        fileMenu.add(showSecondsInfo);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
    }
    // удалить
    private void free() {
        if (hexTablePan != null) remove(hexTablePan);
        if (secondsInfoPan != null) remove(secondsInfoPan);
    }
    // показать hex поездки
    class ShowTableListener implements  ActionListener {
        public void actionPerformed(ActionEvent event) {
            try {
                setCursor(hourglassCursor);
                if (arrBlock32 == null) return;
                free();
                hexTablePan = new HexTablePan(arrBlock32);
                add(hexTablePan, BorderLayout.CENTER);
                SwingUtilities.updateComponentTreeUI(TrainAnalysis.this);
            }
            finally {
                setCursor(defaultCursor);
            }
        }
    }
    // показать секундную информацию
    class ShowSecondsInfoListener implements  ActionListener {
        public void actionPerformed(ActionEvent event) {
            try {
                setCursor(hourglassCursor);
                if (arrBlock32 == null) return;
                free();
                secondsInfoPan = new SecondsInfoPan(arrBlock32);
                add(secondsInfoPan, BorderLayout.CENTER);
                SwingUtilities.updateComponentTreeUI(TrainAnalysis.this);
            }
            finally {
                setCursor(defaultCursor);
            }
        }
    }
    // открыть файл поездки - создать arrBlock32
    class OpenListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            try {
                setCursor(hourglassCursor);
                String fileName = node.get("curDir", ".");
                fileChooser.setCurrentDirectory(new File(fileName));
                int result = fileChooser.showOpenDialog(TrainAnalysis.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    fileName = fileChooser.getSelectedFile().getPath();
                    try {
                        arrBlock32 = new ArrBlock32(Paths.get(fileName), true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    labelStatus.setText(fileName);
                    node.put("curDir", fileName);
                    showHexItem.setEnabled(true);
                    showSecondsInfo.setEnabled(true);
                }
            }
            finally {
                setCursor(defaultCursor);
            }
        }
    }
}
