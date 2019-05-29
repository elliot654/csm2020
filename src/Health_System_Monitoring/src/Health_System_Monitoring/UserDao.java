package Health_System_Monitoring;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;


public class UserDao implements UserDaoInterface{

    public UserDao() {}

    public static UserDao getDAO() { return new UserDao(); }

    @Override
    public User checkCredentials(String userName, String userPassword) {
        Connection databaseConnection = database_driver.getConnection();

        if(userName != null && userPassword != null) {

            PreparedStatement sqlStatement = null;
            try {
                String query = "SELECT * FROM `user` WHERE `username`=? AND `userPassword`=?";

                sqlStatement = databaseConnection.prepareStatement(query);
                sqlStatement.setString(1, userName);
                sqlStatement.setString(2, userPassword);

                ResultSet resultSet = sqlStatement.executeQuery();
//				closeDbConnection();

                if (resultSet.next()) {
                    int userId = resultSet.getInt("userId");
                    String userEmail = resultSet.getString("userEmail");
                    String userFirstName = resultSet.getString("userFirstName");
                    String userLastName = resultSet.getString("userLastName");
                    String userType = resultSet.getString("userType");

                    return new User(userId, userName, null, userFirstName, userLastName,
                            userEmail, userType, true);

                }

            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());

            } finally {
                if (sqlStatement != null) {
                    try {
                        sqlStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                //closeDbConnection();
            }

        }

        return null;
    }

    /**
     * retrieve user by Id
     * @param id of the User to retrieve
     * @return User object for that id, or Null if not found
     */
    @Override
    public User getUserById(int id) {
        Connection databaseConnection = database_driver.getConnection();

        PreparedStatement sqlStatement = null;

        try {
            String query = "SELECT * FROM user WHERE userId=?";

            sqlStatement = databaseConnection.prepareStatement(query);
            sqlStatement.setInt(1, id);

            //holds the result from database
            ResultSet resultSet = sqlStatement.executeQuery();

            if (resultSet.next()) {
                return convertToUser(resultSet);
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        } finally {
            if (sqlStatement != null) {
                try
                {
                    sqlStatement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());

                }
            }
            //closeDbConnection();
        }

        return null;
    }

    @Override
    public List<User> getUserByType(String userType) {
        Connection databaseConnection = database_driver.getConnection();

        List<User> userList = new ArrayList<>();

        PreparedStatement sqlStatement = null;

        try {
            String query = "SELECT * FROM user WHERE userType=?";

            sqlStatement = databaseConnection.prepareStatement(query);
            sqlStatement.setString(1, userType);

            //holds the result from database
            ResultSet resultSet = sqlStatement.executeQuery();

            if (resultSet.next()) {
                User user = convertToUser(resultSet);
                userList.add(user);

                return userList;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        } finally {
            if (sqlStatement != null) {
                try
                {
                    sqlStatement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());

                }
            }
            //closeDbConnection();
        }

        return null;
    }

    @Override
    public List<User> searchUser(String userLastName) {
        Connection databaseConnection = database_driver.getConnection();

        List<User> userMatchList = new ArrayList<>();
        PreparedStatement sqlStatement = null;

        try{
            //make it like sql command by adding the percentage
            userLastName += "%";

            String query = "SELECT * FROM user WHERE userLastName like ?";

            sqlStatement = databaseConnection.prepareStatement(query);

            sqlStatement.setString(1, userLastName);

            ResultSet resultSet = sqlStatement.executeQuery();


            while (resultSet.next()){
                //call convertToPatient object method below
                User user = convertToUser(resultSet);

                userMatchList.add(user);
            }
            return userMatchList;

        } catch (SQLException e){
            e.printStackTrace();
            System.out.println(e.getMessage());

        } finally {
            if(sqlStatement != null){
                try {
                    sqlStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            //closeDbConnection();
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        Connection databaseConnection = database_driver.getConnection();

        List<User> userRecordList = new ArrayList<>();

        PreparedStatement sqlStatement = null;

        try {
            String query = "SELECT * FROM user";
            sqlStatement = databaseConnection.prepareStatement(query);
            ResultSet resultSet = sqlStatement.executeQuery();


            while (resultSet.next()) {
                User user = convertToUser(resultSet);
                userRecordList.add(user);
            }

            return userRecordList;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        } finally {
            if (sqlStatement != null) {
                try {
                    sqlStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            //closeDbConnection();
        }

        return null;
    }

    @Override
    public boolean addNewUserToDatabase(User user) {

        Connection databaseConnection = database_driver.getConnection();

        if(user != null) {

            PreparedStatement sqlStatement = null;
            try {
                String query = "INSERT INTO user (username, userPassword, userEmail, userFirstName," +
                        "userLastName, userType)" + " values (?, ?, ?, ?, ?, ?)";
                //create mysql prepared statement
                sqlStatement = databaseConnection.prepareStatement(query);
                sqlStatement.setString(1, user.getUserName());
                sqlStatement.setString(2, user.getUserPass());
                sqlStatement.setString(3, user.getUserEmail());
                sqlStatement.setString(4, user.getUserFirstName());
                sqlStatement.setString(5, user.getUserLastName());
                sqlStatement.setString(6, user.getUserType());

                sqlStatement.executeUpdate();
                //closeDbConnection();

                System.out.println("User record added");

                return true;

            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());

            } finally {
                if (sqlStatement != null) {
                    try {
                        sqlStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                //closeDbConnection();
            }
        }

        return false;
    }

    @Override
    public boolean updateUserRecord(User user) {
        Connection databaseConnection = database_driver.getConnection();

        if(user != null) {

            PreparedStatement sqlStatement = null;
            try {
                String query = "UPDATE user SET username=?, userPassword=?, userEmail=?,"
                        + "userFirstName=?, userLastName=?, userType=? where userId=?";

                sqlStatement = databaseConnection.prepareStatement(query);
                sqlStatement.setString(1, user.getUserName());
                sqlStatement.setString(2, user.getUserPass());
                sqlStatement.setString(3, user.getUserEmail());
                sqlStatement.setString(4, user.getUserFirstName());
                sqlStatement.setString(5, user.getUserLastName());
                sqlStatement.setString(6, user.getUserType());
                sqlStatement.setInt(7, user.getUserId());

                sqlStatement.executeUpdate();
                //closeDbConnection();

                System.out.println("User record updated");

                return true;

            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());

            } finally {
                if (sqlStatement != null) {
                    try {
                        sqlStatement.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                //closeDbConnection();
            }
        }

        return false;
    }

    @Override
    public boolean deleteUserFromDatabase(int userId) {
        Connection databaseConnection = database_driver.getConnection();

        PreparedStatement sqlStatement = null;
        try {
            String query = "DELETE FROM user WHERE userId=?";

            sqlStatement = databaseConnection.prepareStatement(query);

            sqlStatement.setInt(1, userId);

            sqlStatement.executeUpdate();
            System.out.println("User record deleted!");

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        } finally {
            if (sqlStatement != null) {
                try {
                    sqlStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
//            closeDbConnection();
        }
        return false;
    }

    /*private method to convert result from database
    into a patient object
    @param resultSet is result from database
    @return patient object
    */
    private User convertToUser(ResultSet resultSet) throws SQLException {
        int userId = resultSet.getInt("userId");
        String userEmail = resultSet.getString("userEmail");
        String username = resultSet.getString("username");
        String userFirstName = resultSet.getString("userFirstName");
        String userLastName = resultSet.getString("userLastName");
        String userType = resultSet.getString("userType");

        return new User(userId, username, null, userFirstName, userLastName,
                userEmail, userType, false);
    }

    /**
     * Method to retrieve referral for a given Patient
     * @param patient_id
     * @return rd_id, or -1 if no referral
     */
    @Override
    public int getReferralByPatientId(int patient_id)
    {
        Connection databaseConnection = database_driver.getConnection();

        PreparedStatement sqlStatement = null;

        int output = -1; // default/null response

        try {
            String query = "SELECT rd_id FROM referrals WHERE patient_id=?";

            sqlStatement = databaseConnection.prepareStatement(query);
            sqlStatement.setInt(1,patient_id);

            ResultSet resultSet = sqlStatement.executeQuery();

            if(resultSet.next()) {
                output = resultSet.getInt("rd_id");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        } finally {
            if (sqlStatement != null) {
                try
                {
                    sqlStatement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());

                }
            }
        }

        return output;
    }

    @Override
    public List<Integer> getReferralByRD(int rd_id)
    {
        Connection databaseConnection = database_driver.getConnection();

        PreparedStatement sqlStatement = null;

        List<Integer> output = new ArrayList<Integer>();

        try {
            String query = "SELECT patient_id FROM referrals WHERE rd_id=?";

            sqlStatement = databaseConnection.prepareStatement(query);
            sqlStatement.setInt(1,rd_id);

            ResultSet resultSet = sqlStatement.executeQuery();

            while(resultSet.next()) {
                output.add(resultSet.getInt("patient_id"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        } finally {
            if (sqlStatement != null) {
                try
                {
                    sqlStatement.close();
                }
                catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());

                }
            }
        }

        return output;
    }

    /*
    method to add a referral to the referral table
    @ param patient_id id of the patient in the user table
    @ param gp_id id of the GP in the user table
    @ param rd_id id of the RD in the user table
    @return true if successfully added and false if not
   */
    @Override
    public boolean addReferral(int patient_id, int gp_id, int rd_id) {
    	Connection databaseConnection = database_driver.getConnection();

    	PreparedStatement sqlStatement = null;
 	   
 	   try {
 	   String query = "INSERT INTO referrals (patient_id, gp_id, rd_id, referral_date)" + " values (?, ?, ?, ?)";

 	   
        //create mysql prepared statement
        sqlStatement = databaseConnection.prepareStatement(query);
        sqlStatement.setInt(1,patient_id);
        sqlStatement.setInt(2,gp_id);
        sqlStatement.setInt(3,rd_id);
        sqlStatement.setDate(4, new java.sql.Date(System.currentTimeMillis())); // current date
        
        sqlStatement.executeUpdate();
        
        return true;
 	   }
 	   catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());

        } finally {
            if (sqlStatement != null) {
         	   try
         	   {
         		   sqlStatement.close();
 	           }
 	           catch (SQLException e) {
 	               e.printStackTrace();
 	               System.out.println(e.getMessage());
 	
 	           }
            }
        }
 	   
 	   return false;
    }


}
