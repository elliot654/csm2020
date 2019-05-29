package Health_System_Monitoring;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class Exercise_GUI {
    public static JFrame mainFrame = null;
    private static JPanel titlepanel = new JPanel();
    private static JPanel contentpanel = new JPanel();

    private static JLabel regimeStartLabel = new JLabel("Regime Start Date:");
    private static JLabel regimeEndLabel = new JLabel("Regime End Date:");
    private static JLabel regimeFrequencyLabel = new JLabel("Regime Frequency (sessions/week):");

    private static JLabel trialTypeLabel = new JLabel("Trial Type:");
    private static JLabel trialDurationLabel = new JLabel("Trial Duration:");
    private static JLabel trialSpeedLabel = new JLabel("Trial Speed:");
    private static JLabel trialSlopeLabel = new JLabel( "Trial Slope");

    private static JTextField regimeStartField= new JTextField();
    private static JTextField regimeEndField = new JTextField();
    private static JTextField regimeFrequencyField = new JTextField();

    private static JTextField trialTypeField = new JTextField();
    private static JTextField trialDurationField = new JTextField();
    private static JTextField trialSpeedField = new JTextField();
    private static JTextField trialSlopeField = new JTextField();

    private static Patient patient;

    public static void prepareExerciseGUI(Patient pat) {
        mainFrame = new JFrame("Exercise Regime Settings");
        mainFrame.setSize(700, 500);

        mainFrame.setLayout(new BorderLayout());

        patient = pat;

        contentpanel.setLayout(new FlowLayout());

        JPanel regimePanel = new JPanel();
        regimePanel.setBorder(BorderFactory.createTitledBorder("Regime"));

        regimePanel.add(regimeStartLabel);
        regimePanel.add(regimeStartField);
        regimeStartField.setText(new java.sql.Date(System.currentTimeMillis()).toString());

        regimePanel.add(regimeEndLabel);
        regimePanel.add(regimeEndField);
        regimeEndField.setText(new java.sql.Date(System.currentTimeMillis()).toString());

        regimePanel.add(regimeFrequencyLabel);
        regimePanel.add(regimeFrequencyField);
        regimeFrequencyField.setText("4");
        regimeFrequencyField.setSize(40, regimeFrequencyField.getHeight());

        contentpanel.add(regimePanel);

        JPanel trialPanel = new JPanel();
        trialPanel.setBorder(BorderFactory.createTitledBorder("Trial"));

        trialPanel.add(trialTypeLabel);
        trialPanel.add(trialTypeField);
        trialTypeField.setText("Aerobic");
        trialTypeField.setEditable(false);

        trialPanel.add(trialDurationLabel);
        trialPanel.add(trialDurationField);
        trialDurationField.setText("4");

        trialPanel.add(trialSpeedLabel);
        trialPanel.add(trialSpeedField);
        trialSpeedField.setText("8");

        trialPanel.add(trialSlopeLabel);
        trialPanel.add(trialSlopeField);
        trialSlopeField.setText("2.3");

        contentpanel.add(trialPanel);

        JButton submitButton = new JButton ("Submit");

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                submitResults();
            }
        });

        contentpanel.add(submitButton);

        mainFrame.add(titlepanel);
        mainFrame.add(contentpanel);
    }

    public static void submitResults()
    {
        ExerciseRegime regime = new ExerciseRegime();
        ExerciseTrial trial = new ExerciseTrial();

        regime.startDate = java.sql.Date.valueOf(regimeStartField.getText());
        regime.endDate = java.sql.Date.valueOf(regimeEndField.getText());
        regime.frequency = Integer.valueOf(regimeFrequencyField.getText());

        trial.type = trialTypeField.getText();
        trial.duration = Integer.valueOf(trialDurationField.getText());
        trial.intensity_slope = new BigDecimal(trialSlopeField.getText());
        trial.intensity_speed = Integer.valueOf(trialSpeedField.getText());

        ExerciseDao dao = new ExerciseDao();

        regime.patientId = patient.getPatientId();
        regime.rdId = Main_GUI.getCurrentUser().getUserId();

        trial.regimeId = dao.addNewRegime(regime);

        dao.addNewTrial(trial);

        mainFrame.setVisible(false);
    }

}
