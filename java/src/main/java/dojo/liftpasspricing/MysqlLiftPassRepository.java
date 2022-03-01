package dojo.liftpasspricing;

import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MysqlLiftPassRepository implements LiftPassRepository{

    public static final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/lift_pass";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "mysql";
    private Connection connection;

    static{
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public MysqlLiftPassRepository() {
        try {
            this.connection = DriverManager.getConnection(CONNECTION_STRING, DB_USER, DB_PASSWORD);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    connection.close();
                } catch (SQLException e) {
                    LoggerFactory.getLogger(Main.class).error("connection close", e);
                }
            }));

        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void add(LiftPass liftPass) {
        try (PreparedStatement stmt = connection.prepareStatement( //
                "INSERT INTO base_price (type, cost) VALUES (?, ?) " + //
                        "ON DUPLICATE KEY UPDATE cost = ?")) {
            stmt.setString(1, liftPass.getType().toString());
            stmt.setInt(2, liftPass.getCost());
            stmt.setInt(3, liftPass.getCost());
            stmt.execute();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public LiftPass findBaseByPrice(String type) {
        int basePrice = 0;
        try (PreparedStatement costStmt = connection.prepareStatement( //
                "SELECT cost FROM base_price " + //
                        "WHERE type = ?")) {
            costStmt.setString(1, type);
            try (ResultSet result = costStmt.executeQuery()) {
                result.next();
                basePrice = result.getInt("cost");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return new LiftPass(basePrice, type);
    }

    public List<LocalDate> findAllHolidaysDates() {
        List<LocalDate> holidaysDates = new ArrayList<>();
        try (PreparedStatement holidayStmt = connection.prepareStatement( //
                "SELECT * FROM holidays")) {
            try (ResultSet holidays = holidayStmt.executeQuery()) {

                while (holidays.next()) {
                    java.sql.Date holiday = holidays.getDate("holiday");
                    holidaysDates.add(holiday.toLocalDate());
                }
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return holidaysDates;
    }

}
