package Health_System_Monitoring;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.print.*;
import java.util.ArrayList;

public class Printer implements Printable {

    private ArrayList<String> printList = new ArrayList<String>();
    private int y;

    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        if (page > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D)g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        y = 50;

        for (int i = 0; i < printList.size(); i++){
            g.drawString(printList.get(i), 50, y += 15);
        }

        return PAGE_EXISTS;
    }

    public void setString(String print){
       this.printList.add(print);
    }
}
