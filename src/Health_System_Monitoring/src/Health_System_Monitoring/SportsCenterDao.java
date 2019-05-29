package Health_System_Monitoring;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SportsCenterDao implements SportsCenterDaoInterface {
    @Override
    public void setSportsCenterAvailability(SportsCenter sportsCenter) {

        //if argument passed is not an instance of sportsCenter
        if(sportsCenter instanceof SportsCenter) {
            Connection databaseConnection = database_driver.getConnection();
            PreparedStatement sqlStatement = null;

            try{
                String query = "INSERT INTO sports_center (sportsCenterAvailability)" + "VALUES (?)";

                sqlStatement = databaseConnection.prepareStatement(query);
                sqlStatement.setBoolean(1, sportsCenter.getSportsCenterAvailability());
                sqlStatement.executeUpdate();

            } catch (SQLException e){
                e.printStackTrace();

            } finally {
                if (sqlStatement != null) {
                    try {
                        sqlStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            }

        } else throw new IllegalArgumentException();

    }

    
    @Override
    public boolean getSportsCenterAvailability(int sportsCenterId) {

        if(sportsCenterId > 0){
            Connection databaseConnection = database_driver.getConnection();
            PreparedStatement sqlStatement = null;

            try{
                String query = "SELECT sportsCenterAvailability FROM sports_center WHERE sportsCenterId = ?";

                sqlStatement = databaseConnection.prepareStatement(query);
                sqlStatement.setInt(1, sportsCenterId);

                ResultSet resultSet = sqlStatement.executeQuery();

                if(resultSet.next()){
                    //return true or false
                    return resultSet.getBoolean("sportsCenterAvailability");
                }

            } catch (SQLException e){
                e.printStackTrace();

            } finally {
                if (sqlStatement != null) {
                    try {
                        sqlStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        else throw new IllegalArgumentException();

        return false;
    }

    @Override
    public List<SportsCenter> getAllSportsCenterRecord() {

        Connection databaseConnection = database_driver.getConnection();
        PreparedStatement sqlStatement = null;

        List<SportsCenter> sportsCenterRecordList = new ArrayList<>();

        try{
            String query = "SELECT * FROM sports_center";

            sqlStatement = databaseConnection.prepareStatement(query);

            ResultSet resultSet = sqlStatement.executeQuery();

            if(resultSet.next()){
                SportsCenter sportsCenter = convertResultSetToSportsCenter(resultSet);
                sportsCenterRecordList.add(sportsCenter);
            }

        } catch (SQLException e){
            e.printStackTrace();

        } finally {
            if (sqlStatement != null) {
                try {
                    sqlStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }
        return sportsCenterRecordList;
    }

    public boolean addSportsCenter(SportsCenter sportsCenter){

        Connection databaseConnection = database_driver.getConnection();
        PreparedStatement sqlStatement = null;

        try{
            String query = "INSERT into sports_center (sportsCenterId, sportsCenterName, sportsCenterAvailability)" + "" +
                    "values (?, ?, ?)";

            sqlStatement = databaseConnection.prepareStatement(query);
            sqlStatement.setInt(1, sportsCenter.getSportsCenterId());
            sqlStatement.setString(2, sportsCenter.getSportsCenterName());
            sqlStatement.setBoolean(3, sportsCenter.getSportsCenterAvailability());

            System.out.println("Sports Center record added");

            return true;

        } catch (SQLException e){
            e.printStackTrace();

        } finally {
            if (sqlStatement != null) {
                try {
                    sqlStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }

        System.out.println("Sports Center record added not added");
        return false;
    }

    public List<SportsCenter> getAllAvailableSportsCenter(){
        Connection databaseConnection = database_driver.getConnection();
        PreparedStatement sqlStatement = null;
        List<SportsCenter> availabilityList = new ArrayList<>();

        try{
            String query = "SELECT * FROM sports_center WHERE sportsCenterAvailability = true";

            sqlStatement = databaseConnection.prepareStatement(query);

            ResultSet resultSet = sqlStatement.executeQuery();

            while(resultSet.next()){
                SportsCenter sportsCenter = convertResultSetToSportsCenter(resultSet);
                availabilityList.add(sportsCenter);
            }

            return availabilityList;

        } catch (SQLException e){
            e.printStackTrace();

        } finally {
            if (sqlStatement != null) {
                try {
                    sqlStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }
        return null;
    }

    //convert sports center result set to sports center instance
    private SportsCenter convertResultSetToSportsCenter(ResultSet resultSet) throws SQLException {

        String sportsCenterAddress = resultSet.getString("sportsCenterAddress");
        int sportsCenterId = resultSet.getInt("sportsCenterId");
        String sportsCenterName = resultSet.getString("sportsCenterName");
        boolean sportsCenterAvailability = resultSet.getBoolean("sportsCenterAvailability");

        return new SportsCenter(sportsCenterId, sportsCenterName, sportsCenterAddress, sportsCenterAvailability);
    }


    @Override
    public SportsCenter getSportsCenterByUserId(int user_id)
    {
        Connection databaseConnection = database_driver.getConnection();
        PreparedStatement sqlStatement = null;

        try{
            String query = "SELECT * FROM sports_center WHERE user_id = ?";

            sqlStatement = databaseConnection.prepareStatement(query);

            sqlStatement.setInt(1,user_id);

            ResultSet resultSet = sqlStatement.executeQuery();

            while(resultSet.next()){
                return convertResultSetToSportsCenter(resultSet);
            }

        } catch (SQLException e){
            e.printStackTrace();

        } finally {
            if (sqlStatement != null) {
                try {
                    sqlStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
        }
        return null;
    }
}
