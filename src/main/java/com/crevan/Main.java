package com.crevan;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

public class Main extends Application {
    private Stage stage;
    private Map<String, Long> sizes;

    private final ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
    private PieChart pieChart;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Disk-Analyzer");
        Button chooseButton = new Button("Choose directory");
        chooseButton.setOnAction(event -> {
            File file = new DirectoryChooser().showDialog(stage);
            String absolutePath = file.getAbsolutePath();
            sizes = new Analyzer().calculateDirectorySize(Path.of(absolutePath));
            buildChart(absolutePath);
        });

        StackPane stackPane = new StackPane();
        stackPane.getChildren().add(chooseButton);
        stage.setScene(new Scene(stackPane, 300, 250));
        stage.show();
    }

    private void buildChart(String absolutePath) {
        pieChart = new PieChart(pieChartData);

        reFillChart(absolutePath);

        Button backButton = new Button(absolutePath);
        backButton.setOnAction(event -> reFillChart(absolutePath));

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(backButton);
        borderPane.setCenter(pieChart);

        stage.setScene(new Scene(borderPane, 1280, 1000));
        stage.show();
    }

    private void reFillChart(String absolutePath) {
        pieChartData.clear();
        pieChartData.addAll(sizes.entrySet()
                .parallelStream()
                .filter(entry -> {
                    Path parent = Path.of(entry.getKey()).getParent();
                    return parent != null && parent.toString().equals(absolutePath);
                })
                .map(entry -> new PieChart.Data(entry.getKey(), entry.getValue()))
                .toList()
        );
        pieChart.getData().forEach(data -> data.getNode()
                .addEventHandler(MouseEvent.MOUSE_PRESSED,
                        event -> reFillChart(data.getName())));
    }
}
