package blocks32;

import java.util.ArrayList;
import java.time.LocalDateTime;

public class ArrMinutes {

    private ArrBlock32 arrBlock32;
    private final ArrayList<Values> arrayList = new ArrayList<>();
//    private Values values = new Values();

    public ArrMinutes(ArrBlock32 arrBlock32) {
        this.arrBlock32 = arrBlock32;
        fillArrList();
    }

    private void fillArrList() {

        for (int i = 0; i < arrBlock32.size(); i++) {
            Values values = new Values();
            new Block32Values(arrBlock32.get(i)).fillValStruct(values);
            if (arrBlock32.get(i).type() == Block32.TypeBlock.USAVPas) {
//                values = new Block32Values(arrBlock32.get(i)).getValStruct(i);
//                Block32Values block32Values = new Block32Values(arrBlock32.get(i));
//                block32Values.fillValStruct(values);
//                values = new Values();
//                new Block32Values(arrBlock32.get(i)).fillValStruct(values);
                arrayList.add(values);
            }
        }
    }

    public Values getValues(int i) {
        return arrayList.get(i);
    }

    public LocalDateTime getDateTime(int i) {
        return null;// arrayList.get(i).dateTime;
    }

    public int size() {
        return arrayList.size();
    }
}
