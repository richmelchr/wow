package com.wesll.ui;

import com.wesll.beans.Item;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.math.BigInteger;

public class DisplayOut implements Observer {

    private static final BigDecimal POTS_PROC_RATE = new BigDecimal("1.5");
    private static final BigDecimal FOOD_PROC_RATE = new BigDecimal("10");
    private static final BigDecimal AH_TAX = new BigDecimal("0.05");
    private static Button button;
    private static Stage window;
    private static ItemMap itemMap;
    private static DisplayOut instance;
    private static GridPane gridPane;
    private static ProgressBar pb;

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

    private static void miniBuilder(Item.Category category) {
        for (Item item : itemMap.getCategory(category)) {
            Image imageAddress = new Image("images/" + item.getImage(), 20, 20, false, true);
            gridPane.addRow(getRowCount(),
                    new ImageView(imageAddress),
                    new Label(item.getName()),
                    new Label(buildIntPrice(String.valueOf(item.getPrice()))),
                    new Label(String.valueOf(item.getMyListedCount())),
                    getValueLabel(item),
                    getMatView(item)
            );
        }
        gridPane.addRow(getRowCount(), new Text());
    }

    private static BigInteger calcFinalProfitMargin(BigInteger cost) {
        BigDecimal temp = new BigDecimal(cost);
        temp = temp.subtract(temp.multiply(AH_TAX));
        temp = temp.multiply(POTS_PROC_RATE);
        return temp.toBigInteger();
    }

    private static Label getValueLabel(Item item) {
        BigInteger costTotal = new BigInteger("0");
        for (String matPlusAmount : item.getMaterials()) {
            String material = matPlusAmount.substring(0, matPlusAmount.indexOf('x'));
            String quantity = matPlusAmount.substring(matPlusAmount.indexOf('x') + 1);
            costTotal = costTotal.add(buildCost(material, quantity, item));
        }
        Label valueResult = new Label("");

        if (!costTotal.equals(new BigInteger("0"))) {

            BigInteger value = new BigInteger("0");

            if (item.getCategory().equals(Item.Category.BP) || item.getCategory().equals(Item.Category.FLASK) || item.getCategory().equals(Item.Category.COMBAT)) {
                value = calcFinalProfitMargin(item.getPrice()).subtract(costTotal);
            } else {
                value = item.getPrice().subtract(costTotal);
            }

            valueResult = new Label(buildIntPrice(String.valueOf(value)));

            if (value.compareTo(new BigInteger("0")) > 0) {
                valueResult.getStyleClass().add("highlight");
            }
        }
        return valueResult;
    }

    private static TextFlow getMatView(Item item) {
        TextFlow materialsFlow = new TextFlow();

        for (String matPlusAmount : item.getMaterials()) {
            String material = matPlusAmount.substring(0, matPlusAmount.indexOf('x'));
            ImageView matView = new ImageView(new Image("images/" + material + ".jpg", 20, 20, false, true));
            materialsFlow.getChildren().addAll(matView);
        }
        return materialsFlow;
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
        GridPane.setConstraints(mineListedTitle, 3, 0);
        gridPane.getChildren().add(mineListedTitle);

        // TODO builder pattern?
        miniBuilder(Item.Category.HERB);
        miniBuilder(Item.Category.FLASK);
        miniBuilder(Item.Category.BP);
        miniBuilder(Item.Category.COMBAT);
        miniBuilder(Item.Category.UTILITY);
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
        GridPane.setConstraints(button, 1, getRowCount());


//        GridPane.setConstraints(pb, 3, getRowCount());
        gridPane.getChildren().add(button);

//        gridPane.setGridLinesVisible(true);
//        gridPane.getStyleClass().add("gridLinesStyle");
        gridPane.setHgap(10);


        Scene scene = new Scene(gridPane, 400, 1100);
        scene.getStylesheets().add("style.css");

        window.setScene(scene);
        window.show();
        System.out.println("Display built");
    }

    public void begin(Stage win) {
        window = win;
        itemMap = ItemMap.getInstance();
        itemMap.addObserver(this);
        newButton();
        update();
        new Thread(Connector.r).start();
    }

//    public static EventHandler<ActionEvent> progressBar() {
//
//    }


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


    private static void newButton() {
        button = new Button();
//        pb = new ProgressBar();
        button.setPrefWidth(60);
        button.setText("update");

//        EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                ii += 0.1;
//                pb.setProgress(ii);
//            }
//        };
//        button.setOnAction(event);


        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    System.out.println("clicked");
                    new Thread(Connector.r).start();

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

    public void update() {
        Platform.runLater(DisplayOut::buildDisplay);
    }
}


