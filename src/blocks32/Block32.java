package blocks32;
// 32-х байтовая посылка

import java.util.*;

public class Block32 {

    public static final byte SIZE_VALUES = 29;      //  размер сегмента данных посылки
    public static final int SIZE_BLOCK = 32;        //  размер посылки
    public enum TypeBlock {
        USAVIni, USAVWag, USAVLim, USAVMain, USAVKeyBDU, USAVShedDisp, USAVChangeDisp, USAVBHV,
        USAVMapObj, USAVPilot, USAVISAVPRT, USAVWagonsDisp, USAVChangeDisp2, USAVKLUB, USAVPas,
        UI, DPS1, DPS2, En, Press, Init, EKS1, EKS2, Diagn, Mon, BMS, Map4, Map5, Map9, MapA, MapB, C7, Unknown
    }
    public enum TypeMovement {              // тип движения
        Passenger, Freight, PassLocomotive, Locomotive, Asim, Unknown
    }

    private final byte id;                                  // идентификатор типа посылки
    private byte[] values = new byte[SIZE_VALUES];          // сегмент данных посылки
    private final byte crc16H;                              // кс - старший байт
    private final byte crc16L;                              // кс - младший байт
    private final int crc16;                                // кс - факт
    private TypeMovement typeMovement = TypeMovement.Unknown;

    public Block32(byte[] bytes) {

        id = bytes[0];
        values = Arrays.copyOfRange(bytes, 1, SIZE_BLOCK - 2);
        crc16H = bytes[30];
        crc16L = bytes[31];
        crc16 = getCrc16(Arrays.copyOfRange(bytes, 0, SIZE_BLOCK - 2));
    }

    /**
     *
     * @return идентификатор типа посылки
     */
    public int getId() {

        return Byte.toUnsignedInt(id);
    }

    /**
     *
     * @return контрольная сумма из посылки
     */
    public int getCrc() {
        return Byte.toUnsignedInt(crc16H) << 8 | Byte.toUnsignedInt(crc16L);
    }

    /**
     *
     * @return расчетная контрольная сумма
     */
    public int  getCrc16() {
        return crc16;
    }

    /**
     * @param nByte - порядковый номер байта 0-29
     * @return  беззнаковое значение байта
     * @throws IndexOutOfBoundsException
     */
    public int get(int nByte) throws IndexOutOfBoundsException {

        if (nByte >= 0 && nByte < SIZE_VALUES)
            return Byte.toUnsignedInt(values[nByte]);
        else
            throw new IndexOutOfBoundsException();
    }

    /**
     *
     * @return  знаковый массив байт данных
     */
    public byte[] getValues() {
        return values;
    }

    /**
     *
     * @param bytes
     * @return расчетная контрольная сумма
     */
    public static int getCrc16(final byte[] bytes) {

        int crc = 0;
        for (byte b : bytes) {
            crc = crc ^ b << 8;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) == 0x8000)
                    crc = (crc << 1) ^ 0x1021;
                else
                    crc = crc << 1;
            }
        }
        return crc & 0xFFFF;
    }

    /**
     *
     * @return корректна ли контрольная сумма
     */
    public boolean crcTruth() {

        return getCrc() == getCrc16();
    }

    public TypeBlock type() {
        switch(getId()) {
            case 0x10:
            case 0x12:
            case 0x20:
            case 0x30:
            case 0x50:
            case 0x51:
            case 0x60:
            case 0x90:
            case 0x52:
            case 0x53:
            case 0x5C:
            case 0x72:
                return TypeBlock.UI;
            case 0x21:
            case 0x61:
            case 0x91:
                if (typeMovement == TypeMovement.Passenger)
                    return TypeBlock.USAVPas;
                else
                    switch(((get(27) & 0x60) >>> 3) + ((get(28) & 0x60) >>> 5)) {
                        case 0x01:
                            return TypeBlock.Init;
                        case 0x02:
                            return TypeBlock.USAVWag;
                        case 0x03:
                            return TypeBlock.USAVLim;
                        case 0x04:
                            return TypeBlock.USAVMain;
                        case 0x05:
                            return TypeBlock.USAVKeyBDU;
                        case 0x06:
                            return TypeBlock.USAVShedDisp;
                        case 0x07:
                            return TypeBlock.USAVChangeDisp;
                        case 0x08:
                            return TypeBlock.USAVBHV;
                        case 0x09:
                            return TypeBlock.USAVMapObj;
                        case 0x10:
                            return TypeBlock.USAVPilot;
                        case 0x11:
                            return TypeBlock.USAVISAVPRT;
                        case 0x12:
                            return TypeBlock.USAVWagonsDisp;
                        case 0x13:
                            return TypeBlock.USAVChangeDisp2;
                        case 0x14:
                            return TypeBlock.USAVKLUB;
                        case 0x15:
                            return TypeBlock.USAVPas;
                    }
            case 0x22:
            case 0x62:
            case 0x92:
                return TypeBlock.DPS1;
            case 0x13:
            case 0x23:
            case 0x33:
            case 0x63:
            case 0x73:
            case 0x93:
            case 0x58:
            case 0x57:
            case 0x59:
            case 0x5A:
                return TypeBlock.En;
            case 0x24:
            case 0x64:
            case 0x94:
                return TypeBlock.DPS2;
            case 0x14:
            case 0x18:
            case 0x54:
            case 0x55:
            case 0x5B:
            case 0x5D:
            case 0x5E:
            case 0x5F:
            case 0x25:
            case 0x65:
            case 0x95:
            case 0x70:
                return TypeBlock.Press;
            case 0x16:
            case 0x26:
            case 0x36:
            case 0x56:
            case 0x66:
            case 0x76:
            case 0x96:
                return TypeBlock.Init;
            case 0x28:
            case 0x68:
            case 0x98:
                return TypeBlock.EKS1;
            case 0x2A:
            case 0x6A:
            case 0x9A:
                return TypeBlock.EKS2;
            case 0x29:
            case 0x69:
            case 0x99:
                return TypeBlock.Diagn;
            case 0x2C:
            case 0x6C:
            case 0x9c:
                return TypeBlock.Mon;
            case 0x2E:
            case 0x6E:
            case 0x9E:
            case 0x7E:
                return TypeBlock.BMS;
            // map
            case 0x1D:
            case 0x2D:
            case 0x6D:
            case 0x9D:
            case 0x7D:
                switch(get(0) >>> 4) {
                    case 0x04: return TypeBlock.Map4;
                    case 0x05: return TypeBlock.Map5;
                    case 0x09: return TypeBlock.Map9;
                    case 0x0A: return TypeBlock.MapA;
                    case 0x0B: return TypeBlock.MapB;
                }
            // асим
            case 0xC7:
                return TypeBlock.C7;
            case 0xC0:
                switch(get(28)) {
                    case 0x0: return TypeBlock.Init;
                    case 0x1: return TypeBlock.UI;
                }
            case 0xC2:
                switch (get(28)) {
                    case 0x0: return TypeBlock.Init;
                    case 0x1: return TypeBlock.BMS;
                }
            case 0xC3:
                return TypeBlock.En;
            case 0xC5:
                switch (get(28)) {
                    case 0x0: return TypeBlock.En;
                    case 0x1: return TypeBlock.BMS;
                }
            case 0xC4:
                switch (get(28)) {
                    case 0x0: return TypeBlock.En;
                }

            default: return TypeBlock.Unknown;
        }
    }

    public void setTypeMovement(TypeMovement typeMovement) {
        this.typeMovement = typeMovement;
    }

    public TypeMovement getTypeMovement() {
        return typeMovement;
    }

}



