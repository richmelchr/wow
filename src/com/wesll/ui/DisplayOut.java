package com.wesll.ui;

import com.wesll.beans.Item;
import com.wesll.util.Connector;
import com.wesll.util.ItemMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

public class DisplayOut {

    private static DisplayOut instance;
    private ItemMap itemMap = ItemMap.getInstance();
    private final BigDecimal PROC_RATE = new BigDecimal("1.5");
    private Button button;
    private Stage window;
    // TODO: observer class. if calculator object 'prices' changes, make change to output display

    private DisplayOut() { }

    //singleton
    public static DisplayOut getInstance() {
        synchronized (DisplayOut.class) {
            if (instance == null) {
                instance = new DisplayOut();
            }
        }
        return instance;
    }

    private GridPane miniBuilder(Item.Category category, GridPane gridPane) {
        ArrayList list = itemMap.getCategory(category);
        while (!list.isEmpty()) {
            Item item = (Item) list.get(list.size() - 1);
            int rowSize = gridPane.getRowCount();

            Image imageAddress = new Image("File:images/" + item.getImage(), 20, 20, false, true);
            ImageView imageView = new ImageView(imageAddress);
            BigInteger cost = new BigInteger("0");
            TextFlow materialsFlow = new TextFlow();

            for (int i = 0; i < item.getMaterials().size(); i++) {
                String material = item.getMaterials().get(i);
                String amount;
                int x = material.indexOf('x');
                amount = material.substring(x + 1);
                material = material.substring(0, x);

                try {
                    BigInteger calcCost = itemMap.getItem(Integer.valueOf(material)).getPrice().multiply(new BigInteger(amount));
                    cost = cost.add(calcCost);
                } catch (Exception e) {
                    System.out.println("Exception: Item not found in map of herbs | " + e.getMessage());
                }

                Image materialAddress = new Image("File:images/" + material + ".jpg", 20, 20, false, true);
                ImageView matView = new ImageView(materialAddress);

                materialsFlow.getChildren().addAll(matView);
            }


            Text name = new Text(item.getName());
            Text price = new Text(buildPrice(String.valueOf(item.getPrice())));
            Text costToMake = new Text(buildPrice(String.valueOf(cost)));

            Text listedCount = new Text(String.valueOf(item.getMyListedCount()));

            name.setFill(Color.web("A9B7C6"));
            price.setFill(Color.web("A9B7C6"));

            listedCount.setFill(Color.web("A9B7C6"));
            costToMake.setFill(Color.web("A9B7C6"));

            GridPane.setConstraints(imageView, 0, rowSize);
            GridPane.setConstraints(name, 2, rowSize);
            GridPane.setConstraints(price, 4, rowSize);
            GridPane.setConstraints(listedCount, 6, rowSize);
            GridPane.setConstraints(costToMake, 8, rowSize);
            GridPane.setConstraints(materialsFlow, 10, rowSize);

            gridPane.getChildren().addAll(imageView, name, price, listedCount, costToMake, materialsFlow);

            list.remove(item);
        }
        Text lineBreak = new Text();
        GridPane.setConstraints(lineBreak, 0, gridPane.getRowCount());
        gridPane.getChildren().add(lineBreak);

        return gridPane;
    }

    // TODO: this should update the display when a Observer object notices a change in the mapOfItems Map object
    private GridPane buildGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(20, 0, 20, 20));

        Text mineListedTitle = new Text("listed");
        mineListedTitle.setFill(Color.web("A9B7C6"));
        GridPane.setConstraints(mineListedTitle, 6, 0);
        gridPane.getChildren().add(mineListedTitle);

        gridPane.addColumn(1, new Text(" "));
        gridPane.addColumn(3, new Text("  "));
        gridPane.addColumn(5, new Text("  "));
        gridPane.addColumn(7, new Text("  "));
        gridPane.addColumn(9, new Text("  "));

        // TODO builder pattern?
        gridPane = miniBuilder(Item.Category.HERB, gridPane);
        gridPane = miniBuilder(Item.Category.FLASK, gridPane);
        gridPane = miniBuilder(Item.Category.BP, gridPane);
        gridPane = miniBuilder(Item.Category.COMBAT, gridPane);
        gridPane = miniBuilder(Item.Category.FOLLOWER, gridPane);

        return gridPane;
    }

    private String buildPrice(String rawPrice) {
        int length;
        String gold = "0";
        String silver = "0";
        length = rawPrice.length();

        if (length > 4) {
            gold = rawPrice.substring(0, length - 4);
            silver = rawPrice.substring(length - 4, length - 2);
        } else if (length > 2) {
            silver = rawPrice.substring(0, length - 2);
        }
        return gold + "." + silver;
    }

    private void buildDisplay() {
        GridPane gridPane = buildGridPane();
        GridPane.setConstraints(button, 2, gridPane.getRowCount());
        gridPane.getChildren().add(button);
        gridPane.setStyle("-fx-background-color: transparent;");
        Scene scene = new Scene(gridPane, 400, 660);
        scene.setFill(Color.web("2B2B2B"));
        window.setScene(scene);
        window.show();
    }

    public void begin(Stage window) {
        this.window = window;
        newButton();
        buildDisplay();
    }

    private void newButton() {
        button = new Button();
        button.setText("update");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    System.out.println("clicked");
                    Connector connector = new Connector();
                    connector.running();
                    buildDisplay();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

}


