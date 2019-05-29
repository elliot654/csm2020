package Health_System_Monitoring;
//singleton class to get database connection

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;

import java.util.Properties;


/*
 * TODO:encrypt and decrypt password
 */

/*
 * @desc - A singleton database class access for mysql
 * This class will handle the database connection
 * @author - Kelechi Matthew Okwuriki
 */

public class database_driver {
    private static database_driver database_driver = null;
    private static Connection databaseConnection = null;


    private database_driver() {
        try {
            Properties dbProps = new Properties();
            FileInputStream fileInputStream = new FileInputStream("db.properties");

            //load the info from the input stream
            dbProps.load(fileInputStream);

            String url = dbProps.getProperty("jdbcUrl");
            String user = dbProps.getProperty("databaseUser");
            String pass = dbProps.getProperty("databasePass");

            databaseConnection = DriverManager.getConnection(url,user, pass);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    /*
     * @return mysql database connection object
     */
    public static Connection getConnection() {
        if(database_driver == null) {
            database_driver = new database_driver();
        }
        return databaseConnection;
    }
}
