package com.wesll;

import com.wesll.ui.DisplayOut;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.stage.Stage;
import com.wesll.beans.Auction;
import com.wesll.beans.Item;

import com.wesll.util.ItemMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import com.wesll.util.Connector;

import java.math.BigInteger;
import java.util.*;

public class Main extends Application implements EventHandler<ActionEvent> {

    private Stage window;

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle("AH Tracker");
        window.setOnCloseRequest(e -> closeProgram());
        DisplayOut.begin(window);
    }

    private void closeProgram() {
        boolean saved = ItemMap.saveItemMap();

        if (saved) {
            System.out.println("File is saved");
            window.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Exception: \"Tracked items file did not save properly\"\n" +
                    "Would you like to exit?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                window.close();
            }
        }
    }

    @Override
    public void handle(ActionEvent event) {
    }

}
