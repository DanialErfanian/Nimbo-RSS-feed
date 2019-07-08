package in.nimbo;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

class TestDataSource {
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:mem:default";

    //  Database credentials
    private static final String USER = "sa";
    private static final String PASS = "";

    static private Connection connection;

    static DataSource init() throws SQLException, ClassNotFoundException, IOException {
        Class.forName(JDBC_DRIVER);
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(DB_URL);
        ds.setUser(USER);
        ds.setPassword(PASS);
        connection = ds.getConnection();
        createTables();
        return ds;
    }

    private static void createTables() throws IOException, SQLException {
        String s;
        StringBuilder stringBuffer = new StringBuilder();
        FileReader fr = new FileReader(new File("src/main/sql/createTables.sql"));
        BufferedReader br = new BufferedReader(fr);
        while ((s = br.readLine()) != null) {
            stringBuffer.append(s);
        }
        br.close();
        String[] inst = stringBuffer.toString().split(";");
        Statement st = connection.createStatement();
        for (String value : inst) {
            if (!value.trim().equals("")) {
                st.executeUpdate(value);
                System.out.println(">>" + value);
            }
        }

    }
}
