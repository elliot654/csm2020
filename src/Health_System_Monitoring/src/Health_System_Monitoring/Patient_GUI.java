package Health_System_Monitoring;

import com.opencsv.CSVWriter;
import org.jfree.ui.RefineryUtilities;


import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.PrinterJob;
import java.io.File;
import java.awt.print.PrinterException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class Patient_GUI {
    public static JFrame mainFrame, confirmFrame;
    public static JFrame previousFrame;
    private JLabel headerLabel;
    private JPanel northPanel, headerPanel, controlPanel, confirmPanel, confirmButtonPanel, southPanel, successPanel, successSouthPanel, infoPanel, referPanel,formsPanel;    private JButton triggerButton;
    private Patient patient;
    private JComboBox<String> referBox;
    private List<User> rd_list;

    private Printer print = new Printer();
    private ArrayList<String> printList = new ArrayList<String>();
    private String printTxt;

    // Open Form For Entry
    private JPanel formWritePanel;
    private JComboBox<String> formWriteComboBox;
    private HashMap<String,Integer> formWriteLookup;
    private JButton formWriteButton = new JButton("Go");

    // Open Form For Review
    private JPanel formReviewPanel;
    private JComboBox<String> formReviewComboBox;
    private JComboBox<String> formReviewSelectComboBox;
    private HashMap<String,Integer> formReviewLookup;
    private HashMap<String, Integer> formSubmissionLookup;
    private JButton formReviewButton = new JButton("Go");

    public void preparePatientGUI(Patient pat, JFrame previousWindow, String userType) {

        previousFrame = previousWindow;

        Main_GUI.setWindowPosition(previousWindow.getLocation().x, previousWindow.getLocation().y);
        previousWindow.setVisible(false);

        mainFrame = new JFrame("GP application");
        mainFrame.setSize(500, 500);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        headerPanel = new JPanel();
        controlPanel = new JPanel();
        southPanel = new JPanel();

        patient = pat;

        formsPanel = new JPanel();
        formsPanel.setLayout(new FlowLayout());

        HeaderLabel();
        FormReviewControls(userType);
        FormWriteControls();

        if (userType == "gp") {
            ModifyRecordButton();
            DeleteRecordButton();

            CompareResultsButton();
            PrintButton();
        }

        PatientBackButton();

        PatientInfoPanel();
        PatientInfoDisplay(userType);

        PatientExerciseButton(userType);

        // gp can refer to RD. SC's are referred from the patient interface, nobody else refers
        if(userType == "gp")
        {
            PatientReferPanel();
        }

        mainFrame.setLocation(Main_GUI.getWindowPosition());
        mainFrame.add(headerPanel, BorderLayout.NORTH);
        mainFrame.add(controlPanel, BorderLayout.CENTER);
        mainFrame.add(southPanel, BorderLayout.SOUTH);
        mainFrame.setVisible(true);
    }

    private void PatientExerciseButton(String type)
    {
        switch(type)
        {
            case("rd"):
            {
                JButton exerciseButton = new JButton("Set Exercise Regime");

                southPanel.add(exerciseButton);

                exerciseButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        // launch the exercise settings
                        Exercise_GUI.prepareExerciseGUI(patient);
                        Exercise_GUI.mainFrame.setVisible(true);
                    }
                });
            }
            break;

            case("sc"):
            {
                ExerciseDao dao = new ExerciseDao();
                ExerciseRegime regime = dao.getAssignedRegimeForPatient(patient);
                if(regime!=null) {
                    JButton exerciseButton = new JButton("Export Exercise Data");
                    southPanel.add(exerciseButton);
                    exerciseButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            // launch the exercise data in CSV format

                            File file = new File("exercise_trial.csv");

                            try {
                                // create FileWriter object with file as parameter
                                FileWriter outputfile = new FileWriter(file);

                                // create CSVWriter object filewriter object as parameter
                                CSVWriter writer = new CSVWriter(outputfile);

                                // create a List which contains String array
                                List<String[]> data = new ArrayList<String[]>();
                                data.add(new String[] { "Patient Reference Id", "Intensity / Slope", "Intensity / Speed", "Duration" });

                                ExerciseRegime regime = dao.getAssignedRegimeForPatient(patient);
                                for(ExerciseTrial trial : regime.trials) {
                                    String trial_id = String.valueOf(trial.trialId);
                                    String intensity_slope = String.valueOf(trial.intensity_slope);
                                    String intensity_speed = String.valueOf(trial.intensity_speed);
                                    String duration = String.valueOf(trial.duration);
                                    data.add(new String[]{trial_id, intensity_slope, intensity_speed, duration});
                                }
                                writer.writeAll(data);

                                // closing writer connection
                                writer.close();

                                JOptionPane.showMessageDialog(mainFrame,"Saved successfully!");

                            }
                            catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
            break;
        }


    }



    private void FormReviewControls(String userType) {

        FormDao dao = new FormJDBC();

        // Form Review Panel contains 3 elements - the form selector (containing all patient records this UserType can access),
        // the submission selector (containing all the dated records), and the Go button

        formReviewPanel = new JPanel();
        formReviewPanel.setBorder(BorderFactory.createTitledBorder("Review"));
        formReviewPanel.setLayout(new FlowLayout());
        formReviewPanel.setVisible(true);

        formReviewLookup = new HashMap<String, Integer>();
        formSubmissionLookup = new HashMap<String, Integer>();

        UpdateReviewLookup(userType);

        Set<String> names = formReviewLookup.keySet();

        String[] nameArray = names.toArray(new String[names.size()]);
        formReviewComboBox = new JComboBox<String>(nameArray);
        formReviewComboBox.setVisible(true);
        formReviewPanel.add(formReviewComboBox);

        formReviewSelectComboBox = new JComboBox<String>();
        formReviewSelectComboBox.setVisible(true);
        formReviewSelectComboBox.setEnabled(false);
        formReviewPanel.add(formReviewSelectComboBox);

        formReviewButton = new JButton("Go");
        formReviewButton.setVisible(true);
        formReviewButton.setEnabled(false);
        formReviewPanel.add(formReviewButton);

        // when the form selector combo box is triggered, we update the submission list
        formReviewComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.DESELECTED) return; // skip the deselection events
                String key = (String) e.getItem();
                Integer value = formReviewLookup.getOrDefault(key, -1);
                if (value < 0) {
                    // didn't find the form for some reason
                    formReviewSelectComboBox.setEnabled(false);
                    formReviewButton.setEnabled(false);

                } else {
                    // found the form ID we want to open, so retrieve the dated records for it
                    UpdateReviewSubmissionLookup(value);
                    // and fill up the combo box
                    formReviewSelectComboBox.removeAllItems();
                    for (String date : formSubmissionLookup.keySet()) {
                        formReviewSelectComboBox.addItem(date);
                    }

                    formReviewSelectComboBox.setEnabled(true);
                    formReviewButton.setEnabled(true);
                }
            }
        });

        if(!formReviewLookup.isEmpty())
        {
            // we'll have selected the first one, so activate the second accordingly
            Integer value = formReviewLookup.getOrDefault(nameArray[0], -1);
            if (value < 0) {
                // didn't find the form for some reason
                formReviewSelectComboBox.setEnabled(false);
                formReviewButton.setEnabled(false);

            } else {
                // found the form ID we want to open, so retrieve the dated records for it
                UpdateReviewSubmissionLookup(value);
                // and fill up the combo box
                formReviewSelectComboBox.removeAllItems();
                for (String date : formSubmissionLookup.keySet()) {
                    formReviewSelectComboBox.addItem(date);
                }

                formReviewSelectComboBox.setEnabled(true);
                formReviewButton.setEnabled(true);
            }
        }

        // if the submission combo box is updated, activate the button
        formReviewSelectComboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String key = (String) e.getItem();

                    Integer value = formSubmissionLookup.getOrDefault(key, -1);
                    if (value < 0) {
                        // didn't find the submission for some reason
                        formReviewButton.setEnabled(false);
                    } else {
                        formReviewButton.setEnabled(true);
                    }
            }

        });

        if(!formSubmissionLookup.isEmpty())
        {
            formReviewButton.setEnabled(true);
        }

        // if the button is pressed, look up the relevant submission
        formReviewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String formKey = (String) formReviewComboBox.getSelectedItem();
                String submissionKey = (String) formReviewSelectComboBox.getSelectedItem();
                Integer formValue = formReviewLookup.getOrDefault(formKey, -1);

                    Integer submissionValue = formSubmissionLookup.getOrDefault(submissionKey, -1);
                    if (submissionValue < 0) {
                        // didn't find the form for some reason
                    } else {
                        // found the form ID we want to open, so open it in read-only mode (since this is the review set)
                        Form_GUI.getPatientForm(formValue, patient.getPatientId(), submissionValue,true);
                    }
                }
        });

        controlPanel.add(formReviewPanel);
    }

    /**
     * Update lookup to gather all forms for review mode for the current type of user
     * @param type
     */
    private void UpdateReviewLookup(String type)
    {
        FormDao dao = new FormJDBC();

        switch(type)
        {
            case "gp":
            {
                // gp's can review all forms assigned to the patient
                formReviewLookup = (HashMap<String,Integer>)dao.getFormsForGP(Main_GUI.getCurrentUser().getUserId());
            }
            break;

            case "rd":
            {
                // rd's can review NICE Tests and exercise trial results
                formReviewLookup = (HashMap<String,Integer>)dao.getFormByTypeAndPatient(type,patient.getPatientId());
                formReviewLookup.putAll((HashMap<String,Integer>)dao.getFormByNameAndPatient("NICE Test",patient.getPatientId()));
            }
            break;

            case "sc":
            {
                // sc's can only review exercise trials and issue results
                formReviewPanel.setVisible(false);
            }
            break;
        }
    }

    private void UpdateReviewSubmissionLookup(int form_id)
    {
        FormDao dao = new FormJDBC();

        if(formSubmissionLookup == null)
        {
            formSubmissionLookup = new HashMap<String,Integer>();
        }
        formSubmissionLookup.clear();
        HashMap<java.sql.Date, Integer> map = (HashMap<java.sql.Date, Integer>)dao.getSubmissionsForPatient(form_id,patient.getPatientId());
        for(Map.Entry<java.sql.Date, Integer> entry : map.entrySet())
        {
            String displayDate = entry.getKey().toLocalDate().toString();
            formSubmissionLookup.put(displayDate, entry.getValue());
        }
    }


    /**
    * Doesn't need to know the current type, since it just issues new ones
     */
    private void FormWriteControls()
    {
        FormDao dao = new FormJDBC();

        UpdateFormWriteLookup();

        formWritePanel = new JPanel();

        if(formWriteLookup.size() == 0)
        {
            // we currently have no forms! So remove this from our patient page entirely
            formWritePanel.setVisible(false);
        }
        else {

            TitledBorder referBorder = new TitledBorder("Forms");
            formWritePanel.setBorder(referBorder);

            formWritePanel.setLayout(new FlowLayout());
            Set<String> names = formWriteLookup.keySet();

            String[] nameArray = names.toArray(new String[names.size()]);
            formWriteComboBox = new JComboBox<String>(nameArray);

            formWriteComboBox.setVisible(true);
            formWritePanel.add(formWriteComboBox);

            formWriteComboBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.DESELECTED) return; // skip the deselection events
                    String key = (String) e.getItem();
                    Integer value = formWriteLookup.getOrDefault(key, -1);
                    formWriteButton.setEnabled(true);
                    }
            });
        }

        // button to launch the selected form
        formWriteButton = new JButton("Go");
        formWritePanel.add(formWriteButton);
        formWriteButton.setEnabled(true);
        formWriteButton.setVisible(true);

        formWriteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String formKey = (String) formWriteComboBox.getSelectedItem();
                Integer formValue = formWriteLookup.getOrDefault(formKey, -1);

                int newSubmissionId = dao.addSubmission(formValue, Main_GUI.getCurrentUser().getUserId(), patient.getPatientId(), new java.sql.Date(System.currentTimeMillis()));
                Form_GUI.getPatientForm(formValue, patient.getPatientId(), newSubmissionId,false);
            }
        });

        controlPanel.add(formWritePanel);

    }


    /**
     * Update lookup to gather all forms for data entry mode
     */
    private void UpdateFormWriteLookup()
    {
        FormDao dao = new FormJDBC();

        formWriteLookup = (HashMap<String,Integer>)dao.getFormsForGP(Main_GUI.getCurrentUser().getUserId());
    }






    private void PatientInfoPanel() {
        infoPanel = new JPanel();
        FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 10, 4);
        infoPanel.setLayout(flowLayout);
        infoPanel.setPreferredSize(new Dimension(400, 200));
        TitledBorder patientBorder = new TitledBorder("Patient Details");
        infoPanel.setBorder(patientBorder);
        controlPanel.add(infoPanel);
    }

    private void PatientReferPanel() {
        referPanel = new JPanel();
        FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 10, 4);
        referPanel.setLayout(flowLayout);

        // first check to see if we're already referred

        UserDaoInterface userDao = UserDao.getDAO();

        int rd_id = userDao.getReferralByPatientId(patient.getPatientId());
        if (rd_id != -1) {
            // we have a referral, so show that
            User rd = userDao.getUserById(rd_id);

            JLabel referred = new JLabel(rd.getUserFirstName() + " " + rd.getUserLastName());
            referPanel.add(referred);
        } else {
            // first up, the combo box containing all RDs

            if (rd_list == null) {
                rd_list = new ArrayList<User>();
            } else {
                rd_list.clear();
            }
            rd_list = userDao.getUserByType("rd");
            Vector<String> name_list = new Vector<String>(rd_list.size());

            for (User user : rd_list) {
                name_list.add(user.getUserFirstName() + " " + user.getUserLastName());
            }

            referBox = new JComboBox<String>(name_list);

            // next up, the button to trigger referral

            triggerButton = new JButton("Refer");
            triggerButton.setActionCommand("Refer_Patient");
            triggerButton.addActionListener(new Patient_GUI.ButtonClickListener());

            referPanel.add(referBox);
            referPanel.add(triggerButton);
        }
        TitledBorder referBorder = new TitledBorder("Referrals");
        referPanel.setBorder(referBorder);
        controlPanel.add(referPanel);
    }

    private void PatientInfoDisplay(String type) {
        JLabel NameLabel = new JLabel("", JLabel.CENTER);
        NameLabel.setText("Full name: " + patient.getPatientFirstName() + " " + patient.getPatientLastName());
        JLabel DoBLabel = new JLabel("", JLabel.CENTER);
        DoBLabel.setText("Date of Birth: " + patient.getPatientDob());
        JLabel AddressLabel = new JLabel("", JLabel.CENTER);
        AddressLabel.setText("Address: " + patient.getPatientAddress());
        JLabel HistoryLabel = new JLabel("", JLabel.CENTER);
        JLabel DiagnosisLabel = new JLabel("", JLabel.CENTER);
        JLabel PrescriptionLabel = new JLabel("", JLabel.CENTER);
        if(type != "sc") {
            HistoryLabel.setText("Medical History: " + patient.getPatientMedicalHistory());
            DiagnosisLabel.setText("Diagnosis: " + patient.getPatientDiagnosis());
            PrescriptionLabel.setText("Prescription: " + patient.getPatientPrescriptions());
        }

        print.setString(NameLabel.getText());
        print.setString(DoBLabel.getText());
        print.setString(AddressLabel.getText());
        print.setString(HistoryLabel.getText());
        print.setString(DiagnosisLabel.getText());
        print.setString(PrescriptionLabel.getText());

        infoPanel.add(NameLabel);
        infoPanel.add(DoBLabel);
        infoPanel.add(AddressLabel);
        infoPanel.add(HistoryLabel);
        infoPanel.add(DiagnosisLabel);
        infoPanel.add(PrescriptionLabel);
    }

/**
    private void UpdateSubmissionLookupForForm(int form_id)
    {
        FormDao dao = new FormJDBC();
        if(submissionSelectLookup == null)
        {
            submissionSelectLookup = new HashMap<String,Integer>();
        }
        submissionSelectLookup.clear();
        HashMap<java.sql.Date, Integer> map = (HashMap<java.sql.Date, Integer>)dao.getSubmissionsForPatient(form_id,patient.getPatientId());
        for(Map.Entry<java.sql.Date, Integer> entry : map.entrySet())
        {
            String displayDate = entry.getKey().toLocalDate().toString();
            submissionSelectLookup.put(displayDate, entry.getValue());
        }
    }

    private void FormComboBox() {

        UpdateFormsLookup();
        if(editableFormLookup.size() == 0)
        {
            // we currently have no forms! So remove this from our patient page entirely

            if(formsPanel != null)
            {
                formsPanel.setVisible(false);
            }
        }
        else {
            FormDao dao = new FormJDBC();
            formsPanel = new JPanel();

            TitledBorder referBorder = new TitledBorder("Forms");
            formsPanel.setBorder(referBorder);

            formsPanel.setLayout(new FlowLayout());
            Set<String> names = editableFormLookup.keySet();
            String[] nameArray = names.toArray(new String[names.size()]);
            formCreateComboBox = new JComboBox<String>(nameArray);

            formCreateComboBox.setVisible(true);
            formsPanel.add(formCreateComboBox);

            String initial_set = formCreateComboBox.getItemAt(0);

            submissionSelectComboBox = new JComboBox<String>();
            submissionSelectComboBox.setEnabled(false);
            submissionSelectComboBox.setVisible(true);
            formsPanel.add(submissionSelectComboBox);

            formCreateComboBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.DESELECTED) return; // skip the deselection events
                    String key = (String) e.getItem();
                    Integer value = editableFormLookup.getOrDefault(key, -1);
                    if (value < 0) {
                        // didn't find the form for some reason
                        submissionSelectComboBox.setEnabled(false);
                        gpButton.setEnabled(false);

                    } else {
                        // found the form ID we want to open, so retrieve the dated records for it
                        UpdateSubmissionLookupForForm(value);
                        // and fill up the combo box
                        submissionSelectComboBox.removeAllItems();
                        submissionSelectComboBox.addItem("Add New...");
                        for (String date : submissionSelectLookup.keySet()) {
                            submissionSelectComboBox.addItem(date);
                        }

                        submissionSelectComboBox.setEnabled(true);
                        gpButton.setEnabled(true);
                    }
                }
            });

            submissionSelectComboBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    String key = (String) e.getItem();
                    if (key == "Add New...") {
                        gpButton.setEnabled(true);
                    } else {
                        Integer value = submissionSelectLookup.getOrDefault(key, -1);
                        if (value < 0) {
                            // didn't find the submission for some reason
                            gpButton.setEnabled(false);
                        } else {
                            gpButton.setEnabled(true);
                        }
                    }
                }
            });

            // button to launch the selected form
            gpButton = new JButton("Go");
            formsPanel.add(gpButton);
            gpButton.setEnabled(false);
            gpButton.setVisible(true);

            gpButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String formKey = (String) formCreateComboBox.getSelectedItem();
                    String submissionKey = (String) submissionSelectComboBox.getSelectedItem();
                    Integer formValue = editableFormLookup.getOrDefault(formKey, -1);
                    if (submissionKey == "Add New...") {
                        // new submission!
                        int newSubmissionId = dao.addSubmission(formValue, Main_GUI.getCurrentUser().getUserId(), patient.getPatientId(), new java.sql.Date(System.currentTimeMillis()));
                        Form_GUI.getPatientForm(formValue, patient.getPatientId(), newSubmissionId);
                    } else {
                        Integer submissionValue = submissionSelectLookup.getOrDefault(submissionKey, -1);
                        if (submissionValue < 0) {
                            // didn't find the form for some reason
                        } else {
                            // found the form ID we want to open, so open it
                            Form_GUI.getPatientForm(formValue, patient.getPatientId(), submissionValue);
                        }
                    }
                }
            });

            controlPanel.add(formsPanel);
        }
    }

 */
    public void ReferPatient() {
        int selected = referBox.getSelectedIndex();
        // text box maps one-to-one with returned RD user list, which is stored in rd_list

        User rd = rd_list.get(selected);
        User gp = Main_GUI.getCurrentUser();

        int rd_id = rd.getUserId();
        int gp_id = gp.getUserId();
        int patient_id = patient.getPatientId();

        UserDao userDao = new UserDao();
        boolean result = userDao.addReferral(patient_id, gp_id, rd_id);

        referBox.setEditable(false);
        referBox.setEnabled(false);
        triggerButton.setEnabled(false);
        triggerButton.setText("Referred");
    }

    public void ModifyRecordButtonFunction() {
        GP_Register_GUI bob = new GP_Register_GUI();
        bob.prepareModifyGPGUI(patient);
    }

    private void HeaderLabel() {
        headerLabel = new JLabel("", JLabel.CENTER);
        headerLabel.setText("Patient Record Overview");
        headerPanel.add(headerLabel);
    }

    public void GoToGPGUI() {
        mainFrame.setVisible(false);
        previousFrame.setLocation(Main_GUI.getWindowPosition());
        previousFrame.setVisible(true);
    }

    public void GoToRDGUI() {
        mainFrame.setVisible(false);
        RD_GUI.mainFrame.setLocation(Main_GUI.getWindowPosition());
        RD_GUI.mainFrame.setVisible(true);
    }

    public void DeleteConfirmWindow() {
        confirmFrame = new JFrame("Confirm Deletion");
        confirmFrame.setSize(320, 200);
        confirmFrame.setLayout(new BorderLayout());
        confirmFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                confirmFrame.setVisible(false);
            }
        });

        confirmPanel = new JPanel();
        confirmButtonPanel = new JPanel();

        JLabel SuccessfulLabel = new JLabel("", JLabel.CENTER);
        SuccessfulLabel.setText("Are you sure you want to delete this record?");
        confirmPanel.add(SuccessfulLabel);

        DeleteOkayButton();
        DeleteCancelButton();

        confirmFrame.setLocationRelativeTo(null);
        confirmFrame.add(confirmPanel, BorderLayout.CENTER);
        confirmFrame.add(confirmButtonPanel, BorderLayout.SOUTH);
        confirmFrame.setVisible(true);
    }

    public void DeleteOkayButtonFunction() throws SQLException {
        boolean acceptedCheck;

        PatientDao pDao = (PatientDao) new PatientDao();
        acceptedCheck = pDao.deletePatientRecord(patient.getPatientId());

        if (acceptedCheck == true) {
            confirmFrame.setVisible(false);
            Patient_GUI patient_GUI = new Patient_GUI();
            patient_GUI.GoToGPGUI();
        }
    }

    public void DeleteCancelButtonFunction() {
        confirmFrame.setVisible(false);
    }

    /**
     * Create GUI for
     */
    private void ModifyRecordButton() {
        JButton NiceButton = new JButton("Modify Record");
        NiceButton.setActionCommand("Patient_Modify_Record");
        NiceButton.addActionListener(new Patient_GUI.ButtonClickListener());
        controlPanel.add(NiceButton);
    }

    /**
     * Create GUI for record delete button
     */
    private void DeleteRecordButton() {
        JButton NiceButton = new JButton("Delete Record");
        NiceButton.setActionCommand("Patient_Delete_Record");
        NiceButton.addActionListener(new Patient_GUI.ButtonClickListener());
        controlPanel.add(NiceButton);
    }



    /**
     * Create GUI for view NICE test button
     */
    private void ViewNiceButton() {
        JButton NiceButton = new JButton("View Nice Tests");
        NiceButton.setActionCommand("Patient_Nice_View");
        NiceButton.addActionListener(new Patient_GUI.ButtonClickListener());
        controlPanel.add(NiceButton);
    }

    /**
     * Creates compare results button
     */
    private void CompareResultsButton() {
        JButton ResultsButton = new JButton("Compare Results");
        ResultsButton.setActionCommand("Compare_Results");
        ResultsButton.addActionListener(new Patient_GUI.ButtonClickListener());
        controlPanel.add(ResultsButton);
    }

    /**
     * Creates new window to select which chart to show, for selected patient
     */
    private void CompareResults() {
        Compare_Results.DisplayPanel(patient.getPatientId());
    }

    /**
     * Create a button to print displayed information
     */
    private void PrintButton() {
        JButton PrintButton = new JButton("Print");
        PrintButton.setActionCommand("Print");
        PrintButton.addActionListener(new Patient_GUI.ButtonClickListener());
        controlPanel.add(PrintButton);
    }

    /**
     *  Prints selected patients information
     */
    private void PrinterJob() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setPrintable(print);
        boolean ok = job.printDialog();
        if (ok) {
            try {
                job.print();
            } catch(PrinterException e){
                System.out.println(e);
            }
        }
    }

    /**
     * Create GUI for back button
     */
    private void PatientBackButton() {
        JButton BackButton = new JButton("Back");

        BackButton.setActionCommand("Patient_Back");


        BackButton.addActionListener(new Patient_GUI.ButtonClickListener());
        southPanel.add(BackButton);
    }

    /**
     * Create GUI for okay button in delete confirmation window
     */
    private void DeleteOkayButton() {
        JButton deleteButton = new JButton("Okay");
        deleteButton.setActionCommand("Patient_Delete_Okay");
        deleteButton.addActionListener(new Patient_GUI.ButtonClickListener());
        confirmButtonPanel.add(deleteButton);
    }

    /**
     * Create GUI for cancel button in delete confirmation window
     */
    private void DeleteCancelButton() {
        JButton dontDeleteButton = new JButton("Cancel");
        dontDeleteButton.setActionCommand("Patient_Delete_Cancel");
        dontDeleteButton.addActionListener(new Patient_GUI.ButtonClickListener());
        confirmButtonPanel.add(dontDeleteButton);
    }

    /**
     * Action Listener that looks out for button presses in Patient_GUI
     */
    class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            GP_GUI gp_gui = new GP_GUI();
            GP_Register_GUI gp_register_gui = new GP_Register_GUI();
            //Patient_GUI patient_gui = new Patient_GUI();
            NICE_GUI nice_gui = new NICE_GUI();

            if (command.equals("Default")) {
                //Do nothing
            } else if (command.equals("Patient_Back")) {
                Main_GUI.setWindowPosition(mainFrame.getLocation().x, mainFrame.getLocation().y);
                GoToGPGUI();
            } else if (command.equals("Patient_RD_Back")) {
                Main_GUI.setWindowPosition(mainFrame.getLocation().x, mainFrame.getLocation().y);
                GoToRDGUI();
            } else if (command.equals("Patient_Delete_Cancel")) {
                DeleteCancelButtonFunction();
            } else if (command.equals("Patient_Delete_Okay")) {
                try {
                    DeleteOkayButtonFunction();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            } else if (command.equals("Patient_Delete_Record")) {
                DeleteConfirmWindow();
            } else if (command.equals("Patient_Modify_Record")) {
                Main_GUI.setWindowPosition(mainFrame.getLocation().x, mainFrame.getLocation().y);
                ModifyRecordButtonFunction();
            } else if (command.equals("Patient_Nice")) {
                nice_gui.prepareNiceGUI();
            } else if (command.equals("Patient_Nice_View")) {

            } else if (command.equals("Refer_Patient")) {
                ReferPatient();
            } else if (command.equals("Print")){
                PrinterJob();
            } else if (command.equals("Compare_Results")) {
                CompareResults();
            }
        }
    }
}