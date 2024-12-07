package org.example;

import java.io.File;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;

import org.example.helpers.Logic;

public class GUI extends Application {

    static Logic logic = new Logic();

    private Text programStatus;
    private TextField inField;
    private Button inButton;
    private CheckBox inDir;
    private TextField outField;
    private CheckBox useOCR;

    private final FileChooser fileChooser = new FileChooser();
    private final DirectoryChooser directoryChooser = new DirectoryChooser();

    public void start(Stage primaryStage){
        System.out.println("RUNNING!");
        primaryStage.setTitle("Converter");

        Label inLabel = new Label("Input");
        inField = new TextField();
        inField.setPrefSize(250, 30);
        inDir = new CheckBox();
        inButton = new Button("file");
        inButton.setOnAction(this::handleFolder);
        Text inText = new Text("The input can be either a folder of a file. Files should be a .txt, or .jpg/.png using OCR.\nFolder inputs will try to convert all files within the folder.");

        Label outLabel = new Label("Output");
        outField = new TextField();
        outField.setPrefSize(250, 30);
        Button outButton = new Button("file");
        outButton.setOnAction(this::handleFolder);
        Text outText = new Text("The output location can must be a file. Existing files will be overwritten.");

        useOCR = new CheckBox();
        Label OSRlabel = new Label("Use OSR");

        programStatus = new Text();
        Button submit = new Button("Submit");
        submit.setOnAction(this::handleSubmit);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
//        grid.setGridLinesVisible(true);
        grid.setAlignment(Pos.CENTER);

        grid.add(inLabel, 0, 0);
        grid.add(inField, 1, 0);

        HBox pane1 = new HBox(inButton, inDir, new Text("select folder"));
        grid.add(pane1, 2, 0);

        grid.add(inText, 0, 1, 3, 1);

        grid.add(outLabel, 0, 2);
        grid.add(outField, 1, 2);
        grid.add(outButton, 2, 2);

        grid.add(outText, 0, 3, 3, 1);

        HBox pane = new HBox(useOCR, OSRlabel);
        grid.add(pane, 0,4);

        grid.add(programStatus, 0, 5, 2, 1);
        grid.add(submit, 2, 5);

        Scene scene = new Scene (grid, 500, 270);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleFolder(ActionEvent event){
        boolean useDir = event.getSource() == inButton && inDir.isSelected();

        File chosen;
        if (useDir){
            chosen = directoryChooser.showDialog(new Stage());
        } else {
            chosen = fileChooser.showOpenDialog(new Stage());
        }

        if (event.getSource() == inButton){
            inField.setText(chosen.getAbsolutePath());
        } else {
            outField.setText(chosen.getAbsolutePath());
        }
    }

    private void handleSubmit(ActionEvent event){
        String input = inField.getText();
        String output = outField.getText();
        boolean response;
        if (useOCR.isSelected()){
            response = logic.imgToJSON(input, output, programStatus);
        } else {
            response = logic.txtToJSON(input, output);
        }

        if (response) {
            programStatus.setText("Success!");
        } else {
            programStatus.setText("Something went wrong.");
        }
    }
}