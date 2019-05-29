package Health_System_Monitoring;

import java.sql.Date;
import java.util.Collection;

public class ExerciseRegime {
    public int regimeId;
    public int patientId;
    public int rdId;
    public Date startDate;
    public Date endDate;
    public int frequency;

    public Collection<ExerciseTrial> trials;
}
