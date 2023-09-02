package ngit.maker.recorder.workflow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class WorkflowRecorderPane extends JFrame {
    private String createdTime;
    private JTextArea area;
    private int myPlace;

    public WorkflowRecorderPane(List<WorkflowRecorderPane> recorders) {
        defaultInit(recorders);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setSize(this.getSize());
        panel.setLocation(0, 0);
        addTextArea(panel);

        add(panel, BorderLayout.CENTER);

        setTitle(createdTime);
        setVisible(true);
    }

    public WorkflowRecorderPane(List<WorkflowRecorderPane> recorders, String createdTime) {
        this.createdTime = createdTime;
        defaultInit(recorders);
        timeInit();

        JPanel panel = new JPanel(new BorderLayout());
        panel.setSize(this.getSize());
        panel.setLocation(0, 0);
        addTextArea(panel);

        add(panel, BorderLayout.CENTER);

        setTitle(createdTime);
        setVisible(true);
    }

    public void defaultInit(List<WorkflowRecorderPane> recorders) {
        setSize(200, 100);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setLayout(new BorderLayout());

        timeInit();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int count = 0;
                if (!recorders.isEmpty()) {
                    for (WorkflowRecorderPane r : recorders) {
                        if (Objects.equals(r.createdTime, createdTime)) {
                            recorders.remove(count);
                            break;
                        }
                        count++;
                    }

                    flushInterface(recorders);
                    super.windowClosing(e);
                }
            }
        });
    }

    public void timeInit() {
        SimpleDateFormat sdf2 = new SimpleDateFormat("[yyyy-MM-dd_HH-mm-ss-SSS]");
        createdTime = sdf2.format(new Date());
    }


    public void addTextArea(JPanel panel) {
        area = new JTextArea();
        area.setSize(panel.getSize());
        area.setLocation(0, 0);
        panel.add(area, BorderLayout.CENTER);
    }

    @Override
    public String toString() {
        return area.getText();
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void flushInterface(List<WorkflowRecorderPane> recorders) {
        int count = 1;
        if (!recorders.isEmpty()) {
            for (WorkflowRecorderPane r : recorders) {
                if (r.myPlace != count) {
                    r.myPlace = count;
                    r.repaint();
                    break;
                }
                count++;
            }
        }
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(92, 92, 92, 132));
        g2.setFont(new Font("Inconsolata", Font.BOLD, 20));
        g2.drawString(String.valueOf(myPlace), 10, 30);
    }

    @Override
    public void repaint() {
        super.repaint();
        Graphics2D g2 = (Graphics2D) getGraphics();
        g2.drawString(String.valueOf(myPlace), 10, 30);
    }
}
