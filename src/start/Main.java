package start;

import java.awt.*;
import javax.swing.*;
import java.io.IOException;
import java.sql.*;
import java.nio.file.*;

public class Main {

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            Connection conn = null;
            try {
                conn = db.DataBase.newConnection(Paths.get("database.ini"));
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        String.format("Файл \"%s\" не найден!", e.getMessage()));
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
            StartFrame frame = new StartFrame(conn);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}
