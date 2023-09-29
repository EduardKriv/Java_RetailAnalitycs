package ru.school21.retail.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

@Component
@RequiredArgsConstructor
public class ManagerDAO {
    public static HashMap<Integer, String> CALL;

    static {
        CALL = new HashMap<>();
        CALL.put(1, "SELECT * FROM customers;");
        CALL.put(2, "SELECT * FROM purchase_history;");
        CALL.put(3, "SELECT * FROM periods;");
        CALL.put(4, "SELECT * FROM groups;");
        CALL.put(5, "select * from fnc_personal_offers_average_check(?::int, ?::date, ?::date, ?::int, ?::numeric, ?::numeric, ?::numeric, ?::numeric)");
        CALL.put(6, "select * from fnc_personal_offers_visits_frequency(?::date, ?::date, ?::int, ?::numeric, ?::numeric, ?::numeric)");
        CALL.put(7, "select * from fnc_personal_offers_cross_sales(?::int, ?::numeric, ?::numeric, ?::numeric, ?::numeric)");
        CALL.put(8, "call set_margin_type(?::varchar, ?::int)");
    }

    private final JdbcTemplate jdbcTemplate;

    public Map<String, List<Object>> executeQuery(String SQL) throws SQLException {
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            ResultSet result = preparedStatement.executeQuery();
            return parseResultSet(result);
        }
    }

    public void executeVoidQuery(String SQL) throws SQLException {
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            preparedStatement.execute();
        }
    }

    public Map<String, List<Object>> callFunction(String SQL, Object... args) throws SQLException {
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            if (args != null) {
                int index = 0;
                for (Object it : args) {
                    preparedStatement.setObject(++index, it);
                }
            }
            ResultSet result = preparedStatement.executeQuery();
            return parseResultSet(result);
        }
    }

    public void callVoidFunction(String SQL, Object... args) throws SQLException {
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL)) {
            if (args != null) {
                int index = 0;
                for (Object it : args) {
                    preparedStatement.setObject(++index, it);
                }
            }
            preparedStatement.execute();
        }
    }

    public Map<String, List<Object>> callProcedure(String SQL, Object... args) throws SQLException {
        try (Connection connection = Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection();
             CallableStatement callableStatement = connection.prepareCall(SQL)) {
            connection.setAutoCommit(false);
            callableStatement.registerOutParameter(1, Types.REF_CURSOR);
            if (args != null) {
                int index = 1;
                for (Object it : args) {
                    callableStatement.setObject(++index, it);
                }
            }
            callableStatement.execute();
            ResultSet result = (ResultSet) callableStatement.getObject(1);
            return parseResultSet(result);
        }
    }

    private Map<String, List<Object>> parseResultSet(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metaData = resultSet.getMetaData();
        int countColumn = metaData.getColumnCount();
        Map<String, List<Object>> resultMap = new LinkedHashMap<>();
        if (!resultSet.next()) {
            return resultMap;
        }
        List<String> headerTable = new ArrayList<>();
        List<List<Object>> dataTable = new ArrayList<>();
        for (int i = 0; i < countColumn; ++i) {
            headerTable.add(metaData.getColumnName(i + 1));
            dataTable.add(new ArrayList<>());
        }
        do {
            for (int i = 0; i < countColumn; ++i) {
                dataTable.get(i).add(Objects.requireNonNullElse(resultSet.getObject(i + 1), "<null>"));
            }
        } while (resultSet.next());

//                Object res = resultSet.getObject(i + 1);
//                if (res instanceof BigDecimal){
//                    res = ((BigDecimal) res).setScale(6, RoundingMode.CEILING);
//                }
//                dataTable.get(i).add(Objects.requireNonNullElse(res, "<null>"));
        for (int i = 0; i < countColumn; ++i) {
            resultMap.put(headerTable.get(i), dataTable.get(i));
        }
        return resultMap;
    }
}
