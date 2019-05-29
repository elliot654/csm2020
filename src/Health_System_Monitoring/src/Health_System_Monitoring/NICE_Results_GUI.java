package Health_System_Monitoring;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * 
 * @author Nick
 * 
 * GUI to display NICE test results
 */

public class NICE_Results_GUI {
	
	public static JFrame mainFrame;
	private static JPanel controlPanel, buttonPanel;
	private static JLabel heightResult, weightResult, ageResult, bpResult, hbaResult, acrResult, serumResult, tcResult, lipoResult, sexResult, smokeResult;
	private static String height, weight, age, systolic, diastolic, hba, acr, micromol, egfr, tc, lipo, sex, smoke;
	
	/**
	 * Building GUI for results
	 */
	public static void PrepareNiceResultsGUI() {
		
		mainFrame = new JFrame("NICE Results");
		mainFrame.setSize(500, 500);
		mainFrame.setLayout(new BorderLayout());
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		controlPanel = new JPanel();
		buttonPanel = new JPanel();
		
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		
		heightResult = new JLabel("Height: " + height + "cm");
		weightResult = new JLabel("Weight: " + weight + "kg");
		ageResult = new JLabel("Age: " + age + " years");
		sexResult = new JLabel("Sex: " + sex);
		bpResult = new JLabel("Blood Pressure: " + systolic + "/" + diastolic + "mmHg");
		smokeResult = new JLabel("Smoking Status: " + smoke);
		hbaResult = new JLabel("Glycosylated Haemoglobin (HbA): " + hba + "% HbA1c");
		acrResult = new JLabel("Urinary Albumin: " + acr + "mg/mmol");
		serumResult = new JLabel("Serum Creatine: " + micromol + "micromol/L" + egfr + "eGFR");
		tcResult = new JLabel("Total Cholesterol: " + tc);
		lipoResult = new JLabel("Lipoproteins: " + lipo + "mmol/L");
		
		controlPanel.add(heightResult);
		controlPanel.add(weightResult);
		controlPanel.add(ageResult);
		controlPanel.add(sexResult);
		controlPanel.add(bpResult);
		controlPanel.add(smokeResult);
		controlPanel.add(hbaResult);
		controlPanel.add(acrResult);
		controlPanel.add(serumResult);
		controlPanel.add(tcResult);
		controlPanel.add(lipoResult);
		
		NiceResultsBackButton();
		
		mainFrame.add(controlPanel, BorderLayout.CENTER);
		mainFrame.add(buttonPanel, BorderLayout.SOUTH);
		mainFrame.setVisible(true);
	}
	
	/**
	 * Set results to labels
	 */
	public void SetResults(String height, String weight, String age, String systolic, String diastolic, String hba, String acr, String micromol, String egfr, String tc, String lipo, String sex, String smoke) {
		NICE_Results_GUI.height = height;
		NICE_Results_GUI.weight = weight;
		NICE_Results_GUI.age = age;
		NICE_Results_GUI.systolic = systolic;
		NICE_Results_GUI.diastolic = diastolic;
		NICE_Results_GUI.hba = hba;
		NICE_Results_GUI.acr = acr;
		NICE_Results_GUI.micromol = micromol;
		NICE_Results_GUI.egfr = egfr;
		NICE_Results_GUI.tc = tc;
		NICE_Results_GUI.lipo = lipo;
		NICE_Results_GUI.sex = sex;
		NICE_Results_GUI.smoke = smoke;
	}
	
	/**
	 * Return to NICE test GUI
	 */
	public static void NiceResultsBackButton() {
		JButton niceResultsBackBtn = new JButton("Back");
		niceResultsBackBtn.setActionCommand("Nice_Results_Back");
		niceResultsBackBtn.addActionListener((ActionListener) new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.setVisible(false);
				NICE_GUI.mainFrame.setVisible(true);
			}
		});
		buttonPanel.add(niceResultsBackBtn);
	}
}
