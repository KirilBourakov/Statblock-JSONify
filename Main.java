import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import helpers.Runner;

public class Main {
    static JFrame frame = new JFrame();;
    static HashMap<String, JTextField> textfields = new HashMap<>();
    static Runner runner = new Runner();

    public static void main(String[] args){
        createGUI();
    }

    private static void createGUI(){
        frame.setLayout(new GridLayout(3,1));

        CreateFilePathInput("Input Filename");
        CreateFilePathInput("Output Filename");
        CreateSubmitButton();
        

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private static void CreateFilePathInput(String labelText){
        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0,30,10,30));
        inputPanel.setLayout(new GridLayout(1,1));
        
        JLabel label = new JLabel(labelText);
        inputPanel.add(label);

        JTextField inputFile = new JTextField(20);
        inputPanel.add(inputFile);

        textfields.put(labelText, inputFile);
        frame.add(inputPanel);
    }

    private static void CreateSubmitButton(){
        JButton submit = new JButton("Submit");
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runner.runLogic(textfields.get("Input Filename").getText(), textfields.get("Output Filename").getText());
            }
        });

        frame.add(submit);
    }
}
