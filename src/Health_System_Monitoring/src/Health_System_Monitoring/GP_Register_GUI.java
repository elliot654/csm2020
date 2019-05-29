package Health_System_Monitoring;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;

public class GP_Register_GUI {
    public static JFrame mainFrame;
    private JPanel controlPanel, southPanel, patientPanel, medicationPanel, infoPanel;

    private JLabel firstNameLabel, lastNameLabel, addressLabel, dateOfBirthLabel, emailLabel, medicalHistoryLabel, patientDiagnosisLabel, patientPrescriptionsLabel;
    private JTextField firstNameTextField, lastNameTextField, emailTextField, patientDiagnosisTextField;
    private JTextArea addressTextArea, medicalHistoryTextArea, patientPrescriptionsTextArea;
    private UtilDateModel dateOfBirthModel;
    private JDatePanelImpl datePanel;
    private JDatePickerImpl datePicker;

    private Patient patient;

    private String patient_first_name, patient_last_name, patient_address, patient_email, patient_medical_history, patient_diagnosis, patient_prescriptions;
    private java.sql.Date patient_dob;
    private int patient_ID, userId;
    private Boolean newRecord, patient_email_prescription, anyEmptyStrings;


    public void prepareGPGUI(boolean isNewRecord) {
        newRecord = isNewRecord;

        //userId = Main_GUI.getCurrentUser().getUserId();

        if(userId == 0){
            userId = 1;
        }

        if (isNewRecord) {
            GP_GUI.mainFrame.setVisible(false);
            mainFrame = new JFrame("Register new patient");
            ClearValuesInBoxes();
        } else if (isNewRecord == false) {
            Patient_GUI.mainFrame.setVisible(false);
            mainFrame = new JFrame("Modify current patient");
        }
        mainFrame.setSize(500, 500);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        controlPanel = new JPanel();
        southPanel = new JPanel();

        PatientPanel();
        MedicationPanel();
        InfoPanel();

        FirstNameLabel();
        FirstNameTextField();
        LastNameLabel();
        LastNameTextField();
        AddressLabel();
        AddressTextArea();
        DateOfBirthLabel();
        DateOfBirthField();
        EmailLabel();
        EmailTextField();
        PatientMedicalHistoryLabel();
        PatientMedicalHistoryTextArea();
        PatientDiagnosisLabel();
        PatientDiagnosisTextField();
        PatientPrescriptionsLabel();
        PatientPrescriptionsTextArea();
        emailCheckBox();

        BackButton();

        SubmitButton();


        mainFrame.setLocation(Main_GUI.getWindowPosition());
        mainFrame.add(controlPanel, BorderLayout.CENTER);
        mainFrame.add(southPanel, BorderLayout.SOUTH);
        mainFrame.setVisible(true);
    }

    public void prepareModifyGPGUI(Patient pat) {
        patient = pat;
        PlaceValuesInBoxes();
        prepareGPGUI(false);
    }

    public void ClearValuesInBoxes() {
        patient_first_name = null;
        patient_last_name = null;
        patient_dob = null;
        patient_address = null;
        patient_email = null;
        patient_medical_history = null;
        patient_diagnosis = null;
        patient_prescriptions = null;
    }

    public void PlaceValuesInBoxes() {
        patient_first_name = patient.getPatientFirstName();
        patient_last_name = patient.getPatientLastName();
        patient_dob = patient.getPatientDob();
        patient_address = patient.getPatientAddress();
        patient_email = patient.getPatient_email();
        patient_medical_history = patient.getPatientMedicalHistory();
        patient_diagnosis = patient.getPatientDiagnosis();
        patient_prescriptions = patient.getPatientPrescriptions();
        patient_email_prescription = patient.getPatientEmailPrescription();
        patient_ID = patient.getPatientId();
    }

    private void PatientPanel() {
        patientPanel = new JPanel();
        FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 10, 4);
        patientPanel.setLayout(flowLayout);
        TitledBorder patientBorder = new TitledBorder("Patient Details");
        patientPanel.setBorder(patientBorder);
        patientPanel.setPreferredSize(new Dimension(400, 200));
        controlPanel.add(patientPanel);
    }

    private void MedicationPanel() {
        medicationPanel = new JPanel();
        FlowLayout flowLayout = new FlowLayout(FlowLayout.LEFT, 10, 4);
        medicationPanel.setLayout(flowLayout);
        medicationPanel.setPreferredSize(new Dimension(400, 175));
        TitledBorder patientBorder = new TitledBorder("Medication Details");
        medicationPanel.setBorder(patientBorder);
        controlPanel.add(medicationPanel);
    }

    private void InfoPanel() {
        infoPanel = new JPanel();
        FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER, 10, 4);
        infoPanel.setLayout(flowLayout);
        infoPanel.setPreferredSize(new Dimension(400, 30));
        TitledBorder patientBorder = new TitledBorder("");
        infoPanel.setBorder(patientBorder);
        controlPanel.add(infoPanel);
    }

    private void emailCheckBox() {
        if (patient_email_prescription == null) {
            patient_email_prescription = true;
        }
        JCheckBox PrescribeCheckBox = new JCheckBox("Prescribe to third party material", patient_email_prescription);
        PrescribeCheckBox.addItemListener(this::itemStateChanged);
        infoPanel.add(PrescribeCheckBox);
    }

    public void BackButtonFunction() {
        mainFrame.setVisible(false);
        if (newRecord) {
            GP_GUI.mainFrame.setLocation(Main_GUI.getWindowPosition());
            GP_GUI.mainFrame.setVisible(true);
        } else {
            Patient_GUI.mainFrame.setLocation(Main_GUI.getWindowPosition());
            Patient_GUI.mainFrame.setVisible(true);
        }

    }

    static boolean validEmail(String email) {

        return email.matches("[A-Z0-9._%+-][A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{3}");
    }

    public void SubmitButtonFunction() throws SQLException {
        boolean acceptedCheck = false;
        anyEmptyStrings = false;

        GrabValues();
        if(anyEmptyStrings){
            JOptionPane.showMessageDialog(null, "please fill in all fields");
        }else {
            if(!validEmail(patient_email)){
                JOptionPane.showMessageDialog(null, "please enter a valid email address");
            }else{
                if(patient_email_prescription){
                    Email.sendEmail(patient_email,"subject","message");
                }
            }

            System.out.println("Submitting - First name: " + patient_first_name + ", Last name: " +
                    patient_last_name + ", Address: " + patient_address + ", Date of Birth: " + patient_dob + ", Email: " + patient_email + ", Medical History: "
                    + patient_medical_history + ", Diagnosis: " + patient_diagnosis + ", Prescription: " + patient_prescriptions + ", User ID: " + userId + ", Patient ID: " + patient_ID);

            PatientDao pDao = new PatientDao();

            if (newRecord) {
                int id = pDao.addPatientToDatabase(patient, Main_GUI.getCurrentUser());
                if (id > 0) {
                    acceptedCheck = true;
                }
            } else {
                acceptedCheck = pDao.updatePatientRecord(patient);
            }

            if (acceptedCheck) {
                mainFrame.setVisible(false);
                if (newRecord) {
                    GP_GUI.mainFrame.setVisible(true);
                    SubmitConfirmedWindow();
                } else {
                    //Patient_GUI.mainFrame.setVisible(true);
                    Patient_GUI patient_gui = new Patient_GUI();
//                    patient_gui.preparePatientGUI(patient);
                    SubmitModifyConfirmedWindow();
                }
            } else {
            /*
            JLabel errorLabel = new JLabel();
            errorLabel.setText("Something was wrong with the submission");
            infoPanel.removeAll();
            infoPanel.add(errorLabel);
            infoPanel.updateUI();*/
                JOptionPane.showMessageDialog(null, "Something went wrong with the submission");
            }
        }
    }

    private void FirstNameLabel() {
        firstNameLabel = new JLabel();
        firstNameLabel.setText("First name: ");
        patientPanel.add(firstNameLabel);
    }

    private void FirstNameTextField() {
        firstNameTextField = new JTextField(patient_first_name);
        firstNameTextField.setPreferredSize(new Dimension(250, 25));
        patientPanel.add(firstNameTextField);
    }

    private void LastNameLabel() {
        lastNameLabel = new JLabel();
        lastNameLabel.setText("Last name: ");
        patientPanel.add(lastNameLabel);
    }

    private void LastNameTextField() {
        lastNameTextField = new JTextField(patient_last_name);
        lastNameTextField.setPreferredSize(new Dimension(250, 25));
        patientPanel.add(lastNameTextField);
    }

    private void AddressLabel() {
        addressLabel = new JLabel();
        addressLabel.setText("Address: ");
        patientPanel.add(addressLabel);
    }

    private void AddressTextArea() {
        addressTextArea = new JTextArea(patient_address);
        addressTextArea.setPreferredSize(new Dimension(250, 50));
        JScrollPane scrollPane = new JScrollPane(addressTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        addressTextArea.setLineWrap(true);
        patientPanel.add(scrollPane);
    }

    private void EmailLabel() {
        emailLabel = new JLabel();
        emailLabel.setText("Email address: ");
        patientPanel.add(emailLabel);
    }

    private void EmailTextField() {
        emailTextField = new JTextField(patient_email);
        emailTextField.setPreferredSize(new Dimension(150, 25));
        patientPanel.add(emailTextField);
    }

    private void PatientMedicalHistoryLabel() {
        medicalHistoryLabel = new JLabel();
        medicalHistoryLabel.setText("Medical History: ");
        medicationPanel.add(medicalHistoryLabel);
    }

    private void PatientMedicalHistoryTextArea() {
        medicalHistoryTextArea = new JTextArea(patient_medical_history);
        medicalHistoryTextArea.setPreferredSize(new Dimension(250, 50));
        JScrollPane scrollPane = new JScrollPane(medicalHistoryTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        medicalHistoryTextArea.setLineWrap(true);
        medicationPanel.add(scrollPane);
    }

    private void PatientDiagnosisLabel() {
        patientDiagnosisLabel = new JLabel();
        patientDiagnosisLabel.setText("Diagnosis: ");
        medicationPanel.add(patientDiagnosisLabel);
    }

    private void PatientDiagnosisTextField() {
        patientDiagnosisTextField = new JTextField(patient_diagnosis);
        patientDiagnosisTextField.setPreferredSize(new Dimension(250, 25));
        medicationPanel.add(patientDiagnosisTextField);
    }

    private void PatientPrescriptionsLabel() {
        patientPrescriptionsLabel = new JLabel();
        patientPrescriptionsLabel.setText("Prescription: ");
        medicationPanel.add(patientPrescriptionsLabel);
    }

    private void PatientPrescriptionsTextArea() {
        patientPrescriptionsTextArea = new JTextArea(patient_prescriptions);
        patientPrescriptionsTextArea.setPreferredSize(new Dimension(250, 50));
        JScrollPane scrollPane = new JScrollPane(patientPrescriptionsTextArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        patientPrescriptionsTextArea.setLineWrap(true);
        medicationPanel.add(scrollPane);
    }

    private void DateOfBirthLabel() {
        dateOfBirthLabel = new JLabel();
        dateOfBirthLabel.setText("Date of Birth: ");
        patientPanel.add(dateOfBirthLabel);
    }

    private Calendar getDate() {
        Date date = patient.getPatientDob();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    private void DateOfBirthField() {
        dateOfBirthModel = new UtilDateModel();
        if (newRecord != true) {
            dateOfBirthModel.setDate(getDate().get(Calendar.YEAR), getDate().get(Calendar.MONTH), getDate().get(Calendar.DAY_OF_MONTH));
            dateOfBirthModel.setSelected(true);
        }

        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        datePanel = new JDatePanelImpl(dateOfBirthModel, p);
        datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());

        patientPanel.add(datePicker);
    }

    private class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {

        private String datePattern = "yyyy-MM-dd";
        private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

        @Override
        public Object stringToValue(String text) throws ParseException {
            return dateFormatter.parseObject(text);
        }

        @Override
        public String valueToString(Object value) {
            if (value != null) {
                Calendar cal = (Calendar) value;
                return dateFormatter.format(cal.getTime());
            }

            return "";
        }

    }

    private void createPatient() {
        patient = new Patient();
        patient.setPatient_first_name(patient_first_name);
        patient.setPatient_last_name(patient_last_name);
        patient.setPatient_address(patient_address);
        patient.setPatient_dob(patient_dob);
        patient.setPatient_email(patient_email);
        patient.setPatient_medical_history(patient_medical_history);
        patient.setPatient_diagnosis(patient_diagnosis);
        patient.setPatient_prescriptions(patient_prescriptions);
        patient.setPatient_email_prescription(patient_email_prescription);
        if (!newRecord) {
            patient.setPatient_id(patient_ID);
        }
    }

    private void GrabValues() {
        Date temp = (Date) datePicker.getModel().getValue();
        if (temp != null) {
            patient_dob = new java.sql.Date(temp.getTime());
        }
        patient_first_name = CheckStringEmpty(patient_first_name, firstNameTextField.getText());
        patient_last_name = CheckStringEmpty(patient_last_name, lastNameTextField.getText());
        patient_address = CheckStringEmpty(patient_address, addressTextArea.getText());
        patient_email = CheckStringEmpty(patient_email, emailTextField.getText());
        patient_medical_history = CheckStringEmpty(patient_medical_history, medicalHistoryTextArea.getText());
        patient_diagnosis = CheckStringEmpty(patient_diagnosis, patientDiagnosisTextField.getText());
        patient_prescriptions = CheckStringEmpty(patient_prescriptions, patientPrescriptionsTextArea.getText());
        if (!newRecord) {
            patient_ID = patient.getPatientId();
        }
        createPatient();
    }

    private String CheckStringEmpty(String x, String y) {
        if (y.length() > 0) {
            x = y;
        } else {
            anyEmptyStrings = true;
        }
        return x;
    }

    private void SubmitConfirmedWindow() {
        mainFrame = new JFrame("Registration Successful");
        mainFrame.setSize(320, 200);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                mainFrame.setVisible(false);
            }
        });

        JPanel successPanel = new JPanel();
        JPanel successSouthPanel = new JPanel();

        JLabel SuccessfulLabel = new JLabel("", JLabel.CENTER);
        SuccessfulLabel.setText("Patient registration was submitted successfully");
        successPanel.add(SuccessfulLabel);

        mainFrame.setLocationRelativeTo(null);
        mainFrame.add(successPanel, BorderLayout.CENTER);
        mainFrame.add(successSouthPanel, BorderLayout.SOUTH);
        mainFrame.setVisible(true);
    }

    private void SubmitModifyConfirmedWindow() {
        mainFrame = new JFrame("Modification Successful");
        mainFrame.setSize(320, 200);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                mainFrame.setVisible(false);
            }
        });

        JPanel successPanel = new JPanel();
        JPanel successSouthPanel = new JPanel();

        JLabel SuccessfulLabel = new JLabel("", JLabel.CENTER);
        SuccessfulLabel.setText("Patient record modification was successful");
        successPanel.add(SuccessfulLabel);

        mainFrame.setLocationRelativeTo(null);
        mainFrame.add(successPanel, BorderLayout.CENTER);
        mainFrame.add(successSouthPanel, BorderLayout.SOUTH);
        mainFrame.setVisible(true);
    }

    /**
     * Create GUI for back button
     */
    private void BackButton() {
        JButton BackButton = new JButton("Back");
        BackButton.setActionCommand("GP_Register_Back");
        BackButton.addActionListener(new GP_Register_GUI.ButtonClickListener());
        southPanel.add(BackButton);
    }

    /**
     * Create GUI for
     */
    private void SubmitButton() {
        JButton SubmitButton = new JButton("Submit");
        SubmitButton.setActionCommand("GP_Register_Submit");
        SubmitButton.addActionListener(new GP_Register_GUI.ButtonClickListener());
        southPanel.add(SubmitButton);
    }

    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            patient_email_prescription = true;
        } else {
            patient_email_prescription = false;
        }
    }

    /**
     * Action Listener that looks out for button presses GP_Register_GUI
     */
    class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.equals("Default")) {
                //Do nothing
            } else if (command.equals("GP_Register_Back")) {
                Main_GUI.setWindowPosition(mainFrame.getLocation().x, mainFrame.getLocation().y);
                BackButtonFunction();
            } else if (command.equals("GP_Register_Submit")) {
                try {
                    Main_GUI.setWindowPosition(mainFrame.getLocation().x, mainFrame.getLocation().y);
                    SubmitButtonFunction();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}