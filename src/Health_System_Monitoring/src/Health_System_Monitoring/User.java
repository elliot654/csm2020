package Health_System_Monitoring;

public class User {
    private int userId;
    private String userName;
    private String userPass;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private String userType;

    private boolean userLoggedIn = false;

    public User(int userId, String userName, String userPass, String userFirstName, String userLastName,
                String userEmail, String userType, boolean userLoggedIn) {
        this.userId = userId;
        this.userName = userName;
        this.userPass = userPass;
        this.userFirstName = userFirstName;
        this.userLastName = userLastName;
        this.userEmail = userEmail;
        this.userType = userType;
        this.userLoggedIn = userLoggedIn;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getUserFirstName() {
        return userFirstName;
    }

    public void setUserFirstName(String userFirstName) {
        this.userFirstName = userFirstName;
    }

    public String getUserLastName() {
        return userLastName;
    }

    public void setUserLastName(String userLastName) {
        this.userLastName = userLastName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public boolean isUserLoggedIn() {
        return userLoggedIn;
    }

    public void setUserLoggedIn(boolean userLoggedIn) {
        this.userLoggedIn = userLoggedIn;
    }

    @Override
    public String toString() {
        return "User{" +
                " \nuserId=" + userId +
                " \nuserName='" + userName + '\'' +
                " \nuserPass='" + userPass + '\'' +
                " \nuserFirstName='" + userFirstName + '\'' +
                " \nuserLastName='" + userLastName + '\'' +
                " \nuserEmail='" + userEmail + '\'' +
                " \nuserType='" + userType + '\'' +
                " \nuserLoggedIn=" + userLoggedIn +
                '}';
    }
}
