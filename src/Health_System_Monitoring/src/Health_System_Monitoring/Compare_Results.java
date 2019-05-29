package Health_System_Monitoring;

import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class Compare_Results {

    private static JRadioButton weightBtn, bpBtn, haemoglobinBtn, urinaryBtn, serumBtn, cholesterolBtn;
    private static JPanel panel, north, center;

    public static void DisplayPanel(int patientId) {
        JLabel message = new JLabel("Select which graph to display:");

        weightBtn = new JRadioButton("Weight");
        bpBtn = new JRadioButton("Blood Pressure");
        haemoglobinBtn = new JRadioButton("Haemoglobin");
        urinaryBtn = new JRadioButton("Urinary Albumin");
        serumBtn = new JRadioButton("Serum Creatine");
        cholesterolBtn = new JRadioButton("Cholesterol");

        panel = new JPanel();
        panel.setLayout(new BorderLayout());

        north = new JPanel();
        center = new JPanel();

        north.add(message);
        center.add(weightBtn);
        center.add(bpBtn);
        center.add(haemoglobinBtn);
        center.add(urinaryBtn);
        center.add(serumBtn);
        center.add(cholesterolBtn);

        panel.add(north, BorderLayout.NORTH);
        panel.add(center, BorderLayout.CENTER);

        ButtonGroup group = new ButtonGroup();
        group.add(weightBtn);
        group.add(bpBtn);
        group.add(haemoglobinBtn);
        group.add(urinaryBtn);
        group.add(serumBtn);
        group.add(cholesterolBtn);

        JOptionPane.showMessageDialog(null, panel);

//        FormDao formDao = FormJDBC.getDAO();

        if (weightBtn.isSelected()) {
            System.out.println("weight");
            Results_Graph graph = new Results_Graph("Weight", "Dummy Weight Chart", "Date", "kg");
            graph.pack();
            RefineryUtilities.centerFrameOnScreen(graph);
            graph.setVisible(true);

//            Map<java.sql.Date, Collection<FormElement>> map = formDao.getSubmissionsByDate(3, patientId);
//
//            for(Map.Entry<java.sql.Date, Collection<FormElement>> entry : map.entrySet())
//            {
//                java.sql.Date date = entry.getKey();
//
//                String dateString = date.toString();
//
//                Collection<FormElement> coll = entry.getValue();
//
//                for(FormElement fe : coll)
//                {
//                    switch(fe.type)
//                    {
//                        case FT_INT:
//                        {
//                            Integer p  = (Integer)fe.value;
//                            System.out.println(p);
//                        }
//                        break;
//                    }
//                }
//            }
//
//            System.out.println(formDao.getSubmissionsByDate(3, 1));

        } else if (bpBtn.isSelected()){
            System.out.println("blood pressure");
            Results_Graph graph = new Results_Graph("Blood Pressure Chart", "Patients Blood Pressure Over Time", "Date", "mmHg");
            graph.pack();
            RefineryUtilities.centerFrameOnScreen(graph);
            graph.setVisible(true);
        } else if (haemoglobinBtn.isSelected()) {
            System.out.println("haemoglobin");
            Results_Graph graph = new Results_Graph("Haemoglobin Chart", "Patients Haemoglobin Count Over Time", "Date", "% HbA1c");
            graph.pack();
            RefineryUtilities.centerFrameOnScreen(graph);
            graph.setVisible(true);
        } else if (urinaryBtn.isSelected()) {
            System.out.println("urinary");
            Results_Graph graph = new Results_Graph("Urinary Albumin Chart", "Patients Urinary Albumin Over Time", "Date", "mg/mmol");
            graph.pack();
            RefineryUtilities.centerFrameOnScreen(graph);
            graph.setVisible(true);
        } else if (serumBtn.isSelected()) {
            System.out.println("serum creatine");
            Results_Graph graph = new Results_Graph("Serum Creatine Chart", "Patients Serum Creatine Over Time", "Date", "micromol/L");
            graph.pack();
            RefineryUtilities.centerFrameOnScreen(graph);
            graph.setVisible(true);
        } else if (cholesterolBtn.isSelected()){
            System.out.println("cholesterol");
            Results_Graph graph = new Results_Graph("Cholesterol Chart", "Patients Cholesterol Over Time", "Date", "Total Cholesterol");
            graph.pack();
            RefineryUtilities.centerFrameOnScreen(graph);
            graph.setVisible(true);
        }
    }
}
