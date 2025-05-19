package com.ejemplo;

import com.example.prueba_png_card.ImageCornerCleaner;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("🖼️ Procesador de Imágenes en ZIP");

        Label title = new Label("Procesar Imágenes desde ZIP");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-padding: 0 0 10 0;");

        Label marginLabel = new Label("Margen en píxeles:");
        TextField marginField = new TextField("20");

        Label rgbLabel = new Label("Umbral RGB (mayor es más permisivo):");
        TextField redField = new TextField("180");
        TextField greenField = new TextField("180");
        TextField blueField = new TextField("180");

        HBox rgbFields = new HBox(10, redField, greenField, blueField);
        rgbFields.setAlignment(Pos.CENTER_LEFT);

        Button selectZipBtn = new Button("📂 Seleccionar archivo ZIP");
        selectZipBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        selectZipBtn.setPrefWidth(300);

        ProgressBar progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(300);

        Label progressText = new Label("");
        progressText.setStyle("-fx-font-size: 14px;");

        selectZipBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar archivo ZIP");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos ZIP", "*.zip"));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);

            if (selectedFile != null) {
                int margin = Integer.parseInt(marginField.getText());
                int r = Integer.parseInt(redField.getText());
                int g = Integer.parseInt(greenField.getText());
                int b = Integer.parseInt(blueField.getText());

                String inputPath = selectedFile.getAbsolutePath();
                String outputPath = inputPath.replace(".zip", "_processed.zip");

                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() {
                        try {
                            ImageCornerCleaner.processZip(
                                    inputPath,
                                    outputPath,
                                    margin, r, g, b,
                                    progress -> {
                                        updateProgress(progress, 1.0);
                                        updateMessage("Progreso: " + Math.round(progress * 100) + " %");
                                    }
                            );
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void succeeded() {
                        enableUI();
                        showAlert("✅ Procesamiento finalizado", "Archivo generado:\n" + outputPath);
                    }

                    @Override
                    protected void failed() {
                        enableUI();
                        showAlert("❌ Error", "Ocurrió un error durante el procesamiento.");
                    }

                    @Override
                    protected void cancelled() {
                        enableUI();
                        showAlert("⚠️ Cancelado", "El proceso fue cancelado.");
                    }

                    private void enableUI() {
                        selectZipBtn.setDisable(false);
                        marginField.setDisable(false);
                        redField.setDisable(false);
                        greenField.setDisable(false);
                        blueField.setDisable(false);
                    }

                    private void showAlert(String title, String message) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle(title);
                        alert.setHeaderText(title);
                        alert.setContentText(message);
                        alert.showAndWait();
                    }
                };

                selectZipBtn.setDisable(true);
                marginField.setDisable(true);
                redField.setDisable(true);
                greenField.setDisable(true);
                blueField.setDisable(true);

                progressBar.progressProperty().bind(task.progressProperty());
                progressText.textProperty().bind(task.messageProperty());

                new Thread(task).start();
            }
        });

        VBox vbox = new VBox(12,
                title,
                marginLabel, marginField,
                rgbLabel, rgbFields,
                selectZipBtn,
                progressBar,
                progressText
        );

        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setStyle("-fx-background-color: #f4f4f4;");

        Scene scene = new Scene(vbox, 420, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
