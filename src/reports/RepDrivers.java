package reports;

import db.DetailSelect;
import db.SumSelect;
import static db.Queries.*;
import javax.swing.table.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JTable;

public class RepDrivers extends  RepAbstract {

    public RepDrivers() {
        hideColumnSum(SumSelect.S_TRAIN_NUM);
        hideColumnSum(SumSelect.S_NUM_LOC);
        hideColumnSum(SumSelect.S_TYPE_LOC);
        hideColumnSum(SumSelect.S_FUEL_L);
        hideColumnSum(SumSelect.S_FUEL_KG);
        hideColumnDet(DetailSelect.D_FUEL_L);
        hideColumnDet(DetailSelect.D_FUEL_KG);
        hideColumnDet(DetailSelect.D_EDOP1);
        hideColumnDet(DetailSelect.D_EDOP2);
        hideColumnDet(DetailSelect.D_EHELP1);
        hideColumnDet(DetailSelect.D_EHELP2);
        hideColumnDet(DetailSelect.D_NAME);
        setColumnsOrder(SumSelect.COLUMNS_ORDER, getTableSum().getColumnModel());
        setColumnsOrder(DetailSelect.COLUMNS_ORDER, getTableDet().getColumnModel());
    }

    public int getColSum() {return 29;}
    public int getColDet() {return 38;}

    // запросы (суммарный и детальный)
    public String getSqlSum() {return SumSelect.SUM_DRIVE_PASS(true);}
    public String getSqlDet() {return DetailSelect.DET_SELECT_PASS(true);}

    // значения строки таблицы из структуры sumStruct
    public Object getSumVal(int col, Object sumStruct) {
        return SumSelect.getSumVal(col, sumStruct);
    }
    // значения строки таблицы из структуры DetTrain
    public Object getDetVal(int col, Object detStruct) {
        return DetailSelect.getDetVal(col, detStruct);
    }

    // заполнене структуры SumTrain из запроса
    public Object getSumStruct(ResultSet rs) throws  SQLException {
        return SumSelect.getSumStruct(rs);
    }

    // заполнене структуры DetTrain из запроса
    public Object getDetStruct(ResultSet rs) throws  SQLException {
        return DetailSelect.getDetStruct(rs);
    }

    // свойства столбцов
    public void setPropColSum(TableColumn column) {
        SumSelect.setPropColSum(column);
    }

    public void setPropColDet(TableColumn column) {
        DetailSelect.setPropColDet(column);
    }

    public int getRelationIndex(){
        return SumSelect.S_TAB_NUM;
    }

    public void setRelationParams(Params params, int val) {
        params.setNumLoc(-1);
        params.setNumTrain(-1);
        params.setTabNum(val);
    }

    public int getDateTimeCol() {return DetailSelect.D_DATE;};

    public String getTitleSumTab() {
        return "Суммарный отчет по машинистам";
    }

    public String getTitleDetTab(JTable table, int nCol) {
        String firstName = (String)table.getValueAt(table.getSelectedRow(), SumSelect.S_FIRST_NAME);
        String lastName = (String)table.getValueAt(table.getSelectedRow(), SumSelect.S_LAST_NAME);
        String patron = (String)table.getValueAt(table.getSelectedRow(), SumSelect.S_PATRON);
        int tabNum = (int)table.getValueAt(table.getSelectedRow(), SumSelect.S_TAB_NUM);
        return String.format("Детальный отчет по машинисту %s %s %s (таб.№%d)", lastName, firstName, patron, tabNum);
    }
}
