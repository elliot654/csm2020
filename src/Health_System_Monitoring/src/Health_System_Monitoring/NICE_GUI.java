package Health_System_Monitoring;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

/**
 * @author - Nick Maslin
 * 
 * GUI for NICE test
 * 
 */

public class NICE_GUI {
	public static JFrame mainFrame;
	private JPanel welcomePanel, controlPanel, bmiPanel, bpPanel, smokingPanel, hbaPanel, urinaryPanel,
			serumPanel, cholPanel, eyePanel, footPanel;
	private JLabel welcomeLbl;
	private JTextField heightTxt, weightTxt, ageTxt, systolicTxt, diastolicTxt, hbaTxt, acrTxt, egfrTxt,
			micromolTxt, tcTxt, lipoTxt;
	private ButtonGroup sexGroup, smokeGroup;
	private JCheckBox kidneyDamCheck, eyeDamCheck, cercDamCheck, visionCheck, retinaCheck, detatchCheck, rubeosisCheck, senseCheck, deformCheck, pulseCheck, shoeCheck;
	private String heightInput, weightInput, ageInput, systolicInput, diastolicInput, hbaInput, acrInput, micromolInput, egfrInput, tcInput, lipoInput, sexInput, smokeInput;
	private int resultId, patientId, userId, age, systolic, diastolic, serum, weight, height;
	private Date date;
	private Boolean smoker, kidneyDamage, eyeDamage, cercbroDamage, visionLoss, eyeHaemorrage, retina, rubeosis, sensation, deformity, palpatation, shoes;
	private BigDecimal haemoglobin, urinary, egfr, cholesterol, ldl;
	
	/**
	 * Build the GUI for the NICE test
	 */
	public void prepareNiceGUI() {

		mainFrame = new JFrame("NICE Test");
		mainFrame.setSize(600, 550);
		mainFrame.setLayout(new BorderLayout());
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		welcomePanel = new JPanel();
		welcomePanel.setLayout(new FlowLayout());

		welcomeLbl = new JLabel("NICE Test");

		welcomePanel.add(welcomeLbl);

		controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout());

		// BMI Panel
		bmiPanel = new JPanel();
		bmiPanel.setLayout(new FlowLayout());
		bmiPanel.setPreferredSize(new Dimension(150, 140));
		TitledBorder bmiBorder = new TitledBorder("BMI");
		SetBorderTitle(bmiBorder);
		bmiPanel.setBorder(bmiBorder);

		JLabel heightLbl = new JLabel("Height: ");
		heightTxt = new JTextField(3);
		JLabel cmLbl = new JLabel("cm");
		bmiPanel.add(heightLbl);
		bmiPanel.add(heightTxt);
		bmiPanel.add(cmLbl);

		JLabel weightLbl = new JLabel("Weight: ");
		weightTxt = new JTextField(4);
		
		JLabel kgLbl = new JLabel("kg");
		bmiPanel.add(weightLbl);
		bmiPanel.add(weightTxt);
		bmiPanel.add(kgLbl);

		JLabel ageLbl = new JLabel("Age: ");
		ageTxt = new JTextField(3);
		JLabel yearLbl = new JLabel("years");
		bmiPanel.add(ageLbl);
		bmiPanel.add(ageTxt);
		bmiPanel.add(yearLbl);

		JRadioButton maleBtn = new JRadioButton("Male");
		JRadioButton femaleBtn = new JRadioButton("Female");
		sexGroup = new ButtonGroup();
		sexGroup.add(maleBtn);
		sexGroup.add(femaleBtn);
		bmiPanel.add(maleBtn);
		bmiPanel.add(femaleBtn);

		controlPanel.add(bmiPanel);

		// Blood Pressure Panel
		bpPanel = new JPanel();
		bpPanel.setLayout(new FlowLayout());
		bpPanel.setPreferredSize(new Dimension(200, 150));
		TitledBorder bpBorder = new TitledBorder("Blood Pressure");
		SetBorderTitle(bpBorder);
		bpPanel.setBorder(bpBorder);

		kidneyDamCheck = new JCheckBox("Kidney Damage", false);
		eyeDamCheck = new JCheckBox("Eye Damage", false);
		cercDamCheck = new JCheckBox("Cercbrovascluar Damage", false);

		systolicTxt = new JTextField(3);
		JLabel slashLbl = new JLabel("/");
		diastolicTxt = new JTextField(3);
		JLabel mmhgLbl = new JLabel("mmHg");

		bpPanel.add(kidneyDamCheck);
		bpPanel.add(eyeDamCheck);
		bpPanel.add(cercDamCheck);
		bpPanel.add(systolicTxt);
		bpPanel.add(slashLbl);
		bpPanel.add(diastolicTxt);
		bpPanel.add(mmhgLbl);
		controlPanel.add(bpPanel);

		// Smoking Panel
		smokingPanel = new JPanel();
		smokingPanel.setLayout(new FlowLayout());
		TitledBorder smokingBorder = new TitledBorder("Smoking Status");
		SetBorderTitle(smokingBorder);
		smokingPanel.setBorder(smokingBorder);

		JRadioButton smokeBtn = new JRadioButton("Smoker");
		JRadioButton nonSmokeBtn = new JRadioButton("Non-Smoker");
		smokeGroup = new ButtonGroup();
		smokeGroup.add(smokeBtn);
		smokeGroup.add(nonSmokeBtn);
		smokingPanel.add(smokeBtn);
		smokingPanel.add(nonSmokeBtn);
		controlPanel.add(smokingPanel);

		// HBA Panel
		hbaPanel = new JPanel();
		hbaPanel.setLayout(new FlowLayout());
		hbaPanel.setPreferredSize(new Dimension(200, 50));
		TitledBorder hbaBorder = new TitledBorder("Glycosylated Haemoglobin (HbA)");
		SetBorderTitle(hbaBorder);
		hbaPanel.setBorder(hbaBorder);

		hbaTxt = new JTextField(3);
		JLabel hbaLbl = new JLabel("% HbA1C");

		hbaPanel.add(hbaTxt);
		hbaPanel.add(hbaLbl);
		controlPanel.add(hbaPanel);

		// Urinary Panel
		urinaryPanel = new JPanel();
		urinaryPanel.setLayout(new FlowLayout());
		TitledBorder urinaryBorder = new TitledBorder("Urinary Albumin");
		SetBorderTitle(urinaryBorder);
		urinaryPanel.setBorder(urinaryBorder);

		acrTxt = new JTextField(3);
		JLabel acrLbl = new JLabel("mg/mmol");

		urinaryPanel.add(acrTxt);
		urinaryPanel.add(acrLbl);
		controlPanel.add(urinaryPanel);

		// Serum Panel
		serumPanel = new JPanel();
		serumPanel.setLayout(new FlowLayout());
		serumPanel.setPreferredSize(new Dimension(125, 75));
		TitledBorder serumBorder = new TitledBorder("Serum Creatine");
		SetBorderTitle(serumBorder);
		serumPanel.setBorder(serumBorder);

		micromolTxt = new JTextField(3);
		JLabel micromolLbl = new JLabel("micromol/L");
		egfrTxt = new JTextField(2);
		JLabel egfrLbl = new JLabel("eGFR");

		serumPanel.add(micromolTxt);
		serumPanel.add(micromolLbl);
		serumPanel.add(egfrTxt);
		serumPanel.add(egfrLbl);
		controlPanel.add(serumPanel);

		// Cholesterol Panel
		cholPanel = new JPanel();
		cholPanel.setLayout(new FlowLayout());
		cholPanel.setPreferredSize(new Dimension(200, 100));
		TitledBorder cholBorder = new TitledBorder("Cholestrerol");
		SetBorderTitle(cholBorder);
		cholPanel.setBorder(cholBorder);

		JLabel tcLbl = new JLabel("Total Cholesterol: ");
		tcTxt = new JTextField(3);
		JLabel lipoLbl = new JLabel("Lipoproteins: ");
		lipoTxt = new JTextField(3);
		JLabel mmolLbl = new JLabel("mmol/L");

		cholPanel.add(tcLbl);
		cholPanel.add(tcTxt);
		cholPanel.add(lipoLbl);
		cholPanel.add(lipoTxt);
		cholPanel.add(mmolLbl);
		controlPanel.add(cholPanel);

		// Eye Examination Panel
		eyePanel = new JPanel();
		eyePanel.setLayout(new FlowLayout());
		eyePanel.setPreferredSize(new Dimension(250, 125));
		TitledBorder eyeBorder = new TitledBorder("Eye Examination");
		SetBorderTitle(eyeBorder);
		eyePanel.setBorder(eyeBorder);

		visionCheck = new JCheckBox("Sudden loss of vision", false);
		retinaCheck = new JCheckBox("Pre-retinal or vitreous haemoerhage", false);
		detatchCheck = new JCheckBox("Retinal detatchment", false);
		rubeosisCheck = new JCheckBox("Rubeosis", false);

		eyePanel.add(visionCheck);
		eyePanel.add(retinaCheck);
		eyePanel.add(detatchCheck);
		eyePanel.add(rubeosisCheck);
		controlPanel.add(eyePanel);

		// Foot Examination Panel
		footPanel = new JPanel();
		footPanel.setLayout(new FlowLayout());
		TitledBorder footBorder = new TitledBorder("Foot Examination");
		SetBorderTitle(footBorder);
		footPanel.setBorder(footBorder);

		senseCheck = new JCheckBox("Lack of sensation", false);
		deformCheck = new JCheckBox("Deformity", false);
		pulseCheck = new JCheckBox("Palpatation of foot pulse", false);
		shoeCheck = new JCheckBox("Inappropriate footwear", false);

		footPanel.add(senseCheck);
		footPanel.add(deformCheck);
		footPanel.add(pulseCheck);
		footPanel.add(shoeCheck);
		controlPanel.add(footPanel);

		NiceBackButton();
		SubmitButton();

		mainFrame.add(welcomePanel, BorderLayout.NORTH);
		mainFrame.add(controlPanel, BorderLayout.CENTER);
		mainFrame.setVisible(true);
	}

	/**
	 * Return to the GP GUI
	 */
	public void NiceBackButton() {
		JButton niceBackBtn = new JButton("Back");
		niceBackBtn.setActionCommand("Nice_Back");
		niceBackBtn.addActionListener(new Main_GUI());
		controlPanel.add(niceBackBtn);
	}

	/*
	 * Set the border title for a panel within the frame
	 * @param border
	 */
	public void SetBorderTitle(TitledBorder border) {
		border.setTitleJustification(TitledBorder.LEFT);
		border.setTitlePosition(TitledBorder.TOP);
	}

	/*
	 * Takes user to Patient GUI
	 */
	public void GoToPatientGUI() {
		mainFrame.setVisible(false);
		Patient_GUI.mainFrame.setVisible(true);
	}

	/*
	 * Gets results from NICE test and displayed them in NICE_Results_GUI 
	 */
	public void GoToNiceResults() {

		mainFrame.setVisible(false);
		NICE_Results_GUI results = new NICE_Results_GUI();
		results.SetResults(heightInput, weightInput, ageInput, systolicInput, diastolicInput, hbaInput, acrInput, micromolInput, egfrInput, tcInput, lipoInput, sexInput, smokeInput);
		NICE_Results_GUI.PrepareNiceResultsGUI();
	}
	
	/**
	 * Takes the radio button group and returns selected value
	 * 
	 * @param buttonGroup
	 * @return selected button from group
	 * 
	 */
	public String GetButtonGroupSelection(ButtonGroup buttonGroup) {
		for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
			AbstractButton button = buttons.nextElement();
			
			if(button.isSelected()) {
				return button.getText();
			}
		}
		
		return null;
	}
	
	/*
	 * If the checkbox button has been selected, the corresponding boolean value is set to true
	 */
	public void GetCheckBool(JCheckBox check, Boolean bool) {
		if(check.isSelected()) {
			bool = true;
		} else {
			bool = false;
		}
	}
	
	public void SubmitButton() {
		JButton submitBtn = new JButton("Submit");
		/*
		 * Validate the nice test. This validation forces the user to enter all required
		 * field in the NICE Test. These include the JTextField(s) and JComboBox(s)
		 * Created by: Safouh
		 */
		submitBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Covert textfields to either string, int or bigdecimal values
				height = Integer.parseInt(heightTxt.getText());
				weight = Integer.parseInt(weightTxt.getText());
				age = Integer.parseInt(ageTxt.getText());
				systolic = Integer.parseInt(systolicTxt.getText());
				diastolic = Integer.parseInt(diastolicTxt.getText());
				haemoglobin = new BigDecimal(hbaTxt.getText());
				urinary = new BigDecimal(acrTxt.getText());
				micromolInput = micromolTxt.getText(); // This has not been included -- might need to discuss with Matthew
				egfr = new BigDecimal(egfrTxt.getText());
				cholesterol = new BigDecimal(tcTxt.getText());
				ldl = new BigDecimal(lipoTxt.getText());
				sexInput = GetButtonGroupSelection(sexGroup);
				smokeInput = GetButtonGroupSelection(smokeGroup);
				
				// Radio Button boolean
				if (smokeInput == "Smoker") {
					smoker = true;
				} else if (smokeInput == "Non-Smoker") {
					smoker = false;
				}
				
				// Check box booleans
				GetCheckBool(kidneyDamCheck, kidneyDamage);
				GetCheckBool(eyeDamCheck, eyeDamage);
				GetCheckBool(cercDamCheck, cercbroDamage);
				GetCheckBool(visionCheck, visionLoss);
				GetCheckBool(retinaCheck, eyeHaemorrage);
				GetCheckBool(detatchCheck, retina);
				GetCheckBool(rubeosisCheck, rubeosis);
				GetCheckBool(senseCheck, sensation);
				GetCheckBool(deformCheck, deformity);
				GetCheckBool(pulseCheck, palpatation);
				GetCheckBool(shoeCheck, shoes);
				
				resultId = 1;
				patientId = 1;
				userId = 1;
				
				if (heightInput.isEmpty() || weightInput.isEmpty() || ageInput.isEmpty()
						|| systolicInput.isEmpty() || diastolicInput.isEmpty()
						|| hbaInput.isEmpty() || acrInput.isEmpty() || egfrInput.isEmpty()
						|| micromolInput.isEmpty() || tcInput.isEmpty() || lipoInput.isEmpty()
						|| sexGroup.isSelected(null) || smokeGroup.isSelected(null)) {
					JOptionPane.showMessageDialog(null, "Please enter all required fields");
				} else {

						PatientDaoInterface patientDao = new PatientDao();
						if(patientDao.addNiceResults(patientId, userId, sexInput, age, date, height, weight, systolic,
								diastolic, smoker, haemoglobin, urinary, serum, egfr, cholesterol, 
								ldl, kidneyDamage, eyeDamage, cercbroDamage, visionLoss, eyeHaemorrage,
								retina, rubeosis, sensation, deformity, palpatation, shoes)) {
							JOptionPane.showMessageDialog(null, "SUCCESS! All fields have been saved.");
						} else {
							JOptionPane.showMessageDialog(null, "Error!");
						}

					GoToNiceResults();
				}

			}
		});

		controlPanel.add(submitBtn);
	}
	
}
