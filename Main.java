import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FlowLayout;
import java.awt.Dimension;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import helpers.Logic;

public class Main {
    static JFrame frame = new JFrame();
    static HashMap<String, JTextField> textfields = new HashMap<>();
    static Logic logic = new Logic();
    static JFileChooser fileChooser = new JFileChooser(".");

    public static void main(String[] args){
        createGUI();
    }

    private static void createGUI(){
        frame.setLayout(new GridLayout(5,1));

        createFilePathInput("Input file", "file", "input");
        JLabel inputDisclaimer = new JLabel("The input file should be a .txt, with statblocks using the homebrewery markdown style");
        inputDisclaimer.setBorder(new EmptyBorder(0, 10, 10, 10));
        frame.add(inputDisclaimer);

        createFilePathInput("Output location", "both" , "output");
        JLabel outputDisclaimer = new JLabel("The output location can be either a file or a folder. If it is a folder, the output will be done in that folder, in a file named output.json");
        outputDisclaimer.setBorder(new EmptyBorder(0, 10, 10, 10));
        frame.add(outputDisclaimer);

        createSubmitButton();
        

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private static void createFilePathInput(String labelText, String type, String hashName){
        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(BorderFactory.createEmptyBorder(0,30,0,30));
        inputPanel.setLayout(new BoxLayout(inputPanel,BoxLayout.X_AXIS));
        
        JLabel label = new JLabel(labelText);
        inputPanel.add(label);

        JTextField inputFile = new JTextField(20);
        inputPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        inputPanel.add(inputFile);

        JButton explorerButton = new JButton("Browse");
        explorerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createFileExplorer(type, hashName);
            }
        });

        inputPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        inputPanel.add(explorerButton);

        textfields.put(hashName, inputFile);
        frame.add(inputPanel);
    }

    private static void createFileExplorer(String type, String selectionTarget){
        if (type == "folder"){
            fileChooser.setDialogTitle("Select a Folder");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        } else if (type == "file") {
            fileChooser.setDialogTitle("Select a File");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        } else {
            fileChooser.setDialogTitle("Select a File or Folder");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        }

        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (selectionTarget.equals("output") && selectedFile.isDirectory()){
                Path combinedPath = Paths.get(selectedFile.getAbsolutePath()).resolve("output.json");
                selectedFile = combinedPath.toFile();
            }

            textfields.get(selectionTarget).setText(selectedFile.getAbsolutePath());
        }
    }

    private static void createSubmitButton(){
        JButton submit = new JButton("Submit");
        submit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean response = logic.txtToJSON(textfields.get("input").getText(), textfields.get("output").getText());
                if (response) {
                    JOptionPane.showMessageDialog(frame, "Success.", "Information", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "Something went wrong.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(submit);

        frame.add(buttonPanel);
    }
}
