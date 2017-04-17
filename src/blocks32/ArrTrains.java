package blocks32;

import java.util.ArrayList;

public class ArrTrains {

    private ArrBlock32 arrBlock32;
    private ArrayList<Train> listTrains = new ArrayList<>();

    public ArrTrains(ArrBlock32 arrBlock32) {

        this.arrBlock32 = arrBlock32;
    }

    public class Train {
        public int getnTrain() {
            return nTrain;
        }

        public void setnTrain(int nTrain) {
            this.nTrain = nTrain;
        }

        public int getnType() {
            return nType;
        }

        public void setnType(int nType) {
            this.nType = nType;
        }

        public int nTrain;
        public int nType;

        public Train() {

        }
    }

    private void fillListTrains() {
        for(int i = 0; i < arrBlock32.size(); i++) {

        }
    }
}
