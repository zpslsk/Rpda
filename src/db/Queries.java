//        -------------------- параметры -------------------------------------------
//        DECLARE @date_start VARCHAR(8);
//        DECLARE @date_finish VARCHAR(8);
//        DECLARE @loc_type INT;
//        DECLARE @drvCol INT;
//        SET @date_start = '20160101';	-- 1 дата старт
//        SET @date_finish = '20171231';	-- 2 дата финиш
//        SET @loc_type = -1;				-- 3 тип локомотива
//        SET @drvCol = -1;				-- 4 колонна
//        DECLARE @train_num INT;
//        DECLARE @loc_num INT;
//        DECLARE @tb_num INT;
//        SET @train_num = -1;			-- 5 номер поезда
//        SET @loc_num = -1;				-- 6 номер локомотива
//        SET @tb_num = -1;				-- 7 табельный номер
//        --------------------------- суммарная ------------------------------------
//        SELECT
//
//        train_num, '', '',	'', '',					-- 1 поезд
//        --			loc_num, type_name, '',	'',	'',				-- 1 локомотив, 2 тип локомотива
//        --			surname, name, patronymic, tb_num, drvCol,	-- 1 фамилия, 2 имя, 3 отчество, 4 таб. номер, 5 колонна
//
//        COUNT(*) AS cnt,							-- 6 всего
//        SUM(x_Common) AS x_Common,					-- 7 пробег
//        SUM(x_SavpeAuto) AS x_SavpeAuto,			-- 8 пробег автоведение
//        SUM(x_SavpePrompt) AS x_SavpePrompt,		-- 9 пробег подсказка
//        SUM(trWork) AS trWork,						-- 10 работа
//        SUM(TFuel_kg) AS TFuel_kg,					-- 11 топливо кг
//        SUM(TFuel_l) AS TFuel_l,					-- 12 топливо л
//        SUM(train_time) AS train_time,				-- 13 время
//        SUM(train_time_move) AS train_time_move,	-- 14 время в движении
//        AVG(av_speed) AS av_speed,					-- 15 средняя участковая скорость
//        AVG(av_speed_move) AS av_speed_move,		-- 16 средняя техническая скорость
//        SUM(countTLim) AS countTLim,				-- 17 всего временных ограничений
//        SUM(ESum) AS ESum,							-- 18 ESum
//        SUM(EHeat) AS EHeat,						-- 19 EHeat
//        SUM(EHelp) AS EHelp,						-- 20 EHelp
//        SUM(ERecup) AS ERecup,						-- 21 ERecup
//        SUM(E1B1) AS E1B1,							-- 22 E1B1
//        SUM(E1B2) AS E1B2,							-- 23 E1B2
//        SUM(E2B1) AS E2B1,							-- 23 E2B1
//        SUM(E2B2) AS E2B2							-- 24 E2B2
//        FROM
//        m_Trains tr
//        INNER JOIN m_TrainsInfo ti ON ti.train_id = tr.train_id
//        INNER JOIN ci_Locomotiv loc ON loc.loc_id = ti.loc_id
//        INNER JOIN ci_LocType lt ON lt.type_id = loc.loc_type  AND ((loc.loc_type = @loc_type) or (@loc_type < 0))
//        INNER JOIN ci_Drivers dr ON dr.drv_id = tr.drv_id AND ((dr.DrvCol = @drvCol) OR (@drvCol < 0))
//        LEFT JOIN m_XFiles xf ON xf.id_image = tr.image_id
//        WHERE
//        dateTr BETWEEN @date_start AND @date_finish
//
//	     GROUP BY train_num ORDER BY train_num;								-- поезд
//            --			GROUP BY loc_num, type_name ORDER BY loc_num, type_name;			-- локомотив
//            --	        GROUP BY surname, name, patronymic, tb_num, drvCol ORDER BY surname	-- машинист
//
//        ---------------- детальная -----------------------------
//        SELECT
//        train_num, dateTr,								-- 1 поезд, 2 дата поездки
//        tb_num, surname, name, patronymic,				-- 3 таб номер, 4 фамилия, 5 имя 6 отчество
//        type_name, loc_num,								-- 7 тип локомотива, 8 номер локомотива
//        x_Common, x_SavpeAuto, x_SavpePrompt,			-- 9 пробег, 10 пробег в автоведении, 11 пробег в подсказке
//        DrawSpec,										-- 12 DrawSpec
//        trWork, train_time,								-- 13 работа, 14 время поезда
//        TFuel_l, TFuel_kg,								-- 15 топливо л, 16 топливо кг
//        ESum, EHelp, EHeat, ERecup, EHelp1, EHelp2,		-- 17 ESum, 18 EHelp, 19 EHeat, 20 ERecup, 21 EHelp1, 22 EHelp2
//        E1B1, E1B2, E2B1, E2B2, Edop1, Edop2,			-- 23 E1B1, 24 E1B2, 25 E2B1, 26 E2B2, 27 Edop1, 28 Edop2,
//        av_speed,										-- 29 скорость участковая
//        av_speed_move,									-- 30 скорость техническая
//        countTLim, Weight, VagsCount,					-- 31 всего вр огр, 32 всего вагонов
//        t.NameShoulder, stName, type_code, FileName		-- 33 имя перегона, 34 имя станции, 35 имя файла
//        FROM m_Trains t
//        inner join m_TrainsInfo ON m_TrainsInfo.train_id = t.train_id AND (t.train_num = @train_num OR @train_num < 0)
//        inner join ci_Drivers ON ci_Drivers.drv_id = t.drv_id AND (ci_Drivers.tb_num = @tb_num OR @tb_num < 0)
//        inner join ci_Locomotiv ON ci_Locomotiv.loc_id = m_TrainsInfo.loc_id AND (ci_Locomotiv.loc_num = @loc_num OR @loc_num < 0)
//        inner join ci_LocType ON ci_LocType.type_id = ci_Locomotiv.loc_type AND ((ci_LocType.type_id = @loc_type) OR (@loc_type < 0))
//        left  join m_XFiles ON m_XFiles.id_image= t.image_id
//        WHERE dateTr BETWEEN @date_start AND @date_finish
//        ORDER BY dateTr

package db;

import javax.swing.table.TableColumn;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Queries {

    public static final String LT = "<html><center>";
    public static final String RT = "</center></html>";
    public static final DateTimeFormatter formatter_in = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
    public static final String FILTER_9999 = "AND tr.train_num <> 9999";

    public static final String DECLARE_SQL_PARAM =
            "DECLARE @date_start VARCHAR(8);\n" +
            "DECLARE @date_finish VARCHAR(8);\n" +
            "DECLARE @train_num INT;\n" +
            "DECLARE @loc_num INT;\n" +
            "DECLARE @loc_type INT;\n" +
            "DECLARE @drvCol INT;\n" +
            "DECLARE @tb_num INT;\n" +
            "SET @date_start = ?;\n" +
            "SET @date_finish = ?;\n" +
            "SET @loc_type = ?;\n" +
            "SET @train_num = ?;\n" +
            "SET @loc_num = ?;\n" +
            "SET @drvCol = ?;\n " +
            "SET @tb_num = ?;\n";

    // индексы параметров
    public static final int PAR_DATESTART = 1;
    public static final int PAR_DATEFINISH = 2;
    public static final int PAR_LOCTYPE = 3;
    public static final int PAR_TRNUM = 4;
    public static final int PAR_LOCNUM = 5;
    public static final int PAR_DRCOL = 6;
    public static final int PAR_TBNUM = 7;

// ---------------------------------------------------------------------------------------------------------------------
    public static void setPropColumn(TableColumn column, int width, String header, Integer identifier) {
        column.setMinWidth(width / 2);
        column .setMaxWidth(width * 2);
        column.setPreferredWidth(width);
        column.setHeaderValue(header);
        column.setIdentifier(identifier);
    }

    public static class Params {
        private int rowIndex = 0;
        private LocalDate dStart;       //
        private LocalDate dFinish;      //
        private int typeTrain = -1;     //
        private int drvCol = -1;        //
        private int nTrain = -1;        //
        private int nLoc = -1;          //
        private int tabNum = -1;        //
        private int nTch = -1;
        private int nRoad = -1;

        public Params(LocalDate dStart, LocalDate dFinish, int typeTrain, int nTch, int nRoad) {
            this.dStart = dStart;
            this.dFinish = dFinish;
            this.typeTrain = typeTrain;
            this.nTch = nTch;
            this.nRoad = nRoad;
        }
        public String getDateStartString() {
            return String.format("%02d%02d%02d", dStart.getYear(), dStart.getMonthValue(), dStart.getDayOfMonth());
        }
        public String getDateFinishString() {
            return String.format("%02d%02d%02d", dFinish.getYear(), dFinish.getMonthValue(), dFinish.getDayOfMonth());
        }
        public int getRowIndex() {return rowIndex;}
        public void setRowIndex(int value) {rowIndex = value;}
        public LocalDate getDateStart() {return dStart;}
        public void setDateStart(LocalDate date) {dStart = date;}
        public LocalDate getDateFinish() {return dFinish;}
        public void setDateFinish(LocalDate date) {dFinish = date;}
        public int getTypeTrain() {return typeTrain;}
        public void setTypeTrain(int val) {typeTrain = val;}
        public int getDrvCol() {return drvCol;}
        public void setDrvCol(int val) {drvCol = val;}
        public int getNumTrain() {return nTrain;}
        public void setNumTrain(int val) {nTrain = val;}
        public int getNumLoc() {return nLoc;}
        public void setNumLoc(int val) {nLoc = val;}
        public int getTabNum() {return tabNum;}
        public void setTabNum(int val) {tabNum = val;}
        public int getNumTch() {return nTch;}
        public void setNumTch(int val) {nTch = val;}
        public int getNumRoad() {return nRoad;}
        public void setNumRoad(int val) {nRoad = val;}
    }
}
