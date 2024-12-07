package org.example;

import javax.swing.*;

import java.util.HashMap;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.CheckBox;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.AnchorPane;

import org.example.helpers.Logic;

public class GUI extends Application {
    static JFrame frame;
    static JFileChooser fileChooser;
    static JCheckBox OCRBox;
    static JLabel conversionStatus;

    static HashMap<String, JTextField> textfields = new HashMap<>();
    static Logic logic = new Logic();

    public void start(Stage primaryStage){
        System.out.println("RUNNING!");
        primaryStage.setTitle("Converter");

        Label inLabel = new Label("Input");
        TextField inField = new TextField();
        inField.setPrefSize(250, 30);
        Button inButton = new Button("file");
        Text inText = new Text("The input can be either a folder of a file. Files should be a .txt, or .jpg/.png using OCR.\nFolder inputs will try to convert all files within the folder.");

        Label outLabel = new Label("Output");
        TextField outField = new TextField();
        outField.setPrefSize(250, 30);
        Button outButton = new Button("file");
        Text outText = new Text("The output location can be either a file or a folder.\nIf it is a folder, the output will be done in that folder, in a file named output.json");

        CheckBox useOSR = new CheckBox();
        Label OSRlabel = new Label("Use OSR");

        Text status = new Text("Click to start");
        Button submit = new Button("Submit");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setGridLinesVisible(true);
        grid.setAlignment(Pos.CENTER);

        grid.add(inLabel, 0, 0);
        grid.add(inField, 1, 0);
        grid.add(inButton, 2, 0);

        grid.add(inText, 0, 1, 3, 1);

        grid.add(outLabel, 0, 2);
        grid.add(outField, 1, 2);
        grid.add(outButton, 2, 2);

        grid.add(outText, 0, 3, 3, 1);

        HBox pane = new HBox(useOSR, OSRlabel);
        grid.add(pane, 0,4);

        grid.add(status, 0, 5, 2, 1);
        grid.add(submit, 2, 5);

        Scene scene = new Scene (grid, 500, 270);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

//    private static void createGUI(){
//        frame.setLayout(new GridLayout(0,1));
//
//        createFilePathInput("Input", "both", "input");
//        JLabel inputDisclaimer = new JLabel("The input can be either a folder of a file. Files should be a .txt, or .jpg/.png using OCR. Folder inputs will try to convert all files within the folder.");
//        inputDisclaimer.setBorder(new EmptyBorder(0, 10, 10, 10));
//        frame.add(inputDisclaimer);
//
//        createFilePathInput("Output location", "both" , "output");
//        JLabel outputDisclaimer = new JLabel("The output location can be either a file or a folder. If it is a folder, the output will be done in that folder, in a file named output.json");
//        outputDisclaimer.setBorder(new EmptyBorder(0, 10, 10, 10));
//        frame.add(outputDisclaimer);
//
//        OCRBox = new JCheckBox("Use OCR");
//        frame.add(OCRBox);
//
//        createSubmitButton();
//
//        conversionStatus = new JLabel();
//        frame.add(conversionStatus);
//
//        frame.setTitle("Converter");
//        ImageIcon icon = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/images/convert.png")));
//        frame.setIconImage(icon.getImage());
//
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.pack();
//        frame.setResizable(false);
//        frame.setLocationRelativeTo(null);
//        frame.setVisible(true);
//    }
//
//    private static void createFilePathInput(String labelText, String type, String hashName){
//        JPanel inputPanel = new JPanel();
//        inputPanel.setBorder(BorderFactory.createEmptyBorder(0,30,0,30));
//        inputPanel.setLayout(new BoxLayout(inputPanel,BoxLayout.X_AXIS));
//
//        JLabel label = new JLabel(labelText);
//        inputPanel.add(label);
//
//        JTextField inputFile = new JTextField(20);
//        inputPanel.add(Box.createRigidArea(new Dimension(10, 0)));
//        inputPanel.add(inputFile);
//
//        ImageIcon folder = new ImageIcon(Objects.requireNonNull(Main.class.getResource("/images/folder.png")));
//        JButton explorerButton = new JButton(folder);
//        explorerButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                createFileExplorer(type, hashName);
//            }
//        });
//
//        inputPanel.add(Box.createRigidArea(new Dimension(10, 0)));
//        inputPanel.add(explorerButton);
//
//        textfields.put(hashName, inputFile);
//        frame.add(inputPanel);
//    }
//
//    private static void createFileExplorer(String type, String selectionTarget){
//        if (type.equals("folder")){
//            fileChooser.setDialogTitle("Select a Folder");
//            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
//        } else if (type.equals("file")) {
//            fileChooser.setDialogTitle("Select a File");
//            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//        } else {
//            fileChooser.setDialogTitle("Select a File or Folder");
//            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//        }
//
//        int returnValue = fileChooser.showOpenDialog(null);
//
//        if (returnValue == JFileChooser.APPROVE_OPTION) {
//            File selectedFile = fileChooser.getSelectedFile();
//            if (selectionTarget.equals("output") && selectedFile.isDirectory()){
//                Path combinedPath = Paths.get(selectedFile.getAbsolutePath()).resolve("output.json");
//                selectedFile = combinedPath.toFile();
//            }
//
//            textfields.get(selectionTarget).setText(selectedFile.getAbsolutePath());
//        }
//    }
//
//    private static void createSubmitButton(){
//        JButton submit = new JButton("Submit");
//        submit.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String input = textfields.get("input").getText();
//                String outputFile = textfields.get("output").getText();
//                boolean response;
//                if (OCRBox.isSelected()){
//                    response = logic.imgToJSON(input, outputFile, conversionStatus);
//                } else {
//                    response = logic.txtToJSON(input, outputFile);
//                }
//
//                if (response) {
//                    JOptionPane.showMessageDialog(frame, "Success.", "Information", JOptionPane.INFORMATION_MESSAGE);
//                } else {
//                    JOptionPane.showMessageDialog(frame, "Something went wrong.", "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        });
//
//        JPanel buttonPanel = new JPanel();
//        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
//        buttonPanel.add(submit);
//
//        frame.add(buttonPanel);
//    }
}