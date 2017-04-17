package start;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StartFrame extends JFrame {
    // версии программы и базы данных
    public static final VersionExe versionExe = new VersionExe(1, 3, 16,237);
    public static final db.DataBase.VersionBase versionBase = new db.DataBase.VersionBase(1, 74);
    //
    private static final int DEFAULT_WIDTH = 520;
    private static final int DEFAULT_HEIGHT = 460;
    private static final String ARM_TITLE = "<html><h1 align=\"center\"><i><font color=\"white\">" +
            "Регистратор параметров<br>движения автоведения</font></i></h1></htm1>";
    private static final Color BACKGR_PAN = new Color(0xE1, 0xC6, 0x04);
    private static final Color BACKGR_CENTR_PAN = Color.WHITE;
    private int nButton = 0;
    private JPanel centralPanel;

    private AboutDialog aboutDialog;
    private ReportsDialog reportsDialog;
    private TrainAnalysis trainAnalysis;

    public static class VersionExe {
        private int major;
        private int minor;
        private int release;
        private int build;
        // версия exe
        public VersionExe(int major, int minor, int release, int build) {
            this.major = major;
            this.minor = minor;
            this.release = release;
            this.build = build;
        }
        public String toString() {
            return String.format("%d.%d.%d.%d", major, minor, release, build);
        }
        public int getMajor() {
            return major;
        }
        public int getMinor() {
            return minor;
        }
        public int getRelease() {
            return release;
        }
        public int getBuild() {
            return release;
        }
    }

    public StartFrame(Connection conn) {
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setResizable(false);
        // окно в центре экрана
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        setLocation(screenSize.width / 2 - DEFAULT_WIDTH / 2, screenSize.height / 2 - DEFAULT_HEIGHT / 2);
        // иконки окна и логотипа
        setIconImage(new ImageIcon(getClass().getResource("images/armlogo.png")).getImage());
        JLabel labelLogo = new JLabel();
        labelLogo.setIcon(new ImageIcon(getClass().getResource("images/ocvlogo.png")));
        // панели
        JPanel topPanel = new JPanel();
        topPanel.setBackground(BACKGR_PAN);
        topPanel.add(new JLabel(ARM_TITLE, JLabel.CENTER));
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(BACKGR_PAN);
        centralPanel = new JPanel();
        centralPanel.setBackground(BACKGR_CENTR_PAN);
        centralPanel.setLayout(new GridBagLayout());
        // компановка панелей
        setLayout(new GridBagLayout());
        add(labelLogo, new GBC(0, 0).setFill(GBC.BOTH).setWeight(0, 0));
        add(topPanel, new GBC(1, 0).setFill(GBC.BOTH).setWeight(100, 0));
        add(leftPanel, new GBC(0, 1).setFill(GBC.BOTH).setWeight(0, 100));
        add(centralPanel, new GBC(1, 1).setFill(GBC.BOTH).setWeight(100, 100));
        // кнопки
        addButton("Анализ одного картриджа", true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        EventQueue.invokeLater(() -> {
                            if (trainAnalysis == null) {
                                trainAnalysis = new TrainAnalysis(StartFrame.this);
                            }
                            trainAnalysis.setVisible(true);
                        });
                    }
                });
        addButton("Пакетная обработка", conn != null,
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {

                    }
                });
        addButton("Отчеты", conn != null,
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        EventQueue.invokeLater(() -> {
                            if (reportsDialog == null) {
                                reportsDialog = new ReportsDialog(StartFrame.this);
                            }
                            reportsDialog.setVisible(true);
                        });
                    }
                });
        addButton("Настройки", true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {

                    }
                });
        addButton("О программе", true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        EventQueue.invokeLater(() -> {
                            if (aboutDialog == null)
                                aboutDialog = new AboutDialog(
                                        StartFrame.this, versionExe.toString(), versionBase.toString());

                            aboutDialog.setVisible(true);
                        });
                    }
                });
        addButton("Выход", true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        System.exit(0);
                    }
                });
    }

    private JButton addButton(String title, boolean isEnabled, ActionListener listener) {

        final String tag_open = "<html><h2><font color=\"#000099\">";
        final String tag_close = "</font></h2></html>";
        JButton button = new JButton(tag_open + title + tag_close);
        button.setBackground(BACKGR_CENTR_PAN);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setPreferredSize(new Dimension(DEFAULT_WIDTH - DEFAULT_WIDTH / 3  , 30));
        button.setFocusPainted(false);          // откл прорисовка рмки активной кнопки
        button.setContentAreaFilled(false);     // откл визуальный отклик на нажатие
        button.setEnabled(isEnabled);
        button.addActionListener(listener);
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent event) {
                button.setText("<html><h2><font color=\"#A40000\">" + title + "</font></h2></html>");
            }
            public void mouseExited(MouseEvent event) {
                button.setText(tag_open + title + tag_close);
            }
        });
        centralPanel.add(button,  new GBC(0, nButton++).setFill(GridBagConstraints.CENTER).setWeight(100, 0).setInsets(10, 0, 10, 0));
        return button;
    }
}
