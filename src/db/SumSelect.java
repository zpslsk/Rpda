package db;

import javax.swing.table.TableColumn;
import java.sql.ResultSet;
import java.sql.SQLException;
import static db.Queries.*;

public class SumSelect {
    private static final String TRAIN_SELECT_FIRST = "train_num AS train_num, 1 AS loc_num, '' AS type_name, '' AS surname, '' AS name, '' AS patronimic, 1 AS tb_num, 1 AS drvCol,  -- 1 - 8";
    private static final String LOC_SELECT_FIRST = "1 AS train_num, loc_num AS loc_num, type_name AS type_name, '' AS surname, '' AS name, '' AS patronimic, 1 AS tb_num, 1 AS drvCol,  -- 1 - 8";
    private static final String DRIV_SELECT_FIRST = "1 AS train_num, 1 AS loc_num, '' AS type_name, surname AS surname, name AS name, patronymic AS patronimic, tb_num AS tb_num, drvCol AS drvCol, -- 1 - 8";
    private static final String TRAIN_SELECT_LAST = "GROUP BY train_num ORDER BY train_num; -- поезд";
    private static final String LOC_SELECT_LAST = "GROUP BY loc_num, type_name ORDER BY loc_num, type_name; -- локомотив";
    private static final String DRIV_SELECT_LAST = "GROUP BY surname, name, patronymic, tb_num, drvCol ORDER BY surname -- машинист";
    private static final String SUM_SELECT_PASS =
            "USE RPDAP %s\n" +
                    "SELECT\n" +
                    "%s\n" +
                    "COUNT(*) AS cnt, -- 9 всего\n" +
                    "SUM(x_Common) AS x_Common, -- 10 пробег\n" +
                    "SUM(x_SavpeAuto) AS x_SavpeAuto, -- 11 пробег автоведение\n" +
                    "1, -- 12 пробег автоведение процент\n" +
                    "SUM(x_SavpePrompt) AS x_SavpePrompt, -- 13 пробег подсказка\n" +
                    "1,  -- 14 пробег подсказка процент\n" +
                    "SUM(trWork) AS trWork, -- 15 работа\n" +
                    "SUM(TFuel_l) AS TFuel_l, -- 16 топливо л\n" +
                    "SUM(TFuel_kg) AS TFuel_kg, -- 17 топливо кг\n" +
                    "SUM(train_time) AS train_time, -- 18 время\n" +
//                    "SUM(train_time_move) AS train_time_move,-- 19 время в движении\n" +
                    "AVG(av_speed) AS av_speed, -- 19 средняя участковая скорость\n" +
                    "AVG(av_speed_move) AS av_speed_move, -- 20 средняя техническая скорость\n" +
                    "SUM(countTLim) AS countTLim, -- 21 всего временных ограничений\n" +
                    "SUM(ESum) AS ESum, -- 22 ESum\n" +
                    "SUM(EHeat) AS EHeat, -- 23 EHeat\n" +
                    "SUM(EHelp) AS EHelp, -- 24 EHelp\n" +
                    "SUM(ERecup) AS ERecup, -- 25 ERecup\n" +
                    "SUM(E1B1) AS E1B1, -- 26 E1B1\n" +
                    "SUM(E1B2) AS E1B2, -- 27 E1B2\n" +
                    "SUM(E2B1) AS E2B1, -- 28 E2B1\n" +
                    "SUM(E2B2) AS E2B2 -- 29 E2B2\n" +
                    "FROM m_Trains tr\n" +
                    "INNER JOIN m_TrainsInfo ti ON ti.train_id = tr.train_id %s\n" +
                    "INNER JOIN ci_Locomotiv loc ON loc.loc_id = ti.loc_id\n" +
                    "INNER JOIN ci_LocType lt ON lt.type_id = loc.loc_type  AND ((loc.loc_type = @loc_type) or (@loc_type < 0))\n" +
                    "INNER JOIN ci_Drivers dr ON dr.drv_id = tr.drv_id AND ((dr.DrvCol = @drvCol) OR (@drvCol < 0))\n" +
                    "LEFT JOIN m_XFiles xf ON xf.id_image = tr.image_id\n" +
                    "WHERE \n" +
                    "dateTr BETWEEN @date_start AND @date_finish\n" +
                    "%s\n";

    public static String SUM_TRAIN_PASS(boolean isFilterMan) {
        if (isFilterMan)
            return String.format(SUM_SELECT_PASS, DECLARE_SQL_PARAM, TRAIN_SELECT_FIRST, FILTER_9999, TRAIN_SELECT_LAST);
        else
            return String.format(SUM_SELECT_PASS, DECLARE_SQL_PARAM, TRAIN_SELECT_FIRST, "", TRAIN_SELECT_LAST);
    }
    public static String SUM_LOC_PASS(boolean isFilterMan) {
        if (isFilterMan)
            return String.format(SUM_SELECT_PASS, DECLARE_SQL_PARAM, LOC_SELECT_FIRST, FILTER_9999, LOC_SELECT_LAST);
        else
            return String.format(SUM_SELECT_PASS, DECLARE_SQL_PARAM, LOC_SELECT_FIRST, "", LOC_SELECT_LAST);
    }
    public static String SUM_DRIVE_PASS(boolean isFilterMan) {
        if (isFilterMan)
            return String.format(SUM_SELECT_PASS, DECLARE_SQL_PARAM, DRIV_SELECT_FIRST, FILTER_9999, DRIV_SELECT_LAST);
        else
            return String.format(SUM_SELECT_PASS, DECLARE_SQL_PARAM, LOC_SELECT_FIRST, "", LOC_SELECT_LAST);
    }

    // структура таблицы
    private  static class SumTrain {
        private int nTrain;                                 // 0 Поезд    train_num
        private int nLoc;                                   // 1 номер локомотива
        private String typeLoc;                             // 2 тип локомотива
        private String lastname;                            // 3 фамилия
        private String firstname;                           // 4 имя
        private String patronymic;                          // 5 отчество
        private int nTab;                                   // 6 таб номер
        private int nCol;                                   // 7 томер колонны
        private int cnt;                                    //  8 Всего    cnt
        private double distance;                            // 9  Пробег (км)  x_Common
        private double distanceAuto;                        // 10 Пробег в автоведении (км) x_SavpeAuto
        private double distanceAutoPerc;                    // 11 Пробег в автоведении (%)
        private double distancePrompt;                      // 12 Пробег в подсказке (км)   x_SavpePrompt
        private double distancePromptPerc;                  // 13 Пробег в подсказке (%)
        private double work;                                //  14 Работа (тКм) trWork
        private int fuel_l;                                 //  15
        private int fuel_k;                                 // 16
        private double time;                                // 17 Время    train_time
        private double av_speed;                            //  18 Скорость участковая (км/ч)
        private double av_speed_move;                       //  19 Скорость техническая (км/ч)
        private int count_TLim;                             //  20 кол временных ограничений
        private int eSum;                                   // 21
        private int eHeat;                                  //  22
        private int eHelp;                                  //  23
        private int eRecup;                                 //  24
        private int E1B1;                                   // 25
        private int E1B2;                                   // 26
        private int E2B1;                                   // 27
        private int E2B2;                                   // 28
    }

    // индексы полей запроса
    public static final int S_TRAIN_NUM = 0;            // номер поезда
    public static final int S_NUM_LOC = 1;           // номер локомотива
    public static final int S_TYPE_LOC = 2;          // тип локомотива
    public static final int S_LAST_NAME = 3;            // фамилия машиниста
    public static final int S_FIRST_NAME = 4;            // имя машиниста
    public static final int S_PATRON = 5;             // отчество машиниста
    public static final int S_TAB_NUM = 6;            // табельный номер машиниста
    public static final int S_DRV_COL = 7;           // номер колонны
    public static final int S_CNT = 8;              // всего
    public static final int S_DISTANCE = 9;         // пройденный путь
    public static final int S_DISTANCE_AUTO = 10;       // пройденный путь в автоведении
    public static final int S_DISTANCE_AUTO_PERC = 11;       // пройденный путь в автоведении
    public static final int S_DISTANCE_PROMPT = 12;       // пройдкеннй путь ы подсказке
    public static final int S_DISTANCE_PROMPT_PERC = 13;       // пройдкеннй путь ы подсказке
    public static final int S_WORK = 14;            // работа
    public static final int S_FUEL_L = 15;          // топливо л
    public static final int S_FUEL_KG = 16;          // топливо кг
    public static final int S_TIME = 17;            // время поездки
//    public static final int S_TIMEMOVE = 18;        // время поездки в движении
    public static final int S_AV_SPEED = 18;         // ср скорость
    public static final int S_AV_SPEED_MOVE = 19;     // ср скорость в движении
    public static final int S_TLIM = 20;            // всего вр огр
    public static final int S_ESUM = 21;            // ESUM
    public static final int S_EHEAT = 22;           // EHEAT
    public static final int S_EHELP = 23;           // EHELP
    public static final int S_ERECUP = 24;          // ERECUP
    public static final int S_E1B1 = 25;            // E1B1
    public static final int S_E1B2 = 26;            // E1B2
    public static final int S_E2B1 = 27;            // E2B1
    public static final int S_E2B2 = 28;            // E2B2

    // порядковый массив столбцов в таблице
    public static final int[] COLUMNS_ORDER = {
            S_TRAIN_NUM,            // 0
            S_NUM_LOC,              // 1
            S_TYPE_LOC,             // 2
            S_LAST_NAME,            // 3
            S_FIRST_NAME,           // 4
            S_PATRON,               // 5
            S_TAB_NUM,              // 6
            S_DRV_COL,              // 7
            S_CNT,                  // 8
            S_DISTANCE,             // 9
            S_DISTANCE_AUTO,        // 10
            S_DISTANCE_AUTO_PERC,   // 11
            S_DISTANCE_PROMPT,      // 12
            S_DISTANCE_PROMPT_PERC, // 13
            S_WORK,                 // 14
            S_FUEL_L,               // 15
            S_FUEL_KG,              // 16
            S_TIME,                 // 17
            S_AV_SPEED,             // 18
            S_AV_SPEED_MOVE,        // 19
            S_TLIM,                 // 20
            S_ESUM,                 // 21
            S_EHEAT,                // 22
            S_EHELP,                // 23
            S_ERECUP,               // 24
            S_E1B1,                 // 25
            S_E1B2,                 // 26
            S_E2B1,                 // 27
            S_E2B2,                 // 28
    };

    public static Object getSumVal(int col, Object sumStruct) {
        switch (col) {
            case S_TRAIN_NUM : return ((SumTrain)sumStruct).nTrain;
            case S_NUM_LOC: return ((SumTrain)sumStruct).nLoc;
            case S_TYPE_LOC: return ((SumTrain)sumStruct).typeLoc;
            case S_LAST_NAME: return ((SumTrain)sumStruct).lastname;
            case S_FIRST_NAME: return ((SumTrain)sumStruct).firstname;
            case S_PATRON: return ((SumTrain)sumStruct).patronymic;
            case S_TAB_NUM: return ((SumTrain)sumStruct).nTab;
            case S_DRV_COL: return ((SumTrain)sumStruct).nCol;
            case S_CNT: return ((SumTrain)sumStruct).cnt;
            case S_DISTANCE: return ((SumTrain)sumStruct).distance;
            case S_DISTANCE_AUTO: return ((SumTrain)sumStruct).distanceAuto;
            case S_DISTANCE_AUTO_PERC: return ((SumTrain)sumStruct).distanceAutoPerc;
            case S_DISTANCE_PROMPT: return ((SumTrain)sumStruct).distancePrompt;
            case S_DISTANCE_PROMPT_PERC: return ((SumTrain)sumStruct).distancePromptPerc;
            case S_WORK: return ((SumTrain)sumStruct).work;
            case S_FUEL_L: return ((SumTrain)sumStruct).fuel_l;
            case S_FUEL_KG: return ((SumTrain)sumStruct).fuel_k;
            case S_TIME: return ((SumTrain)sumStruct).time;
            case S_AV_SPEED: return ((SumTrain)sumStruct).av_speed;
            case S_AV_SPEED_MOVE: return ((SumTrain)sumStruct).av_speed_move;
            case S_TLIM: return ((SumTrain)sumStruct).count_TLim;
            case S_ESUM:  return ((SumTrain)sumStruct).eSum;
            case S_EHEAT: return ((SumTrain)sumStruct).eHeat;
            case S_EHELP: return ((SumTrain)sumStruct).eHelp;
            case S_ERECUP: return ((SumTrain)sumStruct).eRecup;
            case S_E1B1:  return ((SumTrain)sumStruct).E1B1;
            case S_E1B2: return ((SumTrain)sumStruct).E1B2;
            case S_E2B1: return ((SumTrain)sumStruct).E2B1;
            case S_E2B2: return ((SumTrain)sumStruct).E2B2;
            default: return null;
        }
    }

    public static Object getSumStruct(ResultSet rs) throws  SQLException {
        SumTrain sumTrain = new SumTrain();
        sumTrain.nTrain = rs.getInt(S_TRAIN_NUM + 1);
        sumTrain.nLoc = rs.getInt(S_NUM_LOC + 1);
        sumTrain.typeLoc = rs.getString(S_TYPE_LOC + 1);
        sumTrain.lastname = rs.getString(S_LAST_NAME + 1);
        sumTrain.firstname = rs.getString(S_FIRST_NAME + 1);
        sumTrain.patronymic = rs.getString(S_PATRON + 1);
        sumTrain.nTab = rs.getInt(S_TAB_NUM + 1);
        sumTrain.nCol = rs.getInt(S_DRV_COL + 1);
        sumTrain.cnt = rs.getInt(S_CNT + 1);
        sumTrain.distance = rs.getDouble(S_DISTANCE + 1);
        sumTrain.distanceAuto = rs.getDouble(S_DISTANCE_AUTO + 1);
        sumTrain.distanceAutoPerc = sumTrain.distanceAuto / sumTrain.distance * 100;
        sumTrain.distancePrompt = rs.getDouble(S_DISTANCE_PROMPT + 1);
        sumTrain.distancePromptPerc = sumTrain.distancePrompt / sumTrain.distance * 100;
        sumTrain.work = rs.getDouble(S_WORK + 1);
        sumTrain.fuel_l = rs.getInt(S_FUEL_L + 1);
        sumTrain.fuel_k =  rs.getInt(S_FUEL_KG + 1);
        sumTrain.time = rs.getDouble(S_TIME + 1) / 3600;
        sumTrain.av_speed = rs.getDouble(S_AV_SPEED + 1);
        sumTrain.av_speed_move = rs.getDouble(S_AV_SPEED_MOVE + 1);
        sumTrain.count_TLim = rs.getInt(S_TLIM + 1);
        sumTrain.eSum = rs.getInt(S_ESUM + 1);
        sumTrain.eHeat = rs.getInt(S_EHEAT + 1);
        sumTrain.eHelp = rs.getInt(S_EHELP + 1);
        sumTrain.eRecup = rs.getInt(S_ERECUP + 1);
        sumTrain.E1B1 = rs.getInt(S_E1B1 + 1);
        sumTrain.E1B2 = rs.getInt(S_E1B2 + 1);
        sumTrain.E2B1 = rs.getInt(S_E2B1 + 1);
        sumTrain.E2B2 = rs.getInt(S_E2B2 + 1);
        return sumTrain;
    }
    // свойства столбцов
    public static void setPropColSum(TableColumn column) {
        switch (column.getModelIndex()) {
            case S_TRAIN_NUM: setPropColumn(column, 70, LT + "Поезд<br>№" + RT, S_TRAIN_NUM); break;
            case S_NUM_LOC: setPropColumn(column, 70, LT + "Локомотив<br>№" + RT, S_NUM_LOC); break;
            case S_TYPE_LOC: setPropColumn(column, 70, LT + "Серия<br>локомотива" + RT, S_TYPE_LOC); break;
            case S_LAST_NAME: setPropColumn(column, 100, LT + "Фамилия" + RT, S_LAST_NAME); break;
            case S_FIRST_NAME: setPropColumn(column, 100, LT + "Имя" + RT, S_FIRST_NAME); break;
            case S_PATRON: setPropColumn(column, 100, LT + "Отчество" + RT, S_PATRON); break;
            case S_TAB_NUM: setPropColumn(column, 70, LT + "Табельный<br>номер" + RT, S_TAB_NUM); break;
            case S_DRV_COL: setPropColumn(column, 70, LT + "Колонна" + RT, S_DRV_COL); break;
            case S_CNT: setPropColumn(column, 50,  LT + "Всего<br>поездок" + RT, S_CNT); break; // ---
            case S_DISTANCE: setPropColumn(column, 80, LT + "Пробег<br>(км)" + RT, S_DISTANCE); break;
            case S_DISTANCE_AUTO: setPropColumn(column, 80, LT + "Пробег в<br>автовед<br>(км)" + RT, S_DISTANCE_AUTO); break;
            case S_DISTANCE_AUTO_PERC: setPropColumn(column, 80, LT + "Пробег в<br>автовед<>(%)" + RT, S_DISTANCE_AUTO_PERC); break;
            case S_DISTANCE_PROMPT: setPropColumn(column, 80, LT + "Пробег в<br>подсказке<br>(км)" + RT, S_DISTANCE_PROMPT); break;
            case S_DISTANCE_PROMPT_PERC: setPropColumn(column, 80, LT + "Пробег в<br>подсказке<br>(%)" + RT, S_DISTANCE_PROMPT_PERC); break;
            case S_WORK: setPropColumn(column, 80, LT + "Работа<br>(тКм)" + RT, S_WORK); break;
            case S_FUEL_L: setPropColumn(column, 80, LT + "Топливо<br>(л)" + RT, S_FUEL_L); break;
            case S_FUEL_KG: setPropColumn(column, 80, LT + "Топливо<br>(кг)" + RT, S_FUEL_KG); break;
            case S_TIME: setPropColumn(column, 80, LT + "Время<br>(ч)" + RT, S_TIME); break;
//            case S_TIMEMOVE: setPropColumn(column, 80, LT + "Время в<br>движении" + RT, S_TIMEMOVE); break;
            case S_AV_SPEED: setPropColumn(column, 80, LT + "Скорость<br>участковая<br>(км/ч)" + RT, S_AV_SPEED); break;
            case S_AV_SPEED_MOVE: setPropColumn(column, 80, LT + "Скорость<br>техническая<br>(км/ч)" + RT, S_AV_SPEED_MOVE); break;
            case S_TLIM: setPropColumn(column, 80, LT + "Всего<br>временных<br>ограничений" + RT, S_TLIM); break;
            case S_ESUM: setPropColumn(column, 80, LT + "Суммарная<br>энергия<br>(кВтч)" + RT, S_ESUM); break;     // eSum
            case S_EHEAT: setPropColumn(column, 80, LT + "Энергия на<br>отопление<br>(кВтч)" + RT, S_EHEAT); break;    // eHeat
            case S_EHELP: setPropColumn(column, 80, LT + "Энергия на<br>тягу<br>(кВтч)" + RT, S_EHELP); break;    // eHelp
            case S_ERECUP: setPropColumn(column, 80, LT + "Рекуперация<br>(кВтч)" + RT, S_ERECUP); break;   // eRecup
            case S_E1B1: setPropColumn(column, 80, LT + "Суммарная<br>энергия<br>(~ кВт*ч)" + RT, S_E1B1); break;     // E1B1
            case S_E1B2: setPropColumn(column, 80, LT + "Энергия на<br>тягу<br>(~ кВт*ч)" + RT, S_E1B2); break;     // E1B2
            case S_E2B1: setPropColumn(column, 80, LT + "Энергия на<br>отопление<br>(~ кВт*ч)" + RT, S_E2B1); break;     // E2B1
            case S_E2B2: setPropColumn(column, 80, LT + "Энергия<br>реккуперации<br>(~ кВт*ч)" + RT, S_E2B2); break;     // E2B2
            default: setPropColumn(column, 70, "Unknown", -1);
        }
    }
}
