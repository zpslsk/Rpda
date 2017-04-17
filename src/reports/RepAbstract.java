package reports;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.border.TitledBorder;

import db.DetailSelect;
import db.Queries.Params;
import db.SumSelect;

import java.sql.ResultSet;
import java.time.format.DateTimeFormatter;

public abstract class RepAbstract extends JPanel {

    public static final String LT = "<html><center>";
    public static final String RT = "</center></html>";
    public final DateTimeFormatter formatter_in = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

    private static final int HEADER_HEIGHT = 50;    // высота заголовка
    private static final int COLUMN_MARGIN = 0;     // интервал между столбцами
    private static final String DATE_FORMAT = "dd.MM.yyyy   HH:mm";
    private TitledBorder borderSum;
    private TitledBorder borderDet;
    private ArrayList<Object> arrayListSum = new ArrayList<>();
    private ArrayList<Object> arrayListDet = new ArrayList<>();
    private JSplitPane splitPane;
    private Params params;
    private JTable tableSum;
    private JTable tableDet;
    private JScrollPane scrollSum;
    private JScrollPane scrollDet;
    private TableSumModel sumModel;
    private TableDetModel detModel;

    class TableSumModel extends AbstractTableModel {

        public int getRowCount() {return arrayListSum.size();}
        public int getColumnCount() {return getColSum();}

        public Object getValueAt(int row, int column) {
            if (row > arrayListSum.size() - 1) row = 0;
            Object sumStruct = null;
            if (arrayListSum.size() == 0) {
                tableSum.setVisible(false);
                return 1;
            } else {
                tableSum.setVisible(true);
                sumStruct = arrayListSum.get(row);
            }
            return getSumVal(column, sumStruct);
        }
        // настройка сортировки
        public Class getColumnClass(int column) {
            Object obj = getValueAt(0, column);
            if (obj != null)
                return obj.getClass();
            else
                return String.class;
        }
    }

    class TableDetModel extends  AbstractTableModel {

        public int getRowCount() {return arrayListDet.size();}
        public int getColumnCount() {return getColDet();}

        public Object getValueAt(int row, int column) {
            if (row > arrayListDet.size() - 1) row = 0;
            Object detStruct = null;
            if (arrayListDet.size() == 0) {
                tableDet.setVisible(false);
                return 0;
            } else {
                tableDet.setVisible(true);
                detStruct = arrayListDet.get(row);
            }
            return getDetVal(column, detStruct);
        }
        // настройка сортировки
        public Class getColumnClass(int column) {
            Object obj = getValueAt(0, column);
            if (obj != null)
                return obj.getClass();
            else
                return String.class;
        }
    }

    public RepAbstract() {
        setLayout(new BorderLayout());
        sumModel = new TableSumModel();
        detModel = new TableDetModel();
        createTableSum();
        createTableDet();
        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollSum, scrollDet);
        splitPane.setContinuousLayout(true);
        splitPane.setOneTouchExpandable(true);
        add(splitPane);
    }

    private TableCellRenderer tableDateTimeRenderer = new DefaultTableCellRenderer() {

        DateTimeFormatter f = DateTimeFormatter.ofPattern(DATE_FORMAT);
        public Component getTableCellRendererComponent(JTable table,
                                                       Object value, boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            if( value instanceof LocalDateTime) {
                value = ((LocalDateTime)value).format(f);
            }
            return super.getTableCellRendererComponent(table, value, isSelected,
                    hasFocus, row, column);
        }
    };

    public void setSplitLocation(int location) {splitPane.setDividerLocation(location);}
    public int getSplitLocation() {return splitPane.getDividerLocation();}
    public void setParams(Params params) {this.params = params;}
    public Params getParams() {return params;}
    public void setTableSumFocus() {tableSum.requestFocus();}
    // детальная таблица
    public void createTableDet() {
        tableDet = new JTable(detModel);
        tableDet.getColumnModel().setColumnMargin(COLUMN_MARGIN);
        tableDet.setAutoCreateRowSorter(true);
        tableDet.getColumnModel().getColumn(getDateTimeCol()).setCellRenderer(tableDateTimeRenderer);
        tableDet.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableDet.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableDet.getTableHeader().setReorderingAllowed(false);
        // свойства столбцов
        for (int i = 0; i < tableDet.getColumnCount(); i++)
            setPropColDet(tableDet.getColumnModel().getColumn(i));
        // установка высоты заголовка
        scrollDet = new JScrollPane(tableDet);
        borderDet = BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (),
                " Детальный отчет ",
                TitledBorder.CENTER,
                TitledBorder.TOP);
        borderDet.setTitleColor(Color.BLUE);
        scrollDet.setBorder(borderDet);
        scrollDet.setColumnHeader(new JViewport() {
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.height = HEADER_HEIGHT;
                return  d;
            }
        });
    }
    // суммарная таблица
    public void createTableSum() {
        tableSum = new JTable(sumModel);
        // связь с детальной таблицей
        tableSum.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent event) {
                if (!event.getValueIsAdjusting()) {
                    int index = tableSum.getSelectedRow();
                    if (index >= 0 && index < tableSum.getRowCount()) {
                        params.setRowIndex(index);
                        Object val = tableSum.getValueAt(tableSum.getSelectedRow(), getRelationIndex());
                        borderDet.setTitle(getTitleDetTab(tableSum, SumSelect.S_TRAIN_NUM));
                        scrollDet.repaint();
                        setRelationParams(params, (int) val);
                        try {
                            doQueryDet();
//                            SwingUtilities.updateComponentTreeUI(tableDet);
//                            SwingUtilities.updateComponentTreeUI(tableSum);
                        } catch (SQLException e) {
                            JOptionPane.showMessageDialog(RepAbstract.this, e.getMessage());
                        }
                    }
                }
            }
        });
        tableSum.setAutoCreateRowSorter(true);
        tableSum.getColumnModel().setColumnMargin(COLUMN_MARGIN);
        tableSum.setSelectionBackground(Color.LIGHT_GRAY);
        tableSum.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tableSum.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableSum.getTableHeader().setReorderingAllowed(false);
        // свойства столбцов
        for (int i = 0; i < tableSum.getColumnCount(); i++)
            setPropColSum(tableSum.getColumnModel().getColumn(i));
        // установка высоты заголовка
        scrollSum = new JScrollPane(tableSum);
        borderSum = BorderFactory.createTitledBorder (BorderFactory.createEtchedBorder (),
                getTitleSumTab(),
                TitledBorder.CENTER,
                TitledBorder.TOP);
        borderSum.setTitleColor(Color.BLUE);
        scrollSum.setBorder(borderSum);
        scrollSum.setColumnHeader(new JViewport() {
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.height = HEADER_HEIGHT;
                return d;
            }
        });
    }

    public void hideColumnSum(int id) {
        TableColumn column = tableSum.getColumn(id);
        column.setMinWidth(0);
        column.setMaxWidth(0);
        column.setPreferredWidth(0);
    }

    public void showColumnSum(int id) {
        TableColumn column =  tableSum.getColumn(id);
        SumSelect.setPropColSum(column);
    }

    public void hideColumnDet(int id) {
        TableColumn column = tableDet.getColumn(id);
        column.setMinWidth(0);
        column.setMaxWidth(0);
        column.setPreferredWidth(0);
    }

    public void showColumnDet(int id) {
        TableColumn column = tableDet.getColumn(id);
        DetailSelect.setPropColDet(column);
    }

    public static void setColumnsOrder(int[] indices, TableColumnModel columnModel) {

        TableColumn columns[] = new TableColumn[indices.length];
        for (int i = 0; i < columns.length; i++) {
            columns[i] = columnModel.getColumn(indices[i]);
        }
        while (columnModel.getColumnCount() > 0) {
            columnModel.removeColumn(columnModel.getColumn(0));
        }
        for(TableColumn column : columns) {
            columnModel.addColumn(column);
        }
    }

    public boolean doQuery() throws SQLException {
        // заполняем список для суммарной таблицы
        arrayListSum.clear();
        try (ResultSet rs  = db.DataBase.getResultSet(getSqlSum(), params)) {
            while (rs.next()) arrayListSum.add(getSumStruct(rs));
        }

        if (arrayListSum.size() > 0)
            sumModel.fireTableDataChanged();

        if (tableSum.getRowCount() > 0) tableSum.setRowSelectionInterval(0, 0);

        doQueryDet();
        return arrayListSum.size() > 0;
    }

    private void doQueryDet() throws SQLException {
        if (arrayListSum.size() == 0) {
            arrayListDet.clear();
            return;
        }
        arrayListDet.clear();
        try (ResultSet rs = db.DataBase.getResultSet(getSqlDet(), params)) {
            while (rs.next()) arrayListDet.add(getDetStruct(rs));
        }
        if (arrayListDet.size() > 0)
            detModel.fireTableDataChanged();
    }

    public JTable getTableSum() {
        return tableSum;
    }

    public JTable getTableDet() {
        return tableDet;
    }

    // кол колонок таблиц
    abstract public int getColSum();
    abstract public int getColDet();
    // значение ячейки
    public abstract Object getSumVal(int col, Object sumStruct);
    public abstract Object getDetVal(int col, Object detStruct);
    // запросы
    abstract public String getSqlSum();
    abstract public String getSqlDet();
    // набор значений стороки таблиц
    public abstract Object getSumStruct(ResultSet rs) throws  SQLException;
    public abstract Object getDetStruct(ResultSet rs) throws  SQLException;
    // свойства колонок таблицы
    public abstract void setPropColSum(TableColumn column);
    public abstract void setPropColDet(TableColumn column);
    // индекс столбца суммарной таблицы для связывания
    public abstract int getRelationIndex();
    // параметры для связывания
    public abstract void setRelationParams(Params params, int val);
    // индекс колонки с датой-временем
    public abstract int getDateTimeCol();
    // заголовок таблицы
    public abstract String getTitleSumTab();
    public abstract String getTitleDetTab(JTable table, int nCol);
}
