package db;

import java.sql.*;
import java.nio.file.*;
import java.util.*;
import java.io.*;
import static db.Queries.*;

public class DataBase {

    private static Connection conn = null;
    // версия базы
    public static class VersionBase {
        private int major;
        private int minor;
        public VersionBase(int major, int minor) {
            this.major = major;
            this.minor = minor;
        }
        public String toString() {
            return String.format("%d.%d", major, minor);
        }
        public int getMajor() {
            return major;
        }
        public int getMinor() {
            return minor;
        }
    }

    // новое соединение из файла
    public static Connection newConnection(Path path) throws IOException, SQLException {
        Properties props = new Properties();
        try(InputStream in = Files.newInputStream(path)) {
            props.load(in);
        }
        String url = props.getProperty("jdbc.url");
        String user = props.getProperty("jdbc.user");
        String password = props.getProperty("jdbc.password");
        conn = DriverManager.getConnection(url, user, password);
        return conn;
    }
    //
    public static Connection getConnection(Path path) throws SQLException, IOException {
        if (conn == null)
            return conn = newConnection(path);
        else
            return conn;
    }

    public static ResultSet getResultSet(String sql, Queries.Params params) throws  SQLException {
        PreparedStatement stat = conn.prepareStatement(sql);
        stat.setString(PAR_DATESTART , params.getDateStartString());
        stat.setString(PAR_DATEFINISH, params.getDateFinishString());
        stat.setInt(PAR_LOCTYPE, params.getTypeTrain());
        stat.setInt(PAR_TRNUM, params.getNumTrain());
        stat.setInt(PAR_LOCNUM, params.getNumLoc());
        stat.setInt(PAR_DRCOL, params.getDrvCol());
        stat.setInt(PAR_TBNUM, params.getTabNum());
        return stat.executeQuery();
    }
}

