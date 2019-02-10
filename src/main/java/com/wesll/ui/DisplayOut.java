package com.wesll.ui;

import com.wesll.beans.Item;
import com.wesll.util.Calc;
import com.wesll.util.Connector;
import com.wesll.util.ItemMap;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.BigInteger;

public class DisplayOut implements Observer {

    private static Button buttonGetREST;
    private static Button buttonRedraw;
    private static Stage window;
    private static DisplayOut instance;
    private static ProgressBar pb;
    private static GridPaneBuilder gpbuilder;

    private static double ii = 0;

    private DisplayOut() {
    }

    public static DisplayOut getInstance() {
        synchronized (DisplayOut.class) {
            if (instance == null) {
                instance = new DisplayOut();
            }
        }
        return instance;
    }

    private static void buildGridPane() {
        gpbuilder.setPadding(new Insets(20, 0, 20, 20));

        Label mineListedTitle = new Label("listed");
        GridPane.setConstraints(mineListedTitle, 3, 0);
        gpbuilder.add(mineListedTitle);

        gpbuilder.addCategory(Item.Category.HERB)
                .addCategory(Item.Category.FLASK)
                .addCategory(Item.Category.BP)
                .addCategory(Item.Category.COMBAT)
                .addCategory(Item.Category.UTILITY)
                .addCategory(Item.Category.FOLLOWER)
                .addCategory(Item.Category.FOOD)
                .addCategory(Item.Category.MEAT);
    }

    private static void buildDisplay() {
        gpbuilder = new GridPaneBuilder();
        buildGridPane();
        GridPane.setConstraints(buttonRedraw, 1, gpbuilder.getRowCount());
        GridPane.setConstraints(buttonGetREST, 1, gpbuilder.getRowCount() + 1);
        gpbuilder.add(buttonRedraw).add(buttonGetREST);
        gpbuilder.setHgap(10);

        Scene scene = new Scene(gpbuilder.build(), 420, 1110);
        scene.getStylesheets().add("style.css");

        window.setScene(scene);
        window.show();
        System.out.println("Display built\n");
    }

    public void pullPricesFromDisplay() {
        Node label, price;

//        System.out.println(ItemMap.getInstance().getItemByName("Star Moss").getPrice());

        for (int i = 0; i < gpbuilder.getRowCount(); i++) {
            label = gpbuilder.getRow(i, 1);
            price = gpbuilder.getRow(i, 2);

            if (label != null && price != null && label.getTypeSelector().equals("Label")) {
                String labelText = ((Label) label).getText();
                String rawPrice = ((TextField) price).getText();
                Item item = ItemMap.getInstance().getItemByName(labelText);

                if (!rawPrice.equals(Calc.buildIntPrice(String.valueOf(item.getPrice())))) {
                    BigInteger itemPrice;
                    System.out.println("Price of (" + labelText + ") changed to (" + rawPrice + ")");

                    if (rawPrice.contains(".")) {
                        double doublePrice = Double.parseDouble(rawPrice);
                        itemPrice = BigDecimal.valueOf(doublePrice * 10000).toBigInteger();

                    } else {
                        int temp = Integer.parseInt(rawPrice);
                        itemPrice = new BigInteger(String.valueOf(temp * 10000));
                    }

                    item.setPrice(itemPrice);

                    ItemMap.getInstance().setItem(item);
                }
            }
        }
    }

    public void begin(Stage win) {
        window = win;
        ItemMap itemMap = ItemMap.getInstance();
        itemMap.addObserver(this);
        newRedrawButton();
        newRestButton();
        update();
        new Thread(Connector.r).start();
    }

    public void startTimer() {
        while (true) {
            try {
                new Thread(Connector.r).start();
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void newRedrawButton() {
        buttonRedraw = new Button();
        buttonRedraw.setPrefWidth(60);
        buttonRedraw.setText("refresh");

        buttonRedraw.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("clicked redraw");
                buildDisplay();
            }
        });

    }

    private static void newRestButton() {
        buttonGetREST = new Button();
//        pb = new ProgressBar();
        buttonGetREST.setPrefWidth(60);
        buttonGetREST.setText("REST");

        buttonGetREST.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    System.out.println("clicked REST");
                    new Thread(Connector.r).start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void update() {
        Platform.runLater(DisplayOut::buildDisplay);
    }
}


