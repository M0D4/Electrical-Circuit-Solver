package com.example.cad;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.Scanner;


public class Main extends Application {
    private TextField[][] matrixFields;
    private TextField heightInput, widthInput;
    private Scene homeScene;
    private Scene matrixInputScene;
    private Scene secondInputScene;
    private Alert errorAlert;
    private Stage stage;
    private int att;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        att = 20;
        initHomeScreen();

        stage.setTitle("Electrical Circuit Solver");
        stage.setScene(homeScene);
        stage.show();

        errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setHeaderText(null);
    }

    public void initHomeScreen(){
        Label incidenceMatrixSizeLabel = new Label("           Enter Incidence Matrix Size");
        Label heightLabel = new Label("Height: ");
        Label widthLabel = new Label("Width: ");

        heightInput = new TextField();
        heightInput.setPromptText("5");
        widthInput = new TextField();
        widthInput.setPromptText("3");

        HBox heightBox = new HBox();
        heightBox.getChildren().addAll(heightLabel, heightInput);
        heightBox.setSpacing(5);

        HBox widthBox = new HBox();
        widthBox.getChildren().addAll(widthLabel, widthInput);
        widthBox.setSpacing(6);

        Button enterButton = new Button("Enter");
        HBox enterButtonHBox = new HBox();
        enterButtonHBox.setAlignment(Pos.BASELINE_CENTER);
        enterButtonHBox.getChildren().addAll(enterButton);

        HBox incidenceMatrixSizeHBox = makeHBox();
        incidenceMatrixSizeHBox.getChildren().addAll(incidenceMatrixSizeLabel);

        GridPane homeSceneGrid = makeGridPane();
        GridPane.setConstraints(incidenceMatrixSizeHBox, 1, 1);
        GridPane.setConstraints(heightBox, 1, 2);
        GridPane.setConstraints(widthBox, 1, 3);
        GridPane.setConstraints(enterButtonHBox, 1, 4);

        homeSceneGrid.getChildren().addAll(incidenceMatrixSizeHBox, heightBox, widthBox, enterButtonHBox);

        homeScene = new Scene(homeSceneGrid, 303, 175);

        enterButton.setOnAction(e -> {
            int height, width;
            try{
                height = Integer.parseInt(heightInput.getText());
                if(height < 2)
                    throw new Exception();
            }catch (Exception ex){
                errorAlert.setContentText("Height must be a positive integer greater than 1!");
                errorAlert.showAndWait();
                return;
            }
            try{
                width = Integer.parseInt(widthInput.getText());
                if(width < 2)
                    throw new Exception();
            }catch (Exception ex){
                errorAlert.setContentText("Width must be a positive integer greater than 1!");
                errorAlert.showAndWait();
                return;
            }
            initMatrixInputScreen(height, width);
        });
    }

    public void initMatrixInputScreen(int height, int width){
        GridPane matrixGridPane = makeGridPane();

        Label incidenceMatrixLabel = new Label("Enter Incidence Matrix (A)");
        Button inputBackButton = new Button("Back");
        Button continueButton = new Button("Continue");

        HBox incidenceMatrixHBox = makeHBox();
        incidenceMatrixHBox.getChildren().addAll(incidenceMatrixLabel);

        HBox backAndCalculateHBox = makeHBox();
        backAndCalculateHBox.getChildren().addAll(inputBackButton, continueButton);

        inputBackButton.setOnAction(e -> {
            stage.setScene(homeScene);
        });


        continueButton.setOnAction(e -> {
            int n = matrixFields.length, m = matrixFields[0].length;
            double[][] incidenceMatrix;
            try{
                incidenceMatrix = Utility.toIncidenceMatrix(matrixFields);
            }catch (Exception ex){
                errorAlert.setContentText(ex.getLocalizedMessage());
                errorAlert.showAndWait();
                return;
            }
            initSecondInputScreen(incidenceMatrix);
        });

        VBox matrixAndButtons = makeVBox();
        matrixAndButtons.getChildren().addAll(incidenceMatrixHBox, matrixGridPane, backAndCalculateHBox);

        matrixInputScene = new Scene(matrixAndButtons, 320 + width, 240 + height);
        matrixGridPane.getChildren().clear();
        matrixFields = new TextField[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                matrixFields[i][j] = new TextField();
                GridPane.setConstraints(matrixFields[i][j], j, i);
                matrixGridPane.getChildren().addAll(matrixFields[i][j]);
            }
        }
        stage.setScene(matrixInputScene);
    }

    public void initSecondInputScreen(double[][] incidenceMatrix){
        int screenHeight = 0, screenWidth = 0;
        int n = incidenceMatrix.length, m = incidenceMatrix[0].length;
        Button secondInputBackButton = new Button("Back");
        Button calculateButton = new Button("Calculate");

        GridPane matrixAGridPane = makeGridPane();

        HBox backAndCalculateHBox = makeHBox();
        backAndCalculateHBox.getChildren().addAll(secondInputBackButton, calculateButton);

        secondInputBackButton.setOnAction(e -> {
            stage.setScene(matrixInputScene);
        });

        Label matrixALabel = new Label("Incidence Matrix (A)");
        double[][] normalizedMatrixA = Utility.getNormalizedMatrix(incidenceMatrix);
        int newN = normalizedMatrixA.length;
        screenHeight += newN * att; screenWidth += m * att;
        TextField[][] matrixA = new TextField[newN][m];
        for (int i = 0; i < newN; i++) {
            for (int j = 0; j < m; j++) {
                matrixA[i][j] = new TextField((int)normalizedMatrixA[i][j] + "");
                matrixA[i][j].setEditable(false);
                matrixA[i][j].setFocusTraversable(false);
                GridPane.setConstraints(matrixA[i][j], j, i);
                matrixAGridPane.getChildren().addAll(matrixA[i][j]);
            }
        }


        n = newN;
        Label currentSourceLabel = new Label("Enter Current Source Matrix (Ib)");
        GridPane currentSourceGridPane = makeGridPane();

        TextField[] currentSourceInput = new TextField[m];
        screenHeight += m * att;
        for (int i = 0; i < currentSourceInput.length; i++) {
            currentSourceInput[i] = new TextField();
            GridPane.setConstraints(currentSourceInput[i], 0, i);
            currentSourceGridPane.getChildren().addAll(currentSourceInput[i]);
        }

        HBox currentSourceBox = makeHBox();
        currentSourceBox.getChildren().addAll(currentSourceGridPane);

        Label voltageSourceLabel = new Label("Enter Voltage Source Matrix (Eb)");
        GridPane voltageSourceGridPane = makeGridPane();


        TextField[] voltageSourceInput = new TextField[m];
        for (int i = 0; i < voltageSourceInput.length; i++) {
            voltageSourceInput[i] = new TextField();
            GridPane.setConstraints(voltageSourceInput[i], 0, i);
            voltageSourceGridPane.getChildren().addAll(voltageSourceInput[i]);
        }

        HBox voltageSourceBox = makeHBox();
        voltageSourceBox.getChildren().addAll(voltageSourceGridPane);

        Label resistorsLabel = new Label("Enter Resistors Matrix (Zb)");
        GridPane resistorsGridPane = makeGridPane();

        TextField[][] resistorsInput = new TextField[m][m];
        screenHeight += m * att; screenWidth += m * att;
        for (int i = 0; i < resistorsInput.length; i++) {
            for (int j = 0; j < resistorsInput[0].length; j++) {
                resistorsInput[i][j] = new TextField((i == j ? "" : "0"));
                if(i != j){
                    resistorsInput[i][j].setFocusTraversable(false);
                }
                GridPane.setConstraints(resistorsInput[i][j], j, i);
                resistorsGridPane.getChildren().addAll(resistorsInput[i][j]);
            }
        }

        HBox resistorsBox = makeHBox();
        resistorsBox.getChildren().addAll(resistorsGridPane);

        VBox currentSourceVBox = makeVBox();
        currentSourceVBox.getChildren().addAll(currentSourceLabel, currentSourceBox);

        VBox voltageSourceVBox = makeVBox();
        voltageSourceVBox.getChildren().addAll(voltageSourceLabel, voltageSourceBox);

        HBox currentAndVoltageHBox = makeHBox();
        currentAndVoltageHBox.getChildren().addAll(currentSourceVBox, voltageSourceVBox);

        VBox matricesAndButtons = makeVBox();
        matricesAndButtons.getChildren().addAll(matrixALabel, matrixAGridPane,
                                            currentAndVoltageHBox,
                                            resistorsLabel, resistorsBox,
                                            backAndCalculateHBox);


        secondInputScene = new Scene(matricesAndButtons, 300 + screenWidth, 400 + screenHeight);
        stage.setScene(secondInputScene);
        
        calculateButton.setOnAction(e -> {
            double[][] currentSourceMatrix, voltageSourceMatrix;
            double[][] resistorsMatrix;
            try{
                currentSourceMatrix = Utility.toCurrentSourceMatrix(currentSourceInput);
                voltageSourceMatrix = Utility.toVoltageSourceMatrix(voltageSourceInput);
                resistorsMatrix = Utility.toResistorsMatrix(resistorsInput);
            }catch (Exception ex){
                errorAlert.setContentText(ex.getLocalizedMessage());
                errorAlert.showAndWait();
                return;
            }
            initOutputScreen(normalizedMatrixA, currentSourceMatrix, voltageSourceMatrix, resistorsMatrix);
        });
    }

    public void initOutputScreen(double[][] normalizedMatrixA, double[][] currentSourceMatrix
                                , double[][] voltageSourceMatrix, double[][] resistorsMatrix){

        int screenHeight = 0, screenWidth = 0;
        int n = normalizedMatrixA.length, m = normalizedMatrixA[0].length;
        Button backButton = new Button("Back");
        Button homeButton = new Button("Home");

        GridPane matrixBGridPane = makeGridPane();

        GridPane matrixCGridPane = makeGridPane();

        GridPane branchesCurrentGridPane = makeGridPane();

        GridPane branchesVoltageGridPane = makeGridPane();

        HBox backHBox = makeHBox();
        backHBox.getChildren().addAll(backButton);

        backButton.setOnAction(e -> {
            stage.setScene(secondInputScene);
        });

        double[][] cutSetMatrix, tieSetMatrix;

        try {
           cutSetMatrix = Utility.calculateCMatrixFromA(normalizedMatrixA);
        }catch (Exception ex){
            errorAlert.setContentText(ex.getLocalizedMessage());
            errorAlert.showAndWait();
            stage.setScene(secondInputScene);
            return;
        }

        tieSetMatrix = Utility.calculateBMatrixFromC(normalizedMatrixA, cutSetMatrix);

        n = tieSetMatrix.length; m = tieSetMatrix[0].length;
        screenHeight += n * att; screenWidth += m * att;

        Label matrixBLabel = new Label("Tie-Set Matrix (B)");
        TextField[][] matrixB = new TextField[tieSetMatrix.length][tieSetMatrix[0].length];
        for (int i = 0; i < tieSetMatrix.length; i++) {
            for (int j = 0; j < tieSetMatrix[0].length; j++) {
                matrixB[i][j] = new TextField("" + tieSetMatrix[i][j]);
                matrixB[i][j].setEditable(false);
                matrixB[i][j].setFocusTraversable(false);
                GridPane.setConstraints(matrixB[i][j], j, i);
                matrixBGridPane.getChildren().addAll(matrixB[i][j]);
            }
        }

        Label matrixCLabel = new Label("Cut-Set Matrix (C)");
        TextField[][] matrixC = new TextField[cutSetMatrix.length][cutSetMatrix[0].length];
        screenHeight += cutSetMatrix.length * att;
        screenWidth += cutSetMatrix[0].length * att;
        for (int i = 0; i < cutSetMatrix.length; i++) {
            for (int j = 0; j < cutSetMatrix[0].length; j++) {
                matrixC[i][j] = new TextField("" + cutSetMatrix[i][j]);
                matrixC[i][j].setEditable(false);
                matrixC[i][j].setFocusTraversable(false);
                GridPane.setConstraints(matrixC[i][j], j, i);
                matrixCGridPane.getChildren().addAll(matrixC[i][j]);
            }
        }

        Label branchesCurrentLabel = new Label("Current in branches");
        Label branchesVoltageLabel = new Label("Voltage in branches");
        double[][] branchesCurrent, branchesVoltage;
        try {
            branchesCurrent = Utility.calculateBranchesCurrent(tieSetMatrix, resistorsMatrix,
                                                    voltageSourceMatrix, currentSourceMatrix);

            branchesVoltage = Utility.calculateBranchesVoltage(resistorsMatrix, currentSourceMatrix,
                    branchesCurrent, voltageSourceMatrix);
        }catch (Exception ex){
            errorAlert.setContentText(ex.getLocalizedMessage());
            ex.printStackTrace();
            errorAlert.showAndWait();
            return;
        }

        screenHeight += branchesCurrent.length * att;
        TextField[] branchesCurrentTextFields = new TextField[branchesCurrent.length];
        for (int i = 0; i < branchesCurrent.length; i++) {
            branchesCurrentTextFields[i] = new TextField(branchesCurrent[i][0] + "");
            branchesCurrentTextFields[i].setEditable(false);
            branchesCurrentTextFields[i].setFocusTraversable(false);
            GridPane.setConstraints(branchesCurrentTextFields[i], 0, i);
            branchesCurrentGridPane.getChildren().addAll(branchesCurrentTextFields[i]);
        }

        TextField[] branchesVoltageTextFields = new TextField[branchesVoltage.length];
        for (int i = 0; i < branchesVoltage.length; i++) {
            branchesVoltageTextFields[i] = new TextField(branchesVoltage[i][0] + "");
            branchesVoltageTextFields[i].setEditable(false);
            branchesVoltageTextFields[i].setFocusTraversable(false);
            GridPane.setConstraints(branchesVoltageTextFields[i], 0, i);
            branchesVoltageGridPane.getChildren().addAll(branchesVoltageTextFields[i]);
        }

        VBox branchesCurrentVBox = makeVBox();
        branchesCurrentVBox.getChildren().addAll(branchesCurrentLabel, branchesCurrentGridPane);

        VBox branchesVoltageVBox = makeVBox();
        branchesVoltageVBox.getChildren().addAll(branchesVoltageLabel, branchesVoltageGridPane);

        HBox branchesCurrentAndVoltageHBox = makeHBox();
        branchesCurrentAndVoltageHBox.getChildren().addAll(branchesCurrentVBox, branchesVoltageVBox);

        HBox homeAndBackHBox = makeHBox();
        homeAndBackHBox.getChildren().addAll(homeButton, backButton);

        VBox matrixBAndCBox = makeVBox();
        matrixBAndCBox.getChildren().addAll(matrixBLabel, matrixBGridPane,
                                            matrixCLabel, matrixCGridPane,
                                            branchesCurrentAndVoltageHBox,
                                            homeAndBackHBox);

        homeButton.setOnAction(e -> {
            stage.setScene(homeScene);
        });

        Scene outputScene = new Scene(matrixBAndCBox, 300 + screenWidth, 400 + screenHeight);
        stage.setScene(outputScene);
    }

    private GridPane makeGridPane(){
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setAlignment(Pos.BASELINE_CENTER);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        return gridPane;
    }

    private HBox makeHBox(){
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.BASELINE_CENTER);
        hBox.setPadding(new Insets(10, 10, 10, 10));
        hBox.setSpacing(10);
        return hBox;
    }

    private VBox makeVBox() {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10, 10, 10, 10));
        return vBox;
    }

    public static void main(String[] args) {
        launch();
    }
}
