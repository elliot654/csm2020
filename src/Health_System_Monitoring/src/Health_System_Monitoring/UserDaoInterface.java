package Health_System_Monitoring;

import java.util.List;


public interface UserDaoInterface {
    /*
     * method to check login credentials
     * @params username and password of user
     * @return User object if successfully authenticated
     * or null if not
     *
     */
    User checkCredentials(String userName, String userPassword);


    /*
    gets users matching the param user type
    @param userType the type of user
    @return a list of users matching the type specified
     */
    List<User> getUserByType(String userType);


    /**
     * get user by Id
     * @param id of the User to retrieve
     * @return User object for that id, or Null if not found
     */
    User getUserById(int id);

    /*
    method to search user
    @param user last name
    @return a list of users that matches the name
    */
    List<User> searchUser(String userLastName);


    /*
     * method to fetch all user record from database
     * @return a list of all users from database
     */
    List<User> getAllUsers();


    /*
    @param patient the User object - Create a user object and pass into the method
    @return boolean true if patient record successfully added
    false for not added
     */
    boolean addNewUserToDatabase(User user);


    /*
    @param user is the User object
    Create a user object and pass into
    the method
    @return boolean true if updated and false if not updated
     */
    boolean updateUserRecord(User user);

    /*
     method to delete a user record from database
     @ param userId is the id of the row in user table
     @return true if deleted and false if not
    */
    boolean deleteUserFromDatabase(int userId);
    
    /*
    method to add a referral to the referral table
    @ param patient_id id of the patient in the patient table
    @ param gp_id id of the GP in the user table
    @ param rd_id id of the RD in the user table
    @return true if successfully added and false if not
   */
    boolean addReferral(int patient_id, int gp_id, int rd_id);

    /*
     * Method to retrieve referral for a given Patient
     * @param patient_id
     * @return rd referred to, or -1 if no referral
     */
    int getReferralByPatientId(int patient_id);

    /*
     * Method to retrieve referral for a given Patient
     * @param rd_id
     * @return list of patient IDs for patients referred to given RD
     */
    List<Integer> getReferralByRD(int rd_id);
}
