package blocks32;
// массив 32-х байтовых посылок

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.file.Path;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import org.w3c.dom.ranges.RangeException;

public class ArrBlock32 {

    private static Block32.TypeMovement curTypeMovement = Block32.TypeMovement.Unknown;
    private final ArrayList<Block32> arrayList = new ArrayList<>();    // массив элементов Block32
    private MappedByteBuffer buf = null;            // 
    private int len;                                        // длина channel
    private boolean isShift;
    private int curLineX;
    private int curLineT;

    /**
     *
     * @param path файл поездки
     * @param isShift  сдвижка (исключить посылки с неверной кс)
    */
    public ArrBlock32(Path path, boolean isShift) throws IOException {
        this.isShift = isShift;
        makeBuffer(path);
        if (!check())
            throw new IOException(path.toString() + 
                    " - it is a not file of the train");
        fillArrayList();
    }

    private void makeBuffer(Path path) throws IOException {

        try (FileChannel channel = FileChannel.open(path)) {

            len = (int)channel.size();
            buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, len);
        }
    }

    private void fillArrayList() {

        if (buf == null) return;
        buf.position(0);
        byte[] b = new byte[Block32.SIZE_BLOCK];
        while (buf.hasRemaining()) {

            if ((buf.position() + Block32.SIZE_BLOCK) > len)
                buf.position(len);
            else
                buf.get(b);
            
            Block32 block32 = new Block32(b);
            Block32Values block32Values = new Block32Values(block32);
            if (!isShift || block32.crcTruth()) {
                arrayList.add(block32);
                block32.setTypeMovement(curTypeMovement);
            } else
                buf.position(buf.position() - Block32.SIZE_BLOCK + 1); // сдвижка
        }
    }

    /**
     *
     * @param iBl индекс посылки
     * @return  32-байтовая посылка
     */
    public Block32 get(int iBl) throws RangeException {
        return arrayList.get(iBl);
    }

    /**
     *
     * @return всего посылок
     */
    public int size() {
        return arrayList.size();
    }
    

    //проверка файла поездки на соответствие формату, текущий тип
    private boolean check() {
        buf.position(0);
        final int n = 50;
        int cnt = 0;
        byte[] b = new byte[Block32.SIZE_BLOCK];
        curTypeMovement = Block32.TypeMovement.Unknown;
        while (buf.hasRemaining()) {

            if ((buf.position() + Block32.SIZE_BLOCK) > len)
                buf.position(len);
            else
                buf.get(b);            
            
            Block32 block32 = new Block32(b);           
            if (block32.crcTruth()) {
                cnt++;
            }

            if (curTypeMovement == Block32.TypeMovement.Unknown)
                curTypeMovement = typeMovementById(block32.getId());
            else if (curTypeMovement == Block32.TypeMovement.Locomotive &&
                    typeMovementById(block32.getId()) != Block32.TypeMovement.Unknown)
                        curTypeMovement = typeMovementById(block32.getId());
        }
        return cnt > n;
    }
    // тип движения по id блока
    private Block32.TypeMovement typeMovementById(int id) {
        if (id == 0x20) return Block32.TypeMovement.Passenger;
        if (id >= 0x22 && id <= 0x2F) return Block32.TypeMovement.Passenger;
        if (id == 0x60) return Block32.TypeMovement.Passenger;
        if (id  >= 0x62 && id  <= 0x6F) return Block32.TypeMovement.Passenger;
        if (id == 0x90) return Block32.TypeMovement.Passenger;
        if (id  >= 0x92 && id  <= 0x9F) return Block32.TypeMovement.Passenger;
        if (id  >= 0x10 && id  <= 0x1F) return Block32.TypeMovement.Freight;
        if (id  >= 0x50 && id  <= 0x5F) return Block32.TypeMovement.Freight;
        if (id  == 0x79) return Block32.TypeMovement.PassLocomotive;
        if (id  >= 0x70 && id  <= 0x78) return Block32.TypeMovement.Locomotive;
        if (id  >= 0x7A && id  <= 0x7F) return Block32.TypeMovement.Locomotive;
        if (id  >= 0xC0 && id  <= 0xC7) return Block32.TypeMovement.Asim;
        return Block32.TypeMovement.Unknown;
    }
}
