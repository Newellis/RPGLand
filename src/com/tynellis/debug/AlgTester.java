package com.tynellis.debug;


import com.tynellis.debug.algs.AlgTest;
import com.tynellis.debug.algs.WorldGenTest;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class AlgTester extends JPanel implements ActionListener {

    private static JFrame frame;
    private static List<AlgTest> algs = new ArrayList<AlgTest>();

    public static void main(String[] args) {
        AlgTester tester = new AlgTester();
        frame = new JFrame();
        frame.setContentPane(tester);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tester.init();
        frame.setSize(new Dimension(700, 300));
        frame.setVisible(true);
    }

    private void init() {
        algs.add(new WorldGenTest());

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        List<String> algNames = new ArrayList<String>();
        for (AlgTest alg : algs) {
            algNames.add(alg.Name);
        }
        JComboBox list = new JComboBox(algNames.toArray());
        list.addActionListener(this);
        this.add(list);
        frame.setContentPane(this);
        frame.pack();
    }

    private void setContent(Container content) {
        this.add(content);
        frame.setContentPane(content);
        frame.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();
        String algName = (String) cb.getSelectedItem();
        System.out.println("Selected: " + algName);
        AlgTest run = algs.get(cb.getSelectedIndex());
        run.Start();
        run.setPreferredSize(frame.getSize());
        setContent(run.draw());
    }
}
