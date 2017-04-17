package start;

import db.Queries.Params;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import reports.*;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.sql.SQLException;
import java.util.prefs.*;
import java.awt.event.*;
import java.util.*;
import java.time.*;

public class ReportsDialog extends JDialog {

    private Preferences root = Preferences.userRoot();
    private Preferences node = root.node("/avpt/arm/rpda/report");
    private JToolBar toolBar = new JToolBar();
    private RepAbstract currentReport;
    private RepTrains repTrains;
    private RepLocmotives repLocmotive;
    private RepGeneral repGeneral;
    private RepDrivers repDrivers;
    private SpinnerDate  startDateSpinner;
    private SpinnerDate finishDateSpinner;
    private ComboTypeLoc typeLocCombo;
    private Cursor hourglassCursor;
    private Cursor defaultCursor;
    // серии локомотивов для typeLocCombo
    private final ItemComboTypeLoc[] itemComboTypeLoc = new ItemComboTypeLoc[] {
        new ItemComboTypeLoc(-1, "Все серии"),
        new ItemComboTypeLoc(118, "ТЭП70"),
        new ItemComboTypeLoc(119, "ТЭП70БС"),
        new ItemComboTypeLoc(106, "ЧС2 ЕКС2"),
        new ItemComboTypeLoc(105, "ЧС2 КАУД"),
        new ItemComboTypeLoc(104, "ЧС2 КР.ЗНАМЯ"),
        new ItemComboTypeLoc(109, "ЧС2К ЕСАУП"),
        new ItemComboTypeLoc(108, "ЧС2К КАУД"),
        new ItemComboTypeLoc(107, "ЧС2К УСАВПП"),
        new ItemComboTypeLoc(110, "ЧС2Т КАУД"),
        new ItemComboTypeLoc(100, "ЧС200 КАУД"),
        new ItemComboTypeLoc(112, "ЧС4Т КАУД"),
        new ItemComboTypeLoc(111, "ЧС6 КАУД"),
        new ItemComboTypeLoc(101, "ЧС7"),
        new ItemComboTypeLoc(102, "ЧС7 ЕКС"),
        new ItemComboTypeLoc(116, "ЧС7 ЕКС2"),
        new ItemComboTypeLoc(121, "ЧС7 КАУД П"),
        new ItemComboTypeLoc(103, "ЧС7 КАУД Ф"),
        new ItemComboTypeLoc(114, "ЧС8 КАУД"),
        new ItemComboTypeLoc(113, "ЭП1 КАУД"),
        new ItemComboTypeLoc(122, "ЭП1П"),
        new ItemComboTypeLoc(115, "ЭП1У КАУД"),
        new ItemComboTypeLoc(117, "ЭП2К КАУД"),
        new ItemComboTypeLoc(120, "ЭП20")
    };

    public ReportsDialog(JFrame owner) {
        super(owner, "Отчеты", true);
        hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
        defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
        // расположение окна
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int left = node.getInt("left", 0);
        int top = node.getInt("top", 0);
        int width = node.getInt("width", (int)screenSize.getWidth() - (int)screenSize.getWidth() / 25);
        int height = node.getInt("height", (int)screenSize.getHeight() - (int)screenSize.getHeight() / 25);
        int splitLocation = node.getInt("splitLocation", height / 2); // положение разделителя для отчета по поездам
        setBounds(left, top, width, height);

        // кнопка выполнения запроса
        JButton btnQuery = makeButton("Выполнить запрос", "images/report/btn_select.png",
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        if (execCurrentReport())
                            currentReport.setTableSumFocus();
                    }
                });
        // кнопки создания отчетов
        JButton btnGeneral = makeButton("Итоговый", "images/report/btn_general.png",
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        if (repGeneral == null) {
                            repGeneral = new RepGeneral();
                            repGeneral.setSplitLocation(splitLocation);
                        }
                        addReport(repGeneral);
                        enabledBtnSelf((JButton)event.getSource());
                        //execCurrentReport();
                    }
                });
        JButton btnTrain = makeButton("По поездам", "images/report/btn_train.png",
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        if (repTrains == null) {
                            repTrains = new RepTrains();
                            repTrains.setSplitLocation(splitLocation);
                        }
                        addReport(repTrains);
                        enabledBtnSelf((JButton)event.getSource());
                        if (execCurrentReport())
                            currentReport.setTableSumFocus();
                    }
                });
        JButton btnLoc = makeButton("По локомтивам", "images/report/btn_loc.png",
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        if (repLocmotive == null) {
                            repLocmotive = new RepLocmotives();
                            repLocmotive.setSplitLocation(splitLocation);
                        }
                        addReport(repLocmotive);
                        enabledBtnSelf((JButton)event.getSource());
                        if (execCurrentReport())
                            currentReport.setTableSumFocus();
                    }
                });
        JButton btnDriv = makeButton("По машинистам", "images/report/btn_driv.png",
                new ActionListener() {
                    public void actionPerformed(ActionEvent event) {
                        if (repDrivers == null) {
                            repDrivers = new RepDrivers();
                            repDrivers.setSplitLocation(splitLocation);
                        }
                        addReport(repDrivers);
                        enabledBtnSelf((JButton)event.getSource());
                        if (execCurrentReport())
                            currentReport.setTableSumFocus();
                    }
                });

        // дата - picker
        startDateSpinner = new SpinnerDate(LocalDate.now().minusYears(1), "Начало выборки");
        finishDateSpinner = new SpinnerDate(LocalDate.now(), "Конец выборки");
        // combo LocType
        typeLocCombo = new ComboTypeLoc();
        typeLocCombo.addPopupMenuListener(new PopupMenuListener() {

            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (execCurrentReport())
                    currentReport.setTableSumFocus();
            }
            public void popupMenuCanceled(PopupMenuEvent e) {}
        });
        // JToolBar
        toolBar.setFloatable(false);
        toolBar.add(btnQuery);
        toolBar.addSeparator();
        toolBar.add(startDateSpinner);
        toolBar.addSeparator();
        toolBar.add(finishDateSpinner);
        toolBar.addSeparator();
        toolBar.add(typeLocCombo);
        toolBar.addSeparator();
        toolBar.add(btnGeneral);
        toolBar.add(btnTrain);
        toolBar.add(btnLoc);
        toolBar.add(btnDriv);
        add(toolBar, BorderLayout.NORTH);
        // обработка закрытия окна
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent event) {
                savePreferences();
            }
        });
    }
    // combo box для выбора типа локомотива
    public class ComboTypeLoc extends  JComboBox<ItemComboTypeLoc> {

        public ComboTypeLoc() {
            super(itemComboTypeLoc);
            AutoCompleteDecorator.decorate(this);
            setToolTipText("Серия локомотива");
            setMaximumSize(new Dimension(150, 25));
        }

        public int getValue() {return getItemAt(getSelectedIndex()).value;}
        public String getLabel() {return getItemAt(getSelectedIndex()).label;}
    }
    // item для ComboTypeLoc
    private class ItemComboTypeLoc {
        private int value;
        private String label;
        public ItemComboTypeLoc(int value, String label) {
            this.value = value;
            this.label = label;
        }
        public int getVlue() {
            return value;
        }
        public String getLabel() {
            return label;
        }
        public String toString() {
            return label;
        }
    }
    // сохранение настроек
    public void savePreferences() {
        node.putInt("left", getX());
        node.putInt("top", getY());
        node.putInt("width", getWidth());
        node.putInt("height", getHeight());
        if (currentReport != null)
            node.putInt("splitLocation", currentReport.getSplitLocation());

    }
    // создать кнопку
    private JButton makeButton(String text, String res, ActionListener listener) {
        JButton btn = new JButton(new ImageIcon(getClass().getResource(res)));
        btn.setToolTipText(text);
        btn.addActionListener(listener);
        return btn;
    }
    // показать отчет
    private void addReport(RepAbstract report) {
        if (currentReport != null) remove(currentReport);
        add(report, BorderLayout.CENTER);
        currentReport = report;
        SwingUtilities.updateComponentTreeUI(currentReport);
    }
    // отключить кнопку текущего отчета
    private void enabledBtnSelf(JButton selfButton) {
        Component[] components =  toolBar.getComponents();
        for (Component component : components) {
            String className = component.getClass().getSimpleName();
            if (className.equals("JButton"))
                component.setEnabled(true);
        }
        selfButton.setEnabled(false);
    }
    // выполнить текущий запрос
    private boolean execCurrentReport() {
        try {
            setCursor(hourglassCursor);
            if (currentReport != null) {
                try {
                    if (currentReport.getParams() == null)
                        currentReport.setParams(new Params(startDateSpinner.getLocalDate(), finishDateSpinner.getLocalDate(),
                                typeLocCombo.getValue(), -1, -1));
                    else {
                        currentReport.getParams().setDateStart(startDateSpinner.getLocalDate());
                        currentReport.getParams().setDateFinish(finishDateSpinner.getLocalDate());
                        currentReport.getParams().setTypeTrain(typeLocCombo.getValue());
                    }
                    return currentReport.doQuery();
                } catch (SQLException e) {
                    JOptionPane.showMessageDialog(this, e.getMessage());
                    return false;
                }
            } else {
                return false;
            }
        } finally {
            setCursor(defaultCursor);
            SwingUtilities.updateComponentTreeUI(currentReport);
        }
    }
    // дата - picker
    public class SpinnerDate extends JSpinner {

        public SpinnerDate(LocalDate localDate, String text) {
            super(new SpinnerDateModel());
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(this, "dd.MM.yyyy");
            setEditor(dateEditor);
            setValue(date);
            setToolTipText(text);
            setMaximumSize(new Dimension(100, 25));
        }

        public void setLocalDate(LocalDate localDate) {
            Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            setValue(date);
        }

        public LocalDate getLocalDate() {
            Date date = (Date)this.getValue();
            return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
        }
    }
}


