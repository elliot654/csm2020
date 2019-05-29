package Health_System_Monitoring;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Main_GUI implements KeyListener, ActionListener {
    public static JFrame mainFrame;
    private JLabel usernameLabel, passwordLabel;
    private JPanel controlPanel;
    private static Point windowsPosition;

    private static JTextField usernameTextField;

    private static JPasswordField passwordField;

    private static User user;

    public Main_GUI() {
        mainFrame = new JFrame("Login");
        mainFrame.setSize(250, 175);
        mainFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                System.exit(0);
            }
        });

        controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());

        usernameLabel = new JLabel();
        usernameLabel.setText("Username: ");

        passwordLabel = new JLabel();
        passwordLabel.setText("Password: ");

        usernameTextField = new JTextField("");
        usernameTextField.setPreferredSize(new Dimension(100, 25));
        usernameTextField.addKeyListener(this);

        passwordField = new JPasswordField("");
        passwordField.setPreferredSize(new Dimension(100, 25));
        passwordField.addKeyListener(this);

        controlPanel.add(usernameLabel);
        controlPanel.add(usernameTextField);
        controlPanel.add(passwordLabel);
        controlPanel.add(passwordField);

        loginButton();
        GetCenterPoint();

        controlPanel.addKeyListener(this);

        mainFrame.setLocation(windowsPosition);
        mainFrame.add(controlPanel);
        mainFrame.setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            loginFunction();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public static User getCurrentUser() {
        return user;
    }

    /*
    private method to validate login
    It opens the GP Gui if authentication is successful
     */
    private static void loginFunction() {
        String username = usernameTextField.getText();
        String userPassword = String.valueOf(passwordField.getPassword());

        UserDaoInterface uDao = (UserDaoInterface) new UserDao();

        user = uDao.checkCredentials(username, userPassword);

        if (user == null) {
            JOptionPane.showMessageDialog(null,"Incorrect username or password. Try again",
                    "Alert", JOptionPane.WARNING_MESSAGE);
        } else {
            if(user.getUserType().equals("gp")){
                GP_GUI gp_GUI = new GP_GUI();
                gp_GUI.prepareGPGUI();
            } else if (user.getUserType().equals("rd")){
                RD_GUI rd_gui = new RD_GUI();
                rd_gui.prepareRDGUI();
            } else if (user.getUserType().equals("sc")){
                SC_GUI sc_gui = new SC_GUI();
                sc_gui.prepareSCGUI();
            }
        }
    }

    /**
     * Creates GUI for login button
     */
    public void loginButton() {
        JButton AddButton = new JButton("Login");
        AddButton.setActionCommand("Login");
        AddButton.addActionListener(this);
        controlPanel.add(AddButton);
    }

    //Temporary button that will be removed later on in development
    private void bypassSCButton() {
        JButton SCButton = new JButton("Bypass SC");
        SCButton.setActionCommand("SCBypass");
        SCButton.addActionListener(this);
        controlPanel.add(SCButton);
    }

    //Temporary button that will be removed later on in development
    private void bypassRDButton() {
        JButton RDButton = new JButton("Bypass RD");
        RDButton.setActionCommand("RDBypass");
        RDButton.addActionListener(this);
        controlPanel.add(RDButton);
    }

    //Temporary button that will be removed later on in development
    private void bypassGPButton() {
        JButton GPButton = new JButton("Bypass GP");
        GPButton.setActionCommand("GPBypass");
        GPButton.addActionListener(this);
        controlPanel.add(GPButton);
    }

    public static void setWindowPosition(double x, double y){
        Point pt = new Point((int)Math.round(x),(int)Math.round(y));
        windowsPosition = pt;
    }

    public static Point getWindowPosition(){
        return windowsPosition;
    }

    private void GetCenterPoint(){
        int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;

        Point pt = new Point((screenWidth / 2) - 125, (screenHeight / 2) - 80);
        windowsPosition = pt;
    }

    /**
     * Action Listener that looks out for button presses in Main_GUI
     */
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            Patient_GUI patient_gui = new Patient_GUI();
            GP_Register_GUI gp_register_gui = new GP_Register_GUI();
            NICE_GUI nice_gui = new NICE_GUI();
            SC_GUI sc_gui = new SC_GUI();
            GP_GUI gp_gui = new GP_GUI();
            RD_GUI rd_gui = new RD_GUI();

        //get the text value from the username and pwd text field
        //converted to string type

            if (command.equals("Default")) {
                //Do Something?
            } else if (command.equals("Login")) {
                loginFunction();
            } else if (command.equals("SCBypass")) {
                sc_gui.prepareSCGUI();
            } else if (command.equals("GPBypass")) {
                setWindowPosition(mainFrame.getLocation().x,mainFrame.getLocation().y);
                gp_gui.prepareGPGUI();
            } else if (command.equals("RDBypass")) {
                setWindowPosition(mainFrame.getLocation().x,mainFrame.getLocation().y);
                rd_gui.prepareRDGUI();
            } else if (command.equals("Nice_Back")) {
                nice_gui.GoToPatientGUI();
            } else {
                System.out.println("No Input for button");
            }
        }
    }
