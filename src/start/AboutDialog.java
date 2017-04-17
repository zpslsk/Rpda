package start;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;

public class AboutDialog extends JDialog {

    public AboutDialog(JFrame owner, String verExe, String verBase) {
        super(owner, "О программе", true);
        setResizable(false);
        // логотип
        JLabel logoLabel = new JLabel();
        logoLabel.setIcon(new ImageIcon(getClass().getResource("images/logo.png")));
        add(logoLabel, BorderLayout.NORTH);
        // информация
        JLabel centralLabel = new JLabel("<html><h3>ООО \"АВП технология\"</h3><h3>АРМ расшифровки РПДА</h3>" +
                "<p>Версия: " + verExe + "<br>" +
                "Версия базы данных: " + verBase + "</p><br></htm1>", JLabel.CENTER);
        centralLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        add(centralLabel);
        // закрыть
        JButton buttonClose = new JButton("Закрыть");
        buttonClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                AboutDialog.this.setVisible(false);
            }
        });
        // отправить сообщение
        String title = "Отправить сообщение<br>для разработчиков...";
        String tag_open = "<html><font color=\"#000099\"><i>";
        String tag_close  = "</i></font></html>";
        JButton buttonSend = new JButton(tag_open + title + tag_close);
        buttonSend.setBorder(BorderFactory.createEmptyBorder());
        buttonSend.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        buttonSend.setFocusPainted(false);          // откл прорисовка рмки активной кнопки
        buttonSend.setContentAreaFilled(false);     // откл визуальный отклик на нажатие
        buttonSend.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent event) {
                buttonSend.setText("<html><font color=\"#0000FF\"><u><i>" + title + "</u></i></font></html>");
            }
            public void mouseExited(MouseEvent event) {
                buttonSend.setText(tag_open + title + tag_close);
            }
        });
        buttonSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    URI uri = new URI("mailto:zapselsky@avpt.ru?subject=Сообщение%20по%20АРМ%20РПДА&body=Хочу%20сообщить%20следующее...");
                    try {
                        Desktop.getDesktop().browse(uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });
        // панель кнопок
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.add(buttonSend, new GBC(0, 0).setInsets(5).setAnchor(GBC.WEST).setWeight(100, 0));
        buttonPanel.add(buttonClose, new GBC(1, 0).setInsets(5).setAnchor(GBC.EAST).setWeight(100, 0));
        add(buttonPanel, BorderLayout.SOUTH);
        //
        pack();
        setLocationRelativeTo(owner);
    }
}
