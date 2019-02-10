package com.wesll.ui;

import com.wesll.beans.Item;
import com.wesll.util.Calc;
import com.wesll.util.ItemMap;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

public class GridPaneBuilder {

    private GridPane gridPane;
    private ItemMap itemMap = ItemMap.getInstance();


    GridPaneBuilder() {
        gridPane = new GridPane();
    }

    GridPane build() {
        return gridPane;
    }

    GridPaneBuilder setPadding(Insets insets) {
        gridPane.setPadding(insets);
        return this;
    }

    GridPaneBuilder add(Node node) {
        gridPane.getChildren().add(node);
        return this;
    }

    GridPaneBuilder setHgap(int hgap) {
        gridPane.setHgap(hgap);
        return this;
    }

    GridPaneBuilder addCategory(Item.Category category) {
        for (Item item : itemMap.getCategory(category)) {
            Image imageAddress = new Image("images/" + item.getImage(), 20, 20, false, true);

            TextField price = new TextField(Calc.buildIntPrice(String.valueOf(item.getPrice())));
            price.setOnAction(event -> {
                DisplayOut.getInstance().pullPricesFromDisplay();
                DisplayOut.getInstance().update();
            });

            gridPane.addRow(getRowCount(),
                    new ImageView(imageAddress),
                    new Label(item.getName()),
//                    new TextField(Calc.buildIntPrice(String.valueOf(item.getPrice()))),
                    price,
                    new Label(String.valueOf(item.getMyListedCount())),
                    Calc.getValueLabel(item),
                    Calc.getMatView(item)
            );
        }
        gridPane.addRow(getRowCount(), new Text());
        return this;
    }

    int getRowCount() {
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

    Node getRow(int row, int column) {
        Node result = null;
        ObservableList<Node> children = gridPane.getChildren();

        for(Node node : children) {
            if(gridPane.getRowIndex(node) == row && gridPane.getColumnIndex(node) == column) {
                result = node;
                break;
            }
        }
        return result;
    }

}
