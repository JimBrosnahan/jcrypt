/*
** Copyright (c) 2020 - Jim Brosnahan
**
** This demonstration source is distributed in the hope it will be useful,
** BUT WITHOUT ANY WARRANTY.
**
** 
*/

package jcrypt;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class Jcrypt extends Application {
    
   
    public static void main(String[] args) throws Exception {
        

        String inputfile = null;
        String outputfile = null;
        char[] pw;
        
        // validate input args for determining commandline vs GUI dialog        
        if (args.length > 2) {

            inputfile = args[1];
            outputfile = args[2];

            // validate cipher mode
            if (args[0].toUpperCase().equals("E")) {
                
                pw = AES.readPassPhrase(2);
                AES.encrypt(pw, inputfile, outputfile);

            } else if (args[0].toUpperCase().equals("D")) {
                
                pw = AES.readPassPhrase(1);
                AES.decrypt(pw, inputfile, outputfile);
                        
            } else {
                System.out.println("invalid cipher mode");
                System.exit(1);
            }

        } else {

            // launch GUI dialog
            launch(args);
            System.exit(0);

            // System.out.println("e|d <input> <output>");
            // System.exit(0);
        }
        
        System.exit(0);
    }
    
    /*
     * GUI mode methods
     */   
    private static String getFileName(Stage s, Text prompt) {
        
        final FileChooser fileChooser = new FileChooser();
        
        File file = fileChooser.showOpenDialog(s);
        
        if (file != null) {
            prompt.setText("");
            return file.getAbsolutePath();
        } 

        prompt.setText("no path/file specified");
        //System.out.println("no path/file specified");
        return null;
    }

    
    @Override
    public void start(Stage primaryStage) {
                
        primaryStage.setTitle("Jcrypt");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER_LEFT);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Encrypt / Decrypt");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        TextField inputFileField = new TextField();
        grid.add(new Label("Input File:"), 0, 1);
        grid.add(inputFileField, 1, 1);
        final Button ibtn = new Button("File");
        grid.add(ibtn, 2, 1);
                
        TextField outputFileField = new TextField();
        grid.add(new Label("Output File:"), 0, 2);
        grid.add(outputFileField, 1, 2);
        final Button obtn = new Button("File");
        grid.add(obtn, 2, 2);       
        
        final ComboBox<String> opmodeComboBox = new ComboBox<>();
        opmodeComboBox.getItems().addAll("Encrypt","Decrypt");   

        //grid.setAlignment(Pos.CENTER_RIGHT);
        opmodeComboBox.getSelectionModel().clearAndSelect(0);
        grid.add(new Label("Mode:"), 0, 3);
        grid.add(opmodeComboBox, 1, 3);
        
        
        final Button btn = new Button("Run");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 2, 3);
        
        // status bar text
        Text statusbar = new Text();
        grid.add(statusbar, 1, 4);
        statusbar.setFill(Color.FIREBRICK);
        
        /*
         * input file handler
         */
        ibtn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent e) {
                
                String name = getFileName(primaryStage, statusbar);
                if (name != null) {
                    inputFileField.setText(name);
                }
            }
        });

        /*
         * output file handler
         */
        obtn.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent e) {

                String name = getFileName(primaryStage, statusbar);
                if (name != null) {
                    outputFileField.setText(name);
                }                
            }            
        });
        
        /*
         * run cipher handler
         */
        btn.setOnAction(new EventHandler<ActionEvent>() {

            char[] password0;
            char[] password1;

            @Override
            public void handle(ActionEvent e) {

                // must have input/output specified
                if ((inputFileField.getLength() > 0) && (outputFileField.getLength() > 0)) {

                    int opMode = opmodeComboBox.getSelectionModel().getSelectedIndex();
                    String inputfile = inputFileField.getText();
                    String outputfile = outputFileField.getText();

                    statusbar.setText("Enter Password");

                    password0 = popupDialog("Enter Password");

                    // opMode = 0 = encrypt, request password 2nd time and verify match
                    if (opMode == 0) {

                        password1 = popupDialog("Enter Password again");
                        if (!Arrays.equals(password0, password1)) {
                            statusbar.setText("Both Passwords must match");
                            return;
                        }
                    }

                    if (password0.length == 0) {
                        statusbar.setText("Missing password");
                        return;
                    }

                    if (password0.length > 0) {
                        try {
                            // 0 = encrypt/1 = decrypt
                            if (opMode == 1) {
                                AES.decrypt(password0, inputfile, outputfile);
                            } else {
                                AES.encrypt(password0, inputfile, outputfile);
                            }

                            statusbar.setText("cipher complete");

                        } catch (IOException exc) {
                            exc.printStackTrace();
                        } catch (Exception exc) {
                            exc.printStackTrace();
                        }
                    }
                } else {
                    statusbar.setText("missing input/output files");
                }
            }
        }); // Run EventHandler

        // Main app scene
        Scene scene = new Scene(grid, 325, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    // Password Dialog
    public char[] popupDialog(String prompt) {

        final Stage myDialog = new Stage();

        Text scenetitle = new Text(prompt);
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 12));
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.BASELINE_CENTER);
        grid.add(scenetitle, 0, 0);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 0, 1);

        /* [ENTER Key] handler */
        pwBox.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                myDialog.close();
                // System.out.println("ENTER");
            }
        });

        Scene myDialogScene = new Scene(grid, 200, 75);

        myDialog.initModality(Modality.APPLICATION_MODAL);
        myDialog.setScene(myDialogScene);
        myDialog.showAndWait();

        return pwBox.getText().toCharArray();
    }
}
