package Health_System_Monitoring;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class RD_GUI {
    public static JFrame mainFrame;
    private JLabel headerLabel;
    private JPanel northPanel;
    private JPanel controlPanel;
    private JPanel southPanel, referalsWindowPanel;
    private JScrollPane scrollPane;
    private SpringLayout layout = new SpringLayout();
    private JButton button = new JButton();
    private Object[][] patientReferals;
    private List<Patient> patients  = new ArrayList<>();

    private int userId;
    private JComboBox<String> formCreateComboBox;

    private HashMap<String, Integer> editableFormLookup;


    public void prepareRDGUI() {

        Main_GUI.mainFrame.setVisible(false);

        userId = Main_GUI.getCurrentUser().getUserId();

        mainFrame = new JFrame("RD application");
        mainFrame.setSize(800, 500);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        northPanel = new JPanel();
        controlPanel = new JPanel();
        southPanel = new JPanel();

        controlPanel.setLayout(layout);

        // check to see if the GP already has a NICE Test default form; if not, create one.
        FormDao dao = new FormJDBC();
        editableFormLookup = (HashMap<String, Integer>) dao.getFormsForGP(Main_GUI.getCurrentUser().getUserId());
        if(!editableFormLookup.containsKey("Diet Evaluation")) spawnEvaluationForms();

        HeaderLabel();
        BackButton();

        FormComboBox();

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ReferalsWindow();
            }
        });

        mainFrame.setLocation(Main_GUI.getWindowPosition().x - 125, Main_GUI.getWindowPosition().y - 165);
        mainFrame.add(northPanel, BorderLayout.NORTH);
        mainFrame.add(controlPanel, BorderLayout.CENTER);
        mainFrame.add(southPanel, BorderLayout.SOUTH);
        mainFrame.setVisible(true);

        if(Form_GUI.mainFrame == null)
        {
            Form_GUI.prepareFormGUI();
        }
        Form_GUI.mainFrame.setVisible(false);
    }

    /**
     *
     */
    public void GoBackToMainGUI() {
        mainFrame.setVisible(false);
        Main_GUI.mainFrame.setVisible(true);
    }

    private void HeaderLabel() {
        headerLabel = new JLabel("", JLabel.CENTER);
        headerLabel.setText("Rehabilitation Doctor Menu");
        northPanel.add(headerLabel);
    }

    /**
     * Create GUI for back button
     */
    private void BackButton() {
        JButton BackButton = new JButton("Back");
        BackButton.setActionCommand("RD_Back");
        BackButton.addActionListener(new ButtonClickListener());
        southPanel.add(BackButton);
    }

    /**
     * @throws SQLException
     */
    public void PatientOpenButtonFunction(int patNum) throws SQLException {
        Patient_GUI patient_GUI = new Patient_GUI();
        patient_GUI.preparePatientGUI(patients.get(patNum), mainFrame, "rd");
    }

    private void UpdateFormsLookup() {
        FormDao dao = new FormJDBC();
        editableFormLookup = (HashMap<String, Integer>) dao.getFormsForGP(Main_GUI.getCurrentUser().getUserId());
    }

    private void FormComboBox() {
        UpdateFormsLookup();
        Set<String> names = editableFormLookup.keySet();
        String[] nameArray = names.toArray(new String[names.size()+1]);
        nameArray[names.size()] = "New Form...";
        formCreateComboBox = new JComboBox<String>(nameArray);

        JPanel container = new JPanel();
        container.setBorder(BorderFactory.createTitledBorder("Form Creator"));
        container.setLayout(new FlowLayout());
        controlPanel.add(container);

        layout.putConstraint(SpringLayout.EAST, container, 0, SpringLayout.EAST, controlPanel);
        layout.putConstraint(SpringLayout.NORTH, container, 0, SpringLayout.NORTH, controlPanel);

        formCreateComboBox.setVisible(true);
        container.add(formCreateComboBox);


        // button to launch the selected form
        JButton gpButton = new JButton("Go");
        gpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String key = (String)formCreateComboBox.getSelectedItem();
                if(key == "New Form...")
                {
                    // generate a new empty form
                    Form_GUI.spawnEmptyForm();
                }
                else {
                    Integer value = editableFormLookup.getOrDefault(key, -1);
                    if (value < 0) {
                        // didn't find the form for some reason
                    } else {
                        // found the form ID we want to open, so open it
                        Form_GUI.setEditMode(true);
                        Form_GUI.openForm(value,false);
                    }
                }
            }
        });

        container.add(gpButton);

    }

    public void spawnEvaluationForms()
    {
        // Because Evaluation Forms are required functionality, if an RD doesn't have the Physical Evaluation tests in their formset
        // we add them for them.

        FormDao dao = (FormDao)new FormJDBC();

        int form_id = dao.addNewForm(Main_GUI.getCurrentUser(), "Diet Evaluation");

        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "5 portions of fruit & veg a day?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "4 or more varieties of fruit a week?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "4 or more varieties of veg a week?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Eat low fat products?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Baked, steamed or grilled products?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Lean cuts of meat?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Oily fish?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Starchy main meals?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Wholemeal bread?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Wholemeal cereal?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Regular pulses in diet?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Sugar in breakfast cereal?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Drink sweet fizzy drinks?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Eat cakes, sweets, chocolate or biscuits at work?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Add salt to food when cooking?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Add salt to meals?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Eat salted snacks eg crisps, peanuts?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Eat pre-prepared meals?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Eat processed foods such as bacon or ham?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Told that you have high blood pressure?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Drink plenty of fluids at regular intervals?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Variety of drinks, including water?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Do you avoid sugary drinks?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Less than 2-3 / 3-4 units alcohol?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Skip breakfast more than once a week?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Skip lunch more than once a week?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Skip evening meals more than once a week?", false);
        dao.addQuestion(form_id, FormType.FT_BOOLEAN, "Skip meals and snack instead most days?", false);


        form_id = dao.addNewForm(Main_GUI.getCurrentUser(), "Physical Evaluation");

        dao.addQuestion(form_id, FormType.FT_INT, "Heart Rate / At Rest (beats/min)", 0);
        dao.addQuestion(form_id, FormType.FT_INT, "Heart Rate / During Exercise (beats/min)", 0);
        dao.addQuestion(form_id, FormType.FT_INT, "Heart Rate / After Exercise (beats/min)", 0);
        dao.addQuestion(form_id, FormType.FT_INT, "Systolic BP / At Rest (mmHg)", 0);
        dao.addQuestion(form_id, FormType.FT_INT, "Systolic BP / During Exercise (mmHg)", 0);
        dao.addQuestion(form_id, FormType.FT_INT, "Systolic BP / After Exercise (mmHg)", 0);
    }

    private void ReferalsWindow() {
        PopulatePatients();
        String[] columns = {"User ID", "Forename", "Surname", "Search"};
        //Object[][] obj = new Object[][]{{"1", "a", "b", "button 1"}, {"2", "c", "d", "button 2"}, {"3", "e", "f", "button 3"},{"g", "h", "i", "button 3"}};
        JTable populatePatients = new JTable();
        populatePatients.setModel(new DefaultTableModel(patientReferals, columns) {
            /*
            public boolean isCellEditable(int row, int column) {
                return false;
            }
            */
        });
        populatePatients.getColumn("Search").setCellRenderer(new ButtonRenderer());
        populatePatients.getColumn("Search").setCellEditor(new ButtonEditor(new JCheckBox()));

        scrollPane = new JScrollPane(populatePatients, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        Dimension d = scrollPane.getPreferredSize();
        d.setSize(d.width, d.height * 0.95);
        scrollPane.setPreferredSize(d);

        controlPanel.add(scrollPane);

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int i;
                i = populatePatients.getSelectedRow();
                try {
                    PatientOpenButtonFunction(i);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void PopulatePatients() {

        java.util.List<Integer> referrals = new ArrayList<>();
        UserDao uDao = new UserDao();
        PatientDao pDao = new PatientDao();

        referrals = uDao.getReferralByRD(userId);

        int patientsNum = referrals.size();

        patientReferals = new Object[patientsNum][4];

        for (int i = 0; i < patientsNum; i++) {
            Patient temp = pDao.searchPatientById(referrals.get(i)).get(0);
            patients.add(temp);

            patientReferals[i][0] = referrals.get(i);
            patientReferals[i][1] = patients.get(i).getPatientFirstName();
            patientReferals[i][2] = patients.get(i).getPatientLastName();
            patientReferals[i][3] = "--->";
        }
    }

    class ButtonRenderer extends JButton implements TableCellRenderer {

        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {

        private String label;

        public ButtonEditor(JCheckBox checkBox) {

            super(checkBox);

        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

            label = (value == null) ? "" : value.toString();
            button.setText(label);

            return button;

        }
    }

    /**
     * Action Listener that looks out for button presses in RD_GUI
     */
    class ButtonClickListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            //GP_GUI gp_gui = new GP_GUI();
            //GP_Register_GUI gp_register_gui = new GP_Register_GUI();

            if (command.equals("Default")) {
                //Do nothing
            } else if (command.equals("RD_Back")) {
                GoBackToMainGUI();
            } else {
                System.out.println("No Input for button");
            }
        }
    }
}
