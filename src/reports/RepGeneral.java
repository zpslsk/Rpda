package reports;

import db.Queries;
import db.Queries.*;
import static db.SumSelect.*;

import javax.swing.table.TableColumn;
import java.sql.ResultSet;
import java.sql.SQLException;
import static db.DetailSelect.*;
import javax.swing.JTable;

public class RepGeneral extends RepAbstract {

    public int getColSum() {return 5;}
    public int getColDet() {return 4;}

    public String getSqlSum() {return SUM_DRIVE_PASS(false);}
    public String getSqlDet() {return DET_SELECT_PASS(false);}

    public Object getSumVal(int col, Object sumStruct) {
        return null;
    }

    public Object getDetVal(int col, Object detStruct) {
        return null;
    }

    public Object getSumStruct(ResultSet rs) throws  SQLException {
//        SumTrain sumTrain = new SumTrain();
        return null;
    }

    public Object getDetStruct(ResultSet rs) throws  SQLException {
//        DetTrain detTrain = new DetTrain();
        return null;
    }

    public void setPropColSum(TableColumn column) {

    }

    public void setPropColDet(TableColumn column) {

    }

    public int getRelationIndex(){
        return -1;
    }

    public void setRelationParams(Params params, int val) {

    }

    public int getDateTimeCol() {return 1;};

    public String getTitleSumTab() {
        return "";
    }

    public String getTitleDetTab(JTable table, int nCol) {
        return "";
    }
}
