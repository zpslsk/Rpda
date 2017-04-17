package reports;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import blocks32.ArrBlock32;
import blocks32.Block32;
import blocks32.Block32Values;

import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import java.awt.*;

public class HexTablePan extends JPanel {

    private static final Color BACK_COLOR = new Color(0x000000);
    private static final Color BACK_COLOR_SEL = new Color(0x555457);
    private ArrBlock32 arrBlock32;

    public HexTablePan(ArrBlock32 arrBlock32) {
        setLayout(new BorderLayout());
        this.arrBlock32 = arrBlock32;
        TableModel tableModel = new TableModel();
        JTable table = new JTable(tableModel);
        table.setBackground(Color.BLACK);
        table.setSelectionBackground(BACK_COLOR_SEL);

        table.setFont(new Font("Monospaced", table.getFont().getStyle(), 12));
        setPropColumn(table.getColumnModel().getColumn(0), 70, "№ блока");
        setPropColumn(table.getColumnModel().getColumn(1), 50, "№ типа");
        setPropColumn(table.getColumnModel().getColumn(2), 70, "Тип");
        for (int i = 3; i <= 31; i++)
            setPropColumn(table.getColumnModel().getColumn(i), 30, Integer.toString(i - 3));
//        setPropColumn(table.getColumnModel().getColumn(32), 140, "-");
//        setPropColumn(table.getColumnModel().getColumn(33), 140, "-");
//        setPropColumn(table.getColumnModel().getColumn(34), 140, "-");
//        setPropColumn(table.getColumnModel().getColumn(35), 140, "-");
//        setPropColumn(table.getColumnModel().getColumn(36), 140, "-");
        for (int i = 0; i < table.getColumnCount(); i++) {
            // блок - цвет
            table.getColumnModel().getColumn(i).setCellRenderer(new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(JTable table,
                                                               Object value, boolean isSelected, boolean hasFocus,
                                                               int row, int column) {
                    if (column == 0) {
                        setForeground(Color.WHITE);
                        table.setSelectionForeground(Color.WHITE);
                    } else {
                        Block32.TypeBlock typeBlock = arrBlock32.get(row).type();
                        setForeground(getColorBlockType(typeBlock));
                        table.setSelectionForeground(getColorBlockType(typeBlock));
                    }
                    return super.getTableCellRendererComponent(table, value, isSelected,
                            hasFocus, row, column);
                }
            });
        }
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public static void setPropColumn(TableColumn column, int width, String header) {
        column.setMinWidth(width / 2);
        column .setMaxWidth(width * 2);
        column.setPreferredWidth(width);
        column.setHeaderValue(header);
    }

    public static Color getColorBlockType(Block32.TypeBlock typeBlock) {

        switch(typeBlock) {
            case USAVIni: return new Color(0xFFE5C1);
            case USAVWag: return new Color(0x69B5FF);
            case USAVLim: return new Color(0x96E8CF);
            case USAVMain: return new Color(0xAD837D);
            case USAVKeyBDU: return new Color(0x82D573);
            case USAVShedDisp: return new Color(0xBF78FF);
            case USAVChangeDisp: return new Color(0x8D7AFF);
            case USAVBHV: return new Color(0x77A6FF);
            case USAVMapObj: return new Color(0x6FEEFF);
            case USAVPilot: return new Color(0x74FF8F);
            case USAVISAVPRT: return new Color(0xC5FF7B);
            case USAVWagonsDisp: return new Color(0xEBFF77);
            case USAVChangeDisp2: return new Color(0xFFF683);
            case USAVKLUB: return new Color(0xFF8287);
            case USAVPas: return  new Color(0xDCA7FF);
            case UI: return new Color(0xFF93BA);
            case DPS1: return new Color(0xFFF06B);
            case DPS2: return new Color(0xA078FF);
            case En: return new Color(0xD6FF7E);
            case Press: return new Color(0xFF62B8);
            case Init: return new Color(0x84A4FF);
            case EKS1: return new Color(0xB6FFA4);
            case EKS2: return new Color(0x8FFF7B);
            case Diagn: return new Color(0xFFEE55);
            case Mon: return new Color(0xA1D9FF);
            case BMS: return new Color(0xFF9BF8);
            case Map4: return new Color(0x69B5FF);
            case Map5: return new Color(0x86CFFF);
            case Map9: return new Color(0xFFE5C1);
            case MapA: return new Color(0xDEFF9F);
            case MapB: return new Color(0xDF16FF);
            case C7: return new Color(0xB5CEFF);

            default: return new Color(0xFCFAFF);
        }
    }

    private class TableModel extends AbstractTableModel {

        private static final String TAG = "%02X";
        private static final int NUM_COLUMNS = 32;

        public int getRowCount() {
            return arrBlock32.size();
        }

        public int getColumnCount() {
            return NUM_COLUMNS;
        }

        public Object getValueAt(int i, int col) {
            Block32Values block32Values = new Block32Values(arrBlock32.get(i));
            switch(col) {
                case 0: return String.format("%,08d", i);                               // ord
                case 1: return String.format("0x" + TAG, arrBlock32.get(i).getId());    // id
                case 2: return arrBlock32.get(i).type().name();
                case 3: return String.format(TAG, arrBlock32.get(i).get(0));
                case 4: return String.format(TAG, arrBlock32.get(i).get(1));
                case 5: return String.format(TAG, arrBlock32.get(i).get(2));
                case 6: return String.format(TAG, arrBlock32.get(i).get(3));
                case 7: return String.format(TAG, arrBlock32.get(i).get(4));
                case 8: return String.format(TAG, arrBlock32.get(i).get(5));
                case 9: return String.format(TAG, arrBlock32.get(i).get(6));
                case 10: return String.format(TAG, arrBlock32.get(i).get(7));
                case 11: return String.format(TAG, arrBlock32.get(i).get(8));
                case 12: return String.format(TAG, arrBlock32.get(i).get(9));
                case 13: return String.format(TAG, arrBlock32.get(i).get(10));
                case 14: return String.format(TAG, arrBlock32.get(i).get(11));
                case 15: return String.format(TAG, arrBlock32.get(i).get(12));
                case 16: return String.format(TAG, arrBlock32.get(i).get(13));
                case 17: return String.format(TAG, arrBlock32.get(i).get(14));
                case 18: return String.format(TAG, arrBlock32.get(i).get(15));
                case 19: return String.format(TAG, arrBlock32.get(i).get(16));
                case 20: return String.format(TAG, arrBlock32.get(i).get(17));
                case 21: return String.format(TAG, arrBlock32.get(i).get(18));
                case 22: return String.format(TAG, arrBlock32.get(i).get(19));
                case 23: return String.format(TAG, arrBlock32.get(i).get(20));
                case 24: return String.format(TAG, arrBlock32.get(i).get(21));
                case 25: return String.format(TAG, arrBlock32.get(i).get(22));
                case 26: return String.format(TAG, arrBlock32.get(i).get(23));
                case 27: return String.format(TAG, arrBlock32.get(i).get(24));
                case 28: return String.format(TAG, arrBlock32.get(i).get(25));
                case 29: return String.format(TAG, arrBlock32.get(i).get(26));
                case 30: return String.format(TAG, arrBlock32.get(i).get(27));
                case 31: return String.format(TAG, arrBlock32.get(i).get(28));
//                case 32: return block32Values.getNumTrain();
//                case 33: return block32Values.getLOst();
//                case 34: return block32Values.getLineX();
//                case 35: return block32Values.getLineT();
//                case 36: return block32Values.getDateTime();
                default: return -1;
            }
        }
    }
}


