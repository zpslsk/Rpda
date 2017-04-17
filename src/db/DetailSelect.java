package db;

import javax.swing.table.TableColumn;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import static db.Queries.*;

public class DetailSelect {

    private static final String DET_PASS =
            "USE RPDAP\n" +
                    db.Queries.DECLARE_SQL_PARAM +
                    "SELECT\n" +
                    "train_num, dateTr, -- 1 поезд, 2 дата поездки\n" +
                    "tb_num, surname, name, patronymic, -- 3 таб номер, 4 фамилия, 5 имя 6 отчество\n" +
                    "type_name, loc_num, -- 7 тип локомотива, 8 номер локомотива\n" +
                    "x_Common, x_SavpeAuto, 1.1, x_SavpePrompt, 1.1, -- 9 пробег, 10 пробег в автоведении, 11,  12 пробег в подсказке, 13\n" +
                    "DrawSpec, -- 14 DrawSpec\n" +
                    "trWork, train_time, -- 15 работа, 16 время поезда\n" +
                    "TFuel_l, TFuel_kg, -- 17 топливо л, 18 топливо кг\n" +
                    "ESum, EHelp, EHeat, ERecup, EHelp1, EHelp2, -- 19 ESum, 20 EHelp, 21 EHeat, 22 ERecup, 23 EHelp1, 24 EHelp2\n" +
                    "E1B1, E1B2, E2B1, E2B2, Edop1, Edop2, -- 25 E1B1, 26 E1B2, 27 E2B1, 28 E2B2, 29 Edop1, 30 Edop2,\n" +
                    "av_speed, -- 31 скорость участковая\n" +
                    "av_speed_move, -- 32 скорость техническая\n" +
                    "countTLim, Weight, VagsCount, -- 31 всего вр огр, 32 вес, 33 всего вагонов\n" +
                    "tr.NameShoulder, stName, FileName -- 34 имя маршрута 35 имя станции, 36 имя файла\n" +
                    "FROM m_Trains tr\n" +
                    "inner join m_TrainsInfo ON m_TrainsInfo.train_id = tr.train_id %s AND (tr.train_num = @train_num OR @train_num < 0)\n" +
                    "inner join ci_Drivers ON ci_Drivers.drv_id = tr.drv_id AND (ci_Drivers.tb_num = @tb_num OR @tb_num < 0)\n" +
                    "inner join ci_Locomotiv ON ci_Locomotiv.loc_id = m_TrainsInfo.loc_id AND (ci_Locomotiv.loc_num = @loc_num OR @loc_num < 0)\n" +
                    "inner join ci_LocType ON ci_LocType.type_id = ci_Locomotiv.loc_type AND ((ci_LocType.type_id = @loc_type) OR (@loc_type < 0))\n" +
                    "left  join m_XFiles ON m_XFiles.id_image= tr.image_id\n" +
                    "WHERE dateTr BETWEEN @date_start AND @date_finish\n" +
                    "ORDER BY dateTr";

    public static String DET_SELECT_PASS(boolean isFilterMan) {
        if (isFilterMan)
            return String.format(DET_PASS, FILTER_9999);
        else
            return String.format(DET_PASS, "");
    }

    // индексы полей запроса
    public static final int D_TRAIN_NUM = 0;
    public static final int D_DATE = 1;
    public static final int D_TAB_NUM = 2;            // табельный номер машиниста
    public static final int D_LAST_NAME = 3;            // фамилия машиниста
    public static final int D_FIRST_NAME = 4;            // имя машиниста
    public static final int D_PATRON = 5;             // отчество машиниста
    public static final int D_TYPE_LOC = 6;          // тип локомотива
    public static final int D_NUM_LOC = 7;           // номер локомотива
    public static final int D_DISTANCE = 8;         // пройденный путь
    public static final int D_DISTANCE_AUTO = 9;       // пройденный путь в автоведении
    public static final int D_DISTANCE_AUTO_PERC = 10;       // пройденный путь в автоведении
    public static final int D_DISTANCE_PROMPT = 11;       // пройдкеннй путь ы подсказке
    public static final int D_DISTANCE_PROMPT_PERC = 12;       // пройдкеннй путь ы подсказке
    public static final int D_DRAWSPEC = 13;
    public static final int D_WORK = 14;            // работа
    public static final int D_TIME = 15;            // время поездки
    public static final int D_FUEL_L = 16;          // топливо л
    public static final int D_FUEL_KG = 17;          // топливо кг
    public static final int D_ESUM = 18;            // ESUM
    public static final int D_EHELP = 19;           // EHELP
    public static final int D_EHEAT = 20;           // EHEAT
    public static final int D_ERECUP = 21;          // ERECUP
    public static final int D_EHELP1 = 22;          // EHELP1
    public static final int D_EHELP2 = 23;          // EHELP2
    public static final int D_E1B1 = 24;            // E1B1
    public static final int D_E1B2 = 25;            // E1B2
    public static final int D_E2B1 = 26;            // E2B1
    public static final int D_E2B2 = 27;            // E2B2
    public static final int D_EDOP1 = 28;            // E2B1
    public static final int D_EDOP2 = 29;            // E2B2
    public static final int D_AV_SPEED = 30;         // ср скорость участковая
    public static final int D_AV_SPEED_MOVE = 31;     // ср скорость техническая
    public static final int D_TLIM = 32;            // всего вр огр
    public static final int D_WEIGHT = 33;            // вес
    public static final int D_NUM_VAGS = 34;            // всего вагонов
    public static final int D_ROUTE_NAME = 35;           // имя перегона
    public static final int D_NAME = 36;           // имя перегона
    public static final int D_NAMEFILE = 37;          // имя файла

    // порядковый массив столбцов в таблице
    public static final int[] COLUMNS_ORDER = {
            D_TRAIN_NUM,            // 0
            D_DATE,                 // 1
            D_ROUTE_NAME,           // 35
            D_TAB_NUM,              // 2
            D_LAST_NAME,            // 3
            D_FIRST_NAME,           // 4
            D_PATRON,               // 5
            D_TYPE_LOC,             // 6
            D_NUM_LOC,              // 7
            D_DISTANCE,             // 8
            D_DISTANCE_AUTO,        // 9
            D_DISTANCE_AUTO_PERC,   // 10
            D_DISTANCE_PROMPT,      // 11
            D_DISTANCE_PROMPT_PERC, // 12
            D_DRAWSPEC,             // 13
            D_WORK,                 // 14
            D_TIME,                 // 15
            D_FUEL_L,               // 16
            D_FUEL_KG,              // 17
            D_ESUM,                 // 18
            D_EHELP,                // 19
            D_EHEAT,                // 20
            D_ERECUP,               // 21
            D_EHELP1,               // 22
            D_EHELP2,               // 23
            D_E1B1,                 // 24
            D_E1B2,                 // 25
            D_E2B1,                 // 26
            D_E2B2,                 // 27
            D_EDOP1,                // 28
            D_EDOP2,                // 29
            D_AV_SPEED,             // 30
            D_AV_SPEED_MOVE,        // 31
            D_TLIM,                 // 32
            D_WEIGHT,               // 33
            D_NUM_VAGS,             // 34
            D_NAME,                 // 36
            D_NAMEFILE              // 37
    };

    // структура таблицы
    private static class DetTrain {
        private int nTrain;                             // 0
        private LocalDateTime dateTr;                   // 1
        private int nTab;                               // 2
        private String lastname;                        // 3
        private String firstname;                       // 4
        private String patronymic;                      // 5
        private String locType;                         // 6
        private int nLoc;                               // 7
        private double distance;                        // 8 Пробег (км)  x_Common
        private double distanceAuto;                    // 9 Пробег в автоведении (км) x_SavpeAuto
        private double distanceAutoPerc;                 // 10 Пробег в автоведении (%)
        private double distancePrompt;                  // 11 Пробег в подсказке (км)   x_SavpePrompt
        private double distancePromptPerc;              // 12 Пробег в подсказке (%)
        private double unitConsump;                       // 13 удельный расход
        private double work;                           // 14 Работа (тКм) trWork
        private double time;                             // 15 Время    train_time
        private int fuel_l;                              // 16
        private int fuel_kg;                              // 17
        private int eSum;                                // 18
        private int eHelp;                               // 19
        private int eHeat;                               // 20
        private int eRecup;                                 // 21
        private int eHelp1;                                 // 22
        private int eHelp2;                                 // 23
        private int E1B1;                                   // 24
        private int E1B2;                                   // 25
        private int E2B1;                                   // 26
        private int E2B2;                                   // 27
        private int Edop1;                                  // 28
        private int Edop2;                                  // 29
        private double av_speed;                            // 30  Скорость участковая (км/ч)
        private double av_speed_move;                       // 31  Скорость техническая (км/ч)
        private int count_TLim;                           // 32 кол временных ограничений
        private int weight;                               // 33
        private int numVags;                              // 34
        private String nameRoute;                           // 35
        private String name;                                // 36
        private String nameFile;                            // 37
    }
    // значения строки таблицы из структуры DetTrain
    public static Object getDetVal(int col, Object detStruct) {
        switch (col) {
            case D_TRAIN_NUM: return ((DetTrain)detStruct).nTrain;
            case D_DATE: return ((DetTrain)detStruct).dateTr;
            case D_FIRST_NAME: return ((DetTrain)detStruct).firstname;
            case D_LAST_NAME: return ((DetTrain)detStruct).lastname;
            case D_PATRON: return ((DetTrain)detStruct).patronymic;
            case D_TAB_NUM: return ((DetTrain)detStruct).nTab;
            case D_TYPE_LOC: return ((DetTrain)detStruct).locType;
            case D_NUM_LOC: return ((DetTrain)detStruct).nLoc;
            case D_DISTANCE: return ((DetTrain)detStruct).distance;
            case D_DISTANCE_AUTO: return ((DetTrain)detStruct).distanceAuto;
            case D_DISTANCE_AUTO_PERC: return ((DetTrain)detStruct).distanceAutoPerc;
            case D_DISTANCE_PROMPT: return ((DetTrain)detStruct).distancePrompt;
            case D_DISTANCE_PROMPT_PERC: return ((DetTrain)detStruct).distancePromptPerc;
            case D_DRAWSPEC: return ((DetTrain)detStruct).unitConsump;
            case D_WORK: return ((DetTrain)detStruct).work;
            case D_TIME: return ((DetTrain)detStruct).time;
            case D_FUEL_L: return ((DetTrain)detStruct).fuel_l;
            case D_FUEL_KG: return ((DetTrain)detStruct).fuel_kg;
            case D_ESUM: return ((DetTrain)detStruct).eSum;
            case D_EHELP: return ((DetTrain)detStruct).eHelp;
            case D_EHEAT: return ((DetTrain)detStruct).eHeat;
            case D_ERECUP: return ((DetTrain)detStruct).eRecup;
            case D_EHELP1: return ((DetTrain)detStruct).eHelp1;
            case D_EHELP2: return ((DetTrain)detStruct).eHelp2;
            case D_E1B1:  return ((DetTrain)detStruct).E1B1;
            case D_E1B2: return ((DetTrain)detStruct).E1B2;
            case D_E2B1: return ((DetTrain)detStruct).E2B1;
            case D_E2B2: return ((DetTrain)detStruct).E2B2;
            case D_EDOP1:  return ((DetTrain)detStruct).Edop1;
            case D_EDOP2:  return ((DetTrain)detStruct).Edop2;
            case D_AV_SPEED: return ((DetTrain)detStruct).av_speed;
            case D_AV_SPEED_MOVE: return ((DetTrain)detStruct).av_speed_move;
            case D_TLIM: return ((DetTrain)detStruct).count_TLim;
            case D_WEIGHT: return ((DetTrain)detStruct).weight;
            case D_NUM_VAGS: return ((DetTrain)detStruct).numVags;
            case D_ROUTE_NAME: return ((DetTrain)detStruct).nameRoute;
            case D_NAME: return  ((DetTrain)detStruct).name;
            case D_NAMEFILE: return  ((DetTrain)detStruct).nameFile;
            default: return null;
        }
    }
    // заполнене структуры DetTrain из запроса
    public static Object getDetStruct(ResultSet rs) throws SQLException {
        DetTrain detTrain = new DetTrain();
        detTrain.nTrain = rs.getInt(D_TRAIN_NUM + 1);
        detTrain.dateTr = LocalDateTime.parse(rs.getString(D_DATE + 1), formatter_in);
        detTrain.lastname = rs.getString(D_LAST_NAME + 1);
        detTrain.firstname = rs.getString(D_FIRST_NAME + 1);
        detTrain.patronymic = rs.getString(D_PATRON + 1);
        detTrain.nTab = rs.getInt(D_TAB_NUM + 1);
        detTrain.locType = rs.getString(D_TYPE_LOC + 1);
        detTrain.nLoc = rs.getInt(D_NUM_LOC + 1);
        detTrain.distance = rs.getDouble(D_DISTANCE + 1);
        detTrain.distanceAuto = rs.getDouble(D_DISTANCE_AUTO + 1);
        detTrain.distanceAutoPerc = detTrain.distanceAuto / detTrain.distance * 100;
        detTrain.distancePrompt = rs.getDouble(D_DISTANCE_PROMPT + 1);
        detTrain.distancePromptPerc = detTrain.distancePrompt / detTrain.distance * 100;
        detTrain.unitConsump = rs.getDouble(D_DRAWSPEC + 1);
        detTrain.work = rs.getDouble(D_WORK + 1);
        detTrain.time = rs.getDouble(D_TIME + 1) / 3600;
        detTrain.fuel_l = rs.getInt(D_FUEL_L + 1);
        detTrain.fuel_kg = rs.getInt(D_FUEL_KG + 1);
        detTrain.eSum = rs.getInt(D_ESUM + 1);
        detTrain.eHelp = rs.getInt(D_EHELP + 1);
        detTrain.eHeat = rs.getInt(D_EHEAT + 1);
        detTrain.eRecup = rs.getInt(D_ERECUP + 1);
        detTrain.eHelp1 = rs.getInt(D_EHELP1 + 1);
        detTrain.eHelp2 = rs.getInt(D_EHELP2 + 1);
        detTrain.E1B1 = rs.getInt(D_E1B1 + 1);
        detTrain.E1B2 = rs.getInt(D_E1B2 + 1);
        detTrain.E2B1 = rs.getInt(D_E2B1 + 1);
        detTrain.E2B2 = rs.getInt(D_E2B2 + 1);
        detTrain.Edop1 = rs.getInt(D_EDOP1 + 1);
        detTrain.Edop2 = rs.getInt(D_EDOP2 + 1);
        detTrain.av_speed = rs.getDouble(D_AV_SPEED + 1);
        detTrain.av_speed_move = rs.getDouble(D_AV_SPEED_MOVE + 1);
        detTrain.count_TLim = rs.getInt(D_TLIM + 1);
        detTrain.weight = rs.getInt(D_WEIGHT + 1);
        detTrain.numVags = rs.getInt(D_NUM_VAGS + 1);
        detTrain.nameRoute = rs.getString(D_ROUTE_NAME + 1);
        detTrain.name = rs.getString(D_NAME + 1);
        detTrain.nameFile = rs.getString(D_NAMEFILE + 1);
        return detTrain;
    }
    // свойства столбцов
    public static void setPropColDet(TableColumn column) {
        switch (column.getModelIndex()) {
            case D_TRAIN_NUM: setPropColumn(column, 50, LT + "Поезд<br>№" + RT, D_TRAIN_NUM); break;
            case D_DATE: setPropColumn(column, 150, LT + "Дата" + RT, D_DATE); break;
            case D_LAST_NAME: setPropColumn(column, 100, LT + "Фамилия" + RT, D_LAST_NAME); break;
            case D_FIRST_NAME: setPropColumn(column, 100, LT + "Имя" + RT, D_FIRST_NAME); break;
            case D_PATRON: setPropColumn(column, 100, LT + "Отчество" + RT, D_PATRON); break;
            case D_TAB_NUM: setPropColumn(column, 80, LT + "Табельный<br>номер" + RT, D_TAB_NUM); break;
            case D_TYPE_LOC: setPropColumn(column, 80, LT + "Серия<br>локомотива" + RT, D_TYPE_LOC); break;
            case D_NUM_LOC: setPropColumn(column, 80, LT + "Номер<br>локомотива" + RT, D_NUM_LOC); break;
            case D_DISTANCE: setPropColumn(column, 80, LT + "Пробег<br>общий<br>(км)" + RT, D_DISTANCE); break;
            case D_DISTANCE_AUTO: setPropColumn(column, 80, LT + "Пробег в<br>автовед<br>(км)" + RT, D_DISTANCE_AUTO); break;
            case D_DISTANCE_AUTO_PERC: setPropColumn(column, 80, LT + "Пробег в<br>автовед<br>(%)" + RT, D_DISTANCE_AUTO_PERC); break;
            case D_DISTANCE_PROMPT: setPropColumn(column, 80, LT + "Пробег в<br>подсказке<br>(км)" + RT, D_DISTANCE_PROMPT); break;
            case D_DISTANCE_PROMPT_PERC: setPropColumn(column, 80, LT + "Пробег в<br>подсказке<br>(%)" + RT, D_DISTANCE_PROMPT_PERC); break;
            case D_DRAWSPEC: setPropColumn(column, 80, LT + "Удельный<br>расход" + RT, D_DRAWSPEC); break;
            case D_WORK: setPropColumn(column, 80, LT + "Работа<br>(тКм)" + RT, D_WORK); break;
            case D_TIME: setPropColumn(column, 80, LT + "Время<br>(ч)" + RT, D_TIME); break;
            case D_FUEL_L: setPropColumn(column, 80, LT + "Топливо<br>(л)" + RT, D_FUEL_L); break;
            case D_FUEL_KG: setPropColumn(column, 80, LT + "Топливо<br>(кг)" + RT, D_FUEL_KG); break;
            case D_ESUM: setPropColumn(column, 80, LT + "Суммарная<br>энергия<br>(кВт*ч)" + RT, D_ESUM); break;
            case D_EHELP: setPropColumn(column, 80, LT + "Энергия на<br>тягу<br>(кВт*ч)" + RT, D_EHELP); break;
            case D_EHEAT: setPropColumn(column, 80, LT + "Энергия на<br>отопление<br>(кВт*ч)" + RT, D_EHEAT); break;
            case D_ERECUP: setPropColumn(column, 80, LT + "Энергия<br>реккуперации<br>(кВт*ч)" + RT, D_ERECUP); break;
            case D_EHELP1: setPropColumn(column, 80, LT + "D_EHELP1" + RT, D_EHELP1); break;
            case D_EHELP2: setPropColumn(column, 80, LT + "D_EHELP2" + RT, D_EHELP2); break;
            case D_E1B1: setPropColumn(column, 80, LT + "Суммарная<br>энергия<br>(~ кВт*ч)" + RT, D_E1B1); break;     // E1B1
            case D_E1B2: setPropColumn(column, 80, LT + "Энергия на<br>тягу<br>(~ кВт*ч)" + RT, D_E1B2); break;     // E1B2
            case D_E2B1: setPropColumn(column, 80, LT + "Энергия на<br>отопление<br>(~ кВт*ч)" + RT, D_E2B1); break;     // E2B1
            case D_E2B2: setPropColumn(column, 80, LT + "Энергия<br>реккуперации<br>(кВт*ч)" + RT, D_E2B2); break;     // E2B2
            case D_EDOP1: setPropColumn(column, 80, LT + "D_EDOP1" + RT, D_EDOP1); break;     // E2B1
            case D_EDOP2: setPropColumn(column, 80, LT + "D_EDOP2" + RT, D_EDOP2); break;     // E2B2
            case D_AV_SPEED: setPropColumn(column, 80, LT + "Скорость<br>участковая<br>(км/ч)" + RT, D_AV_SPEED); break;
            case D_AV_SPEED_MOVE: setPropColumn(column, 80, LT + "Скорость<br>техническая<br>(км/ч)" + RT, D_AV_SPEED_MOVE); break;
            case D_TLIM: setPropColumn(column, 80, LT + "Всего<br>временных<br>ограничений" + RT, D_TLIM); break;
            case D_WEIGHT: setPropColumn(column, 80, LT + "Вес<br>(т)" + RT, D_WEIGHT); break;
            case D_NUM_VAGS: setPropColumn(column, 80, LT + "Всего<br>вагонов" + RT, D_NUM_VAGS); break;
            case D_ROUTE_NAME: setPropColumn(column, 250, LT + "Маршрут" + RT, D_ROUTE_NAME); break;
            case D_NAME: setPropColumn(column, 250, LT + "D_NAME" + RT, D_NAME); break;
            case D_NAMEFILE: setPropColumn(column, 400, LT + "Файл поездки" + RT, D_NAMEFILE); break;
            default: setPropColumn(column, 50, "Unknow", -1);
        }
    }
}
