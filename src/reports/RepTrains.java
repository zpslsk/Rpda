package reports;

import db.DetailSelect;
import db.SumSelect;
import static db.Queries.*;
import javax.swing.table.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JTable;

public class RepTrains extends RepAbstract  {

    public RepTrains() {
        // скрываем столбцы
        hideColumnSum(SumSelect.S_NUM_LOC);
        hideColumnSum(SumSelect.S_TYPE_LOC);
        hideColumnSum(SumSelect.S_LAST_NAME);
        hideColumnSum(SumSelect.S_FIRST_NAME);
        hideColumnSum(SumSelect.S_PATRON);
        hideColumnSum(SumSelect.S_TAB_NUM);
        hideColumnSum(SumSelect.S_DRV_COL);
        hideColumnSum(SumSelect.S_FUEL_L);
        hideColumnSum(SumSelect.S_FUEL_KG);
        hideColumnDet(DetailSelect.D_TRAIN_NUM);
        hideColumnDet(DetailSelect.D_FUEL_L);
        hideColumnDet(DetailSelect.D_FUEL_KG);
        hideColumnDet(DetailSelect.D_EHELP1);
        hideColumnDet(DetailSelect.D_EHELP2);
        hideColumnDet(DetailSelect.D_EDOP1);
        hideColumnDet(DetailSelect.D_EDOP2);
        hideColumnDet(DetailSelect.D_NAME);
        setColumnsOrder(SumSelect.COLUMNS_ORDER, getTableSum().getColumnModel());
        setColumnsOrder(DetailSelect.COLUMNS_ORDER, getTableDet().getColumnModel());
    }

    // количество столбцов
    public int getColSum() {return 29;}
    public int getColDet() {return 38;}
    // запросы (суммарный и детальный)
    public String getSqlSum() {return SumSelect.SUM_TRAIN_PASS(false);}
    public String getSqlDet() {return DetailSelect.DET_SELECT_PASS(false);}

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

    // столбец для связывания
    public int getRelationIndex(){
        return SumSelect.S_TRAIN_NUM;           // nTrain
    }
    // параметр для связывания
    public void setRelationParams(Params params, int val) {
        params.setNumTrain(val);
        params.setNumLoc(-1);
        params.setTabNum(-1);
    }
    // столбец с датой
    public int getDateTimeCol() {return DetailSelect.D_DATE;};

    public String getTitleSumTab() {
        return "Суммарный отчет по поездам";
    }

    public String getTitleDetTab(JTable table, int nCol) {
        int nTrain = (int)table.getValueAt(table.getSelectedRow(), SumSelect.S_TRAIN_NUM);
        return  String.format("Детальный отчет по поезду №%d", nTrain);
    }
}
