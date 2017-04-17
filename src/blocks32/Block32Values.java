package blocks32;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;

public class Block32Values {

    private Block32 block32;
    private static int predT , T;
    private static boolean isStartTime = true;

    public Block32Values(Block32 block32) {
        this.block32 = block32;
    }

    // индексы структуры
    public static final int V_XCOORD = 0;            //
    public static final int V_XTIME = 1;            //
    public static final int V_LOST = 2;
    public static final int V_DATE = 3;         //
    public static final int V_TIME = 4;         //
    public static final int V_NTRAIN = 5;         //
    public static final int V_DBAND = 6;         //

    public static void resetStartTime() {
        isStartTime = true;
    }

    public void fillValStruct(Values values) {
        values.xCoord = 0;
        values.xTime = getSecond();
        values.lOst = getLOst();
        values.date = getDate();
        values.time = getTime();
        values.nTrain = getNumTrain();
        values.dBand = getBandage();
    }

    // Seconds  V_XTIME
    private int getSecond() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas) {
                    int curT =  block32.get(23) & 0x3F;
                    if (curT < predT)
                        predT = predT - 60;
                    if (isStartTime) {
                        T = 0;
                        isStartTime = false;
                    } else
                        T += curT - predT;
                    predT = block32.get(23) & 0x3F;
                    return T;
                }

            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return 0;
        }
        return 0;
    }
    // V_LOST pass
    private int getLOst() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return ((block32.get(0) & 0x1F) << 16) + (block32.get(1) << 8) + block32.get(2);
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return 0;
        }
        return 0;
    }
    // дата V_DATE
    private  LocalDate getDate() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                int year = (block32.get(26) >>> 2) + 2000;
                int month = ((block32.get(26) & 0x03) << 2) + (block32.get(25) >>> 6);
                int day = (block32.get(25) >>> 1) & 0x1F;
                try {
                    return LocalDate.of(year, month, day);
                } catch(Exception e) {
                    return null;
                }
            case Freight: return null;
        }
        return null;
    }
    // время V_TIME
    private  LocalTime getTime() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                int hour = (block32.get(24) >>> 4) + ((block32.get(25) & 0x01) << 4);
                int min = (block32.get(23) >>> 6) + ((block32.get(24) & 0x0F) << 2);
                int sec = block32.get(23) & 0x3F;
                try {
                    return LocalTime.of(hour, min, sec);
                } catch(Exception e) {
                    return null;
                }
            case Freight: return null;
        }
        return null;
    }
    // номер поезда V_NTRAIN
    private int getNumTrain() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return block32.get(3) + (block32.get(4) << 8);
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }
    // диаметр бандажа
    private int getBandage() {
        final int D_BAND = 1100;
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return block32.get(5) + D_BAND;
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }
    // CmdState 0 – автоведение 1 – советчик 2 – безопасность 3 – промежуточный
    private int getCmdState() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return block32.get(6) >>> 6;
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }
    // VmaxType Тип тек огр скор 0-пост 1–вр 2–оперативн(задано машинистом) 3–задано САУТ 4–задано КЛУБ-У 5..7 - резерв
    private int getVmaxType() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return block32.get(6) & 0x07;
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }
    // табельный номер
    private int getNumTab() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return block32.get(7) + (block32.get(8) << 8) + ((block32.get(22) & 0x40) << 10);
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }
    // км ближ вр огр
    private int getTLimKm() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return block32.get(9) + (block32.get(10) << 8);
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }
    // пикет ближ вр огр
    private int getTLimPk() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return block32.get(11);
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }
    // величина ближ вр огр
    private int getTLim() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return block32.get(12);
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }
    // текущее ограничение скорости
    private int getCurLim() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return block32.get(13);
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }
    // текущяя скорость
    private int getSpeed() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return block32.get(14);
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }
    // номер пути
    private int getNRoute() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return block32.get(15) >>> 5;
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }
    // число вагонов
    private int getNWags() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return block32.get(15) & 0x1F;
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }
    // текущий номер перегона
    private int getNPer() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return block32.get(16);
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }
    // давление ТЦ
    private double getPressTC() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return block32.get(17) * 0.04;
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }
    // давление УР
    private double getPressUR() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return block32.get(18) * 0.04;
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }
    // давление ЗТС
    private double getPressZTS() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return block32.get(19) * 0.04;
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }
    // позиция контроллера
    private int getPosController() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return block32.get(20);
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }
    // АЛСН УСАВП
    private int getALSNUSAVP() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return (block32.get(21) & 0xF8) >>> 3;
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }
    // ОП
    private int getPosOP() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return block32.get(21) & 0x07;
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }
    // АЛСН КЛУБ
    private int getALSNKLUB() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return block32.get(22) & 0x0F;
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }
    // тип торможения
    private int getTypeBrake() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return (block32.get(22) & 0x30) >>> 4;
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }

    // версия посылки 0(старая- не испльзую) или 1 - новая
    private int getVersion() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return (block32.get(28) >>> 5) & 0x03;
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }
    // серия локомотива
    private int getLocType() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return block32.get(28) & 0x1F;
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }
    // поз едт
    private int getPosEdt() {
        switch (block32.getTypeMovement()) {
            case Passenger:
                if (block32.type() == Block32.TypeBlock.USAVPas)
                    return block32.get(28) >>> 0x07;
            case Freight:
                if (block32.type() == Block32.TypeBlock.USAVMain)
                    return  0;
        }
        return 0;
    }

//    StatusSavpe := (blk[6] shr 6) and $01;   // ????????????????
//    PUSK := (blk[27] and $10) shr 4;
//    ActivTorm := (blk[27] and $8) shr 3;
//    PermTorm:=(blk[27] and $4) shr 2;
//    PermTyag:=(blk[27] and $2) shr 1;
//    bAutoGo:=blk[27] and $1;
//    Vmax:=blk[6] and $07;
//    bAlsnClub:=blk[22] and $0f;

}
