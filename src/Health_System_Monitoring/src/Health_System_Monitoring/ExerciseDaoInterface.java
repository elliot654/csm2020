package Health_System_Monitoring;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Collection;

public interface ExerciseDaoInterface {
    /*
    Access Exercise Plan tables
    These are a little more involved than the usual enter/retrieve form setup
    We have a Regimes table, which records which exercise regime is assigned to which patient and for how long,
    and a Trials table, which records the actual exercise regime itself.
    Note that the relationship between Trials and Regimes is  many-to-one. We record the results for each Trial in the
    completed_time field.
    */

    // Regime Table CRUD

    public int addNewRegime(ExerciseRegime regime);

    public boolean updateRegime(ExerciseRegime regime);

    public boolean removeRegime(ExerciseRegime regime);
    public boolean removeRegime(int regimeId);

    // basic getter

    public Collection<ExerciseRegime> getAllRegimesForPatient(int patientId);

    // -----------------------------------------------------------------------------------

    // Trial Table Crud

    public int addNewTrial(ExerciseTrial trial);

    public boolean updateTrial(ExerciseTrial trial);

    public boolean removeTrial(ExerciseTrial trial);
    public boolean removeTrial(int trialId);

    // basic getter

    public Collection<ExerciseTrial> getAllTrialsForRegime(int regimeId); // note that this is basically 1, see above

    // -----------------------------------------------------------------------------------

    // Utility Functions

    public ExerciseRegime getAssignedRegimeForPatient(Patient patient);
}
