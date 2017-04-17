package reports;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

import blocks32.ArrBlock32;
import blocks32.ArrMinutes;
import blocks32.Block32Values;
import java.awt.*;


public class SecondsInfoPan extends JPanel {

    private ArrMinutes arrMinutes;

    public SecondsInfoPan(ArrBlock32 arrBlock32) {
        setLayout(new BorderLayout());
        arrMinutes = new ArrMinutes(arrBlock32);
        TableModel tableModel = new TableModel();
        JTable table = new JTable(tableModel);
        Block32Values.resetStartTime();

        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.setBackground(Color.BLACK);
        table.setForeground(Color.WHITE);
        setHeaders(table.getColumnModel());
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private class TableModel extends AbstractTableModel {

        private static final int NUM_COLUMNS = 20;

        public int getRowCount() {
            return arrMinutes.size();
        }

        public int getColumnCount() {
            return NUM_COLUMNS;
        }

        public Object getValueAt(int i, int col) {
            switch (col) {
                case Block32Values.V_XCOORD: return arrMinutes.getValues(i).xCoord;
                case Block32Values.V_XTIME: return arrMinutes.getValues(i).xTime;
                case Block32Values.V_LOST: return arrMinutes.getValues(i).lOst;
                case Block32Values.V_DATE: return arrMinutes.getValues(i).date;
                case Block32Values.V_TIME: return arrMinutes.getValues(i).time;
                case Block32Values.V_NTRAIN: return arrMinutes.getValues(i).nTrain;
                case Block32Values.V_DBAND: return arrMinutes.getValues(i).dBand;
            }
            return null;
        }
    }

    private void setHeaders(TableColumnModel tableColumnModel) {
        HexTablePan.setPropColumn(tableColumnModel.getColumn(Block32Values.V_XCOORD), 50, "Координата");
        HexTablePan.setPropColumn(tableColumnModel.getColumn(Block32Values.V_XTIME), 50, "Секунд");
        HexTablePan.setPropColumn(tableColumnModel.getColumn(Block32Values.V_LOST), 50, "L Ост. (м)");
        HexTablePan.setPropColumn(tableColumnModel.getColumn(Block32Values.V_DATE), 70, "Дата");
        HexTablePan.setPropColumn(tableColumnModel.getColumn(Block32Values.V_TIME), 70, "Время");
        HexTablePan.setPropColumn(tableColumnModel.getColumn(Block32Values.V_NTRAIN), 50, "№ поезда");
        HexTablePan.setPropColumn(tableColumnModel.getColumn(Block32Values.V_DBAND), 50, "Бандаж (мм)");
    }
}
