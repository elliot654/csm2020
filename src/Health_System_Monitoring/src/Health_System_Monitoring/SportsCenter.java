package Health_System_Monitoring;
/*
    Class model for sports center
 */
public class SportsCenter {

    int sportsCenterId;
    String sportsCenterName;
    String sportsCenterAddress;
    boolean sportsCenterAvailability;

    public SportsCenter(int sportsCenterId, String sportsCenterName, String sportsCenterAddress, boolean sportsCenterAvailability){
        this.sportsCenterId = sportsCenterId;
        this.sportsCenterName = sportsCenterName;
        this.sportsCenterAddress = sportsCenterAddress;
        this.sportsCenterAvailability = sportsCenterAvailability;
    }

    public int getSportsCenterId(){
        return sportsCenterId;
    }

    public void setSportsCenterId(int sportsCenterId) {
        this.sportsCenterId = sportsCenterId;
    }

    public String getSportsCenterName() {
        return sportsCenterName;
    }

    public void setSportsCenterName(String sportsCenterName) {
        this.sportsCenterName = sportsCenterName;
    }

    public boolean getSportsCenterAvailability() {
        return sportsCenterAvailability;
    }

    public void setSportsCenterAvailability(boolean sportsCenterAvailability) {
        this.sportsCenterAvailability = sportsCenterAvailability;
    }

    @Override
    public String toString() {
        return "SportsCenter{" +
                "sportsCenterName='" + sportsCenterName + '\'' +
                ", sportsCenterAvailability=" + sportsCenterAvailability +
                '}';
    }
}
