package in.nimbo;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDataSource {
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:mem:default";

    //  Database credentials
    private static final String USER = "sa";
    private static final String PASS = "";
    private static boolean TABLES_CREATED = false;
    static private Connection connection;

    public static DataSource init() throws SQLException, ClassNotFoundException, IOException {
        Class.forName(JDBC_DRIVER);
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(DB_URL);
        ds.setUser(USER);
        ds.setPassword(PASS);
        connection = ds.getConnection();
        if (TABLES_CREATED == false)
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
            }
        }
        TABLES_CREATED = true;
    }
}
