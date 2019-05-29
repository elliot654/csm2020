package Health_System_Monitoring;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Vector;

public class Form_GUI {
    public static JFrame mainFrame = null;
    private static JPanel titlepanel = new JPanel();
    private static JButton editbutton = new JButton("edit");
    private static JPanel editingpanel = new JPanel();
    private static JScrollPane scrollpanel = new JScrollPane();
    private static JPanel contentpanel = new JPanel();
    private static Vector<JButton> editButtonz = new Vector<JButton>();
    private static Vector<FormElement> formElements = new Vector<FormElement>();

    private static JTextField titleField = new JTextField();

    // convenience vectors to quickly access editable form elements
    private static Vector<JPanel> formPanels = new Vector<JPanel>();
    private static Vector<JTextField> formLabels = new Vector<JTextField>();
    private static Vector<JButton> formCloseButtons = new Vector<>();
    private static Vector<JComponent> formEntries = new Vector<JComponent>();

    private static boolean editMode = false;

    private static int formId; // form we're using
    private static int submissionId; // submission ID for the current answer set (outside Edit Mode)
    private static int patientID; // from the patient table

    private static FormDao dao;

    public static void prepareFormGUI() {
        mainFrame = new JFrame();
        mainFrame.setSize(500, 500);
        mainFrame.setLocation(Main_GUI.getWindowPosition().x -125, Main_GUI.getWindowPosition().y -165);
        titleField = new JTextField("Form Name:");
        titleField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String title = titleField.getText();
                dao.updateForm(formId, Main_GUI.getCurrentUser(), title);
            }
        });
        titlepanel.add(titleField);
        titlepanel.add(editbutton);

        titleField.setVisible(true);
        editbutton.setVisible(true);

        titlepanel.setVisible(true);

        editingpanel.setLayout(new BoxLayout(editingpanel, BoxLayout.Y_AXIS));
        editingpanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        for (FormType ft : FormType.values()) {
            if (ft != FormType.FT_ERROR) {
                String var = ft.toString();
                JButton nButton = new JButton(var);
                editButtonz.add(nButton);
                editingpanel.add(nButton);
                nButton.setEnabled(false);
                nButton.setVisible(false);
                nButton.setActionCommand(var);
                nButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        FormType f = FormType.fromString(e.getActionCommand().toLowerCase());
                        insertItem(f);
                    }
                });
            }
        }

        contentpanel.setLayout(new FlowLayout());
        contentpanel.setVisible(true);

        contentpanel.setPreferredSize(new Dimension(400,700));
        //scrollpanel = new JScrollPane(editingpanel);
        scrollpanel.setViewportView(contentpanel);
        contentpanel.setAutoscrolls(true);
        scrollpanel.setPreferredSize(new Dimension(400,500));
        scrollpanel.setVisible(true);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(titlepanel, BorderLayout.NORTH);
        mainFrame.add(editingpanel, BorderLayout.EAST);
        mainFrame.add(scrollpanel, BorderLayout.CENTER);
        mainFrame.setVisible(true);

        dao = FormJDBC.getDAO();
    }

    // Usage contexts:

    // GP - edit form (GP)
    // GP - data entry (GP)
    // RD - read data (GP)
    // RD - read data (SC)
    // RD - edit form (RD)
    // RD - data entry (RD)
    // SC - read data (RD)
    // SC - data entry (SC)
    //
    // States:
    // Edit Own Forms (No submission Id, No patient Id, readOnly = false, edit mode on)
    // Enter own Data (Submission Id, Patient Id, readonly = false, edit mode off)
    // Read Data From X (linked by patient id)


    // read data from x (if readOnly), or Enter Own data (if !readOnly)
    public static void getPatientForm(int form_id, int patient_id, int submission_id, boolean readOnly)
    {
        patientID = patient_id;
        submissionId = submission_id;

        setEditMode(false);

        openForm(form_id,readOnly);
    }

    private static void clearForm() {
        formElements = new Vector<FormElement>();

        formPanels.clear();
        formLabels.clear();
        formCloseButtons.clear();
        formEntries.clear();

        contentpanel.removeAll();
    }

    public static void openForm(int newFormId, boolean readOnly) {
        clearForm();

        formId = newFormId;

        Collection<FormElement> elements;

        if(editMode)
        {
            elements = dao.getFormElements(formId);
        }
        else {
            elements = dao.getSubmission(formId, submissionId);
        }

        for(FormElement element : elements)
        {
            formElements.add(element);
            contentpanel.add(buildPanel(element.type, element.question_id, element.label, element.value, element.default_value));
        }


        contentpanel.revalidate();
        scrollpanel.revalidate();

        JButton submitButton = new JButton("submit");
        submitButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                mainFrame.setVisible(false);
                //do a scheduling thing
            }
        });
        mainFrame.add(submitButton, BorderLayout.SOUTH);
        mainFrame.setVisible(true);

        updateEditMode();
    }

    public static void spawnEmptyForm() {
        editMode = true;
        updateEditMode();
        clearForm();

        formId = dao.addNewForm(Main_GUI.getCurrentUser(),"");

        mainFrame.setVisible(true);
        openForm(formId,false);
    }

    private static void updateEditMode() {

        // change the edit mode of the title field
        titleField.setEnabled(editMode);

        editbutton.setVisible(editMode);

        // flip the edit mode state of our panels
        for (int i = 0; i < formPanels.size(); i++) {
            // if we're not in edit mode, the label is read-only
            formLabels.get(i).setEditable(editMode);
            // if the entryElement is a single thing (like a text box) then if we're not in edit mode it's editable
            JComponent entryElement = formEntries.get(i);
            //entryElement.setEnabled(!editMode);
            // now check for children (eg, radio buttons)
            //for (Component c : entryElement.getComponents()) {
            //    c.setEnabled(!editMode);
            //}
            JButton button = formCloseButtons.get(i);
            button.setEnabled(editMode);
            button.setVisible(editMode);
        }

        // and make the edit buttonz materialise
        for (JButton button : editButtonz) {
            button.setVisible(editMode);
            button.setEnabled(editMode);
        }
    }

    public static void setEditMode(boolean edit) {
        editMode = edit;

        updateEditMode();
    }


    public static void insertItem(FormType f) {
        // we now know the Type of our new form element, so create an edit-mode version of that form element

        int newID = formElements.size();
        FormElement newElement = new FormElement();
        formElements.add(newElement);

        newElement.type = f;
        newElement.label = "New Question";
        switch(f){
            case FT_INT: {
                newElement.default_value = 0;
            }
            break;
            case FT_FLOAT: {
                newElement.default_value = 0.0f;
            }
            break;

            case FT_STRING: {
                newElement.default_value = "";
            }
            break;

            case FT_BOOLEAN: {
                newElement.default_value = false;
            }
        }

        newElement.value = newElement.default_value;

        addQuestionToFormDao(newID);

        JPanel newPanel = buildPanel(newElement.type, newElement.question_id, newElement.label, newElement.value, newElement.default_value);

        newPanel.setVisible(true);

        contentpanel.add(newPanel);
        contentpanel.updateUI();
    }

    /// ---------------------------------------------------------------------------------

    private static void updateFormDaoForElement(int elementId)
    {
        // we just updated an Question element, so we need to update the corresponding part of the Question table
        FormElement fe = formElements.get(elementId);
        dao.updateQuestion(fe.question_id, fe.type, fe.label,fe.default_value);
    }

    private static void addQuestionToFormDao(int elementId)
    {
        // we just added the question, so it needs to be added to the DAO
        FormElement fe = formElements.get(elementId);
        int id = dao.addQuestion(formId, fe.type, fe.label,fe.default_value);
        fe.question_id = id;
    }

    private static void removeQuestionFromFormDao(int elementId)
    {
        // we just removed a question, so it needs to be removed from the DAO
        FormElement fe = formElements.get(elementId);
        dao.removeQuestion(fe.question_id);
    }

    private static void newSubmissionToFormDao()
    {
        // create new submission and populate the table
        submissionId = dao.addSubmission(formId, Main_GUI.getCurrentUser().getUserId(), patientID, new java.sql.Date(System.currentTimeMillis()));

        // add default answers for all fields
        for(FormElement fe : formElements)
        {
             fe.value = fe.default_value;
             dao.addAnswer(fe.question_id, submissionId, fe.value );
        }
    }

    private static void updateSubmissionAnswer(FormElement answer)
    {
        dao.updateAnswer(answer.question_id, submissionId, answer.value);
    }

    /// --------------------------------------------------------------------------------

    private static void updateElementLabel(int elementId, String labelText)
    {
        FormElement fe = formElements.get(elementId);
        fe.label = labelText;
        updateFormDaoForElement(elementId);
    }

    private static void updateElementControl(int elementId, Object value)
    {
        // this can only happen when we're not in Edit Mode, which means we have a Submission ID
        FormElement fe = formElements.get(elementId);
        fe.value = value;

        updateSubmissionAnswer(fe);
    }

    /**
     * Create new empty panel in form
     * @param f FormType of the panel to create
     * @return the new JPanel
     */
    private static JPanel createPanel(FormType f, int questionId) {
        return buildPanel(f, questionId, "New Field", null, null);
    }

    private static JPanel buildPanel(FormType f, int questionId, String label, Object value, Object default_value)
    {
        JPanel newPanel = new JPanel();
        // set the name to the question id so we can retrieve it
        newPanel.setName(String.valueOf(questionId));

        // label is actually a text field; we turn off editable once we're done with Edit Mode
        JTextField labelField = new JTextField(label);
        labelField.setEnabled(true);
        labelField.setVisible(true);
        int newIndex = formLabels.size();
        labelField.setActionCommand(String.valueOf(newIndex));
        labelField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BigInteger indexB = new BigInteger(e.getActionCommand());
                int index = indexB.intValue();
                String text = formLabels.get(index).getText();
                updateElementLabel(index,text);
            }
        });
        newPanel.add(labelField);

        formLabels.add(labelField);

        if(!editMode && value == null)
        {
            value = default_value;
        }

        // now add the appropriate value control
        switch (f)
        {
            case FT_BOOLEAN: {
                // 2 strong button group, mutually exclusive
                ButtonGroup boolGroup = new ButtonGroup();
                JRadioButton yesButton = new JRadioButton("Yes");
                JRadioButton noButton = new JRadioButton("No");
                boolGroup.add(yesButton);
                boolGroup.add(noButton);

                // we add our buttons to a JPanel so we can enable/disable them together
                JPanel buttonPanel = new JPanel();
                buttonPanel.add(yesButton);
                buttonPanel.add(noButton);

                yesButton.setVisible(true);
                noButton.setVisible(true);

                if(editMode)
                {
                    yesButton.setSelected((Boolean)default_value);
                    noButton.setSelected(!(Boolean)default_value);
                }
                else
                {
                    yesButton.setSelected((Boolean)value);
                    noButton.setSelected(!(Boolean)value);
                }

                yesButton.setActionCommand(String.valueOf(newIndex));
                noButton.setActionCommand(String.valueOf(newIndex));

                yesButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        BigInteger indexB = new BigInteger(e.getActionCommand());
                        int index = indexB.intValue();
                        FormElement fe = formElements.get(index);
                        if(editMode)
                        {
                            fe.default_value = true;
                            dao.updateAnswer(fe.question_id, -1, true);
                        }
                        else {
                            fe.value = true;
                            if(dao.getAnswer(fe.type, fe.question_id, submissionId)==null)
                            {
                                dao.addAnswer(fe.question_id,submissionId, fe.value);
                            }
                            else {
                                dao.updateAnswer(fe.question_id, submissionId, fe.value);
                            }
                        }
                    }
                });

                noButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        BigInteger indexB = new BigInteger(e.getActionCommand());
                        int index = indexB.intValue();
                        FormElement fe = formElements.get(index);
                        if(editMode)
                        {
                            fe.default_value = false;
                            dao.updateAnswer(fe.question_id, -1, false);
                        }
                        else {
                            fe.value = false;
                            if(dao.getAnswer(fe.type, fe.question_id, submissionId)==null)
                            {
                                dao.addAnswer(fe.question_id,submissionId, fe.value);
                            }
                            else {
                                dao.updateAnswer(fe.question_id, submissionId, fe.value);
                            }
                        }
                    }
                });

                newPanel.add(buttonPanel);

                formEntries.add(buttonPanel);
            }
            break;
            case FT_INT: {
                // numerical input, only allow ints
                JTextField valueField = new JTextField();
                InputVerifier veri = new InputVerifier() {
                    @Override
                    public boolean verify(JComponent input) {
                        // try to create a BigDecimal with our text input
                        BigDecimal number;
                        try {
                            number = new BigDecimal(((JTextField) input).getText());
                        }
                        catch (NumberFormatException nfe)
                        {
                            // not a number, so reject
                            return false;
                        }

                        // if we got a number, use the intValueExact property of BigDecimal to test for an exact int
                        try {
                            int result = number.intValueExact();
                            return true;
                        }
                        catch (ArithmeticException e)
                        {
                            return false;
                        }

                    }
                };
                valueField.setInputVerifier(veri);

                valueField.setVisible(true);

                valueField.setActionCommand(String.valueOf(newIndex));
                valueField.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        BigInteger indexB = new BigInteger(e.getActionCommand());
                        int index = indexB.intValue();
                        FormElement fe = formElements.get(index);
                        JTextField tf = (JTextField)formEntries.get(index);
                        if(editMode)
                        {
                            fe.default_value = new BigDecimal(tf.getText()).intValue();
                            dao.updateAnswer(fe.question_id, -1, fe.default_value);
                        } else {
                            fe.value = new BigDecimal(tf.getText()).intValue();
                            if(dao.getAnswer(fe.type, fe.question_id, submissionId)==null)
                            {
                                dao.addAnswer(fe.question_id,submissionId, fe.value);
                            }
                            else {
                                dao.updateAnswer(fe.question_id, submissionId, fe.value);
                            }
                        }
                    }
                });

                if(editMode)
                {
                    valueField.setText(((Integer)default_value).toString());
                }
                else
                {
                    valueField.setText(((Integer) value).toString());
                }

                newPanel.add(valueField);
                formEntries.add(valueField);
            }
            break;
            case FT_FLOAT: {
                // numerical input, only allow floats
                JTextField valueField = new JTextField();
                InputVerifier veri = new InputVerifier() {
                    @Override
                    public boolean verify(JComponent input) {
                        // try to create a BigDecimal with our text input
                        BigDecimal number;
                        try {
                            number = new BigDecimal(((JTextField) input).getText());
                        }
                        catch (NumberFormatException nfe)
                        {
                            // not a number, so reject
                            return false;
                        }

                        // if we got to this point, we're an acceptable decimal value, so we are OK
                        return true;

                    }
                };
                valueField.setInputVerifier(veri);

                valueField.setVisible(true);

                valueField.setActionCommand(String.valueOf(newIndex));
                valueField.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        BigInteger indexB = new BigInteger(e.getActionCommand());
                        int index = indexB.intValue();
                        FormElement fe = formElements.get(index);
                        JTextField tf = (JTextField)formEntries.get(index);
                        if(editMode)
                        {
                            fe.default_value = new BigDecimal(tf.getText()).floatValue();
                            dao.updateAnswer(fe.question_id, -1, fe.default_value);
                        } else {
                            fe.value = new BigDecimal(tf.getText()).floatValue();
                            if(dao.getAnswer(fe.type, fe.question_id, submissionId)==null)
                            {
                                dao.addAnswer(fe.question_id,submissionId, fe.value);
                            }
                            else {
                                dao.updateAnswer(fe.question_id, submissionId, fe.value);
                            }
                        }
                    }
                });

                if(editMode)
                {
                    valueField.setText(((Float)default_value).toString());
                }
                else
                {
                    valueField.setText(((Float)value).toString());
                }

                newPanel.add(valueField);
                formEntries.add(valueField);
            }
            break;
            case FT_STRING: {
                // string input
                JTextField valueField = new JTextField();
                // no input verifier

                valueField.setVisible(true);

                valueField.setActionCommand(String.valueOf(newIndex));
                valueField.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        BigInteger indexB = new BigInteger(e.getActionCommand());
                        int index = indexB.intValue();
                        FormElement fe = formElements.get(index);
                        JTextField tf = (JTextField)formEntries.get(index);
                        if(editMode)
                        {
                            fe.default_value = tf.getText();
                            dao.updateAnswer(fe.question_id, -1, fe.default_value);
                        } else {
                            fe.value = tf.getText();
                            if(dao.getAnswer(fe.type, fe.question_id, submissionId)==null)
                            {
                                dao.addAnswer(fe.question_id,submissionId, fe.value);
                            }
                            else {
                                dao.updateAnswer(fe.question_id, submissionId, fe.value);
                            }
                        }
                    }
                });

                if(editMode)
                {
                    valueField.setText((String)default_value);
                }
                else
                {
                    valueField.setText((String)value);
                }

                newPanel.add(valueField);
                formEntries.add(valueField);
            }
            break;
        }

        JButton closeButton = new JButton("\u274c");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //int question_id = Integer.getInteger(newPanel.getName());
                contentpanel.remove(newPanel);
                dao.removeQuestion(questionId);
            }
        });
        formCloseButtons.add(closeButton);
        newPanel.add(closeButton);

        newPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        formPanels.add(newPanel);
        return newPanel;
    }
}
