package com.wesll.ui;

import com.wesll.beans.Item;
import com.wesll.util.Connector;
import com.wesll.util.ItemMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Observable;
import java.util.Observer;

public class DisplayOut implements Observer {

    private final BigDecimal POTS_PROC_RATE = new BigDecimal("1.5");
    private final BigDecimal FOOD_PROC_RATE = new BigDecimal("10");
    private static Button button;
    private static Stage window;
    private static ItemMap itemMap = ItemMap.getInstance();
    private static DisplayOut instance;

    private static GridPane gridPane;
    // TODO: observer class. if calculator object 'prices' changes, make change to output display

    private DisplayOut( ) {
    }

    public static DisplayOut getInstance() {
        synchronized (DisplayOut.class) {
            if (instance == null) {
                instance = new DisplayOut();
            }
        }
        return instance;
    }

    private static void miniBuilder(Item.Category category) {

        for (Item item : itemMap.getCategory(category)) {

            BigInteger costTotal = new BigInteger("0");
            TextFlow materialsFlow = new TextFlow();
            Image imageAddress = new Image("File:images/" + item.getImage(), 20, 20, false, true);

            for (String matPlusAmount : item.getMaterials()) {

                String material = matPlusAmount.substring(0, matPlusAmount.indexOf('x'));
                String quantity = matPlusAmount.substring(matPlusAmount.indexOf('x') + 1);

                ImageView matView = new ImageView(new Image("File:images/" + material + ".jpg", 20, 20, false, true));
                materialsFlow.getChildren().addAll(matView);

                costTotal = costTotal.add(buildCost(material, quantity, item));
            }

            gridPane.addRow(getRowCount(),
                    new ImageView(imageAddress),
                    new Text(" "),
                    new Label(item.getName()),
                    new Text(" "),
                    new Label(buildIntPrice(String.valueOf(item.getPrice()))),
                    new Text(" "),
                    new Label(String.valueOf(item.getMyListedCount())),
                    new Text(" "),
                    new Label(buildIntPrice(String.valueOf(costTotal))),
                    new Text(" "),
                    materialsFlow
            );

        }
        gridPane.addRow(getRowCount(), new Text());
    }

    private static BigInteger buildCost(String material, String quantity, Item item) {
        BigInteger calcCost = new BigInteger("0");
        try {
            Item matItem = itemMap.getItem(Integer.valueOf(material));
            calcCost = matItem.getPrice().multiply(new BigInteger(quantity));
            if (item.getCategory().equals(Item.Category.FOOD)) {
                // quantity of item I get from making a batch
                calcCost = calcCost.divide(new BigInteger("10"));
            }

        } catch (Exception e) {
            System.out.println("Exception: Item not found in map of herbs | " + e.getMessage());
        }
        return calcCost;
    }

    // TODO: this should update the display when a Observer object notices a change in the mapOfItems Map object
    private static void buildGridPane() {
        gridPane.setPadding(new Insets(20, 0, 20, 20));

        Label mineListedTitle = new Label("listed");
        GridPane.setConstraints(mineListedTitle, 6, 0);
        gridPane.getChildren().add(mineListedTitle);

        // TODO builder pattern?
        miniBuilder(Item.Category.HERB);
        miniBuilder(Item.Category.FLASK);
        miniBuilder(Item.Category.BP);
        miniBuilder(Item.Category.COMBAT);
        miniBuilder(Item.Category.FOLLOWER);
        miniBuilder(Item.Category.FOOD);
        miniBuilder(Item.Category.MEAT);
    }

    private static String buildIntPrice(String rawPrice) {
        int length = rawPrice.length();
        String gold = "0";

        if (length > 4) {
            gold = rawPrice.substring(0, length - 4);
        } else if (length > 2) {
            gold = "0." + rawPrice.substring(0, length - 2);
        }
        return gold;
    }

    private static String buildDecimalPrice(String rawPrice) {
        String gold = "$0";
        String silver = "$0";
        int length = rawPrice.length();

        if (length > 4) {
            gold = rawPrice.substring(0, length - 4);
            silver = rawPrice.substring(length - 4, length - 2);
        } else if (length > 2) {
            silver = rawPrice.substring(0, length - 2);
        }
        return gold + "." + silver;
    }

    private static void buildDisplay() {
        gridPane = new GridPane();
        buildGridPane();
        GridPane.setConstraints(button, 2, getRowCount());
        gridPane.getChildren().add(button);
        Scene scene = new Scene(gridPane, 400, 1010);
        scene.getStylesheets().add("style.css");
        window.setScene(scene);
        window.show();
    }

    public void begin(Stage win) {
        window = win;
        itemMap.addObserver(this);
        newButton();
//        new Thread(Connector.r).start();
        buildDisplay();
    }

    private static void newButton() {
        button = new Button();
        button.setText("update");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    System.out.println("clicked");
//                    new Thread(Connector.r).start();
                    Connector.running();
//                    buildDisplay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static int getRowCount() {
        int numRows = gridPane.getRowConstraints().size();
        for (int i = 0; i < gridPane.getChildren().size(); i++) {
            Node child = gridPane.getChildren().get(i);
            if (child.isManaged()) {
                Integer rowIndex = GridPane.getRowIndex(child);
                if (rowIndex != null) {
                    numRows = Math.max(numRows, rowIndex + 1);
                }
            }
        }
        return numRows;
    }


    @Override
    public void update(Observable o, Object arg) {
        buildDisplay();
    }
}


