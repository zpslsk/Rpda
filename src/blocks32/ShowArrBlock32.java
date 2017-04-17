package blocks32;

import javax.swing.*;
import java.util.*;
import java.util.List;
import java.awt.*;

public class ShowArrBlock32  {

    private final JTextArea textArea;
    private final JProgressBar progress;
    private final boolean isProgressVisibleDone;    
    private final ArrBlock32 arrBlock32;    
    private final HashSet<Integer> setFilterId = new HashSet<>();    
    private final String strFormat = "%8d | %8d | 0x%02X |  "
        + "%02X %02X %02X %02X %02X %02X %02X %02X %02X %02X "
        + "%02X %02X %02X %02X %02X %02X %02X %02X %02X %02X "
        + "%02X %02X %02X %02X %02X %02X %02X %02X %02X\n";  

    public ShowArrBlock32(ArrBlock32 arrBlock32, 
        JTextArea textArea, JProgressBar progress, int... arrId) {

        if (progress == null) progress = new JProgressBar();
        this.progress = progress;
        if (textArea == null) textArea = new JTextArea();
        this.textArea = textArea;        
        textArea.setText("");
        textArea.setFont(new Font("Monospaced", 
                textArea.getFont().getStyle(), textArea.getFont().getSize()));        
        for (int id : arrId) setFilterId.add(id);        
        this.arrBlock32 = arrBlock32;
        progress.setMaximum(arrBlock32.size() - 1); 
        isProgressVisibleDone = progress.isVisible();
    }
    
    public void doShow() {
        new ProgressWorker().execute();
    }
    
    private class ProgressData{
        public int number;
        public String line;
    }
    
    private class ProgressWorker extends SwingWorker<Void, ProgressData> {
        
        @Override
        public Void doInBackground() {
            int nOrd = 0, nBlk = 0;
            progress.setVisible(true);
            for (int i = 0; i < arrBlock32.size(); i++) {
                Block32 block32 = arrBlock32.get(i);
                if (setFilterId.isEmpty() || setFilterId.contains(block32.getId())){
                    byte[] bts = block32.getValues();
                    String line = String.format(strFormat,
                        ++nOrd, nBlk, block32.getId(),
                        bts[0], bts[1], bts[2], bts[3], bts[4], bts[5], bts[6], 
                        bts[7], bts[8], bts[9], bts[10], bts[11], bts[12], 
                        bts[13], bts[14], bts[15], bts[16], bts[17], bts[18], 
                        bts[19], bts[20], bts[21], bts[22], bts[23], bts[24],
                        bts[25], bts[26], bts[27], bts[28]);                        
                    ProgressData data = new ProgressData();
                    data.line = line;
                    data.number = nBlk;
                    publish(data);                        
                }                 
                ++nBlk;                    
            }                
            return null;
        }
        
        private boolean isSetPosition = true;        
        @Override
        public void process(List<ProgressData> data) {
            StringBuilder builder = new StringBuilder();
            progress.setValue(data.get(data.size() - 1).number);
            data.forEach((d) -> {
                builder.append(d.line);
            });
            textArea.append(builder.toString());
            if (isSetPosition && textArea.getCaretPosition() > 0) {
                textArea.setCaretPosition(0);
                isSetPosition = false;
            }            
        }
        
        @Override
        public void done() {
            progress.setValue(arrBlock32.size());
            progress.setVisible(isProgressVisibleDone);
        }
    }
}
