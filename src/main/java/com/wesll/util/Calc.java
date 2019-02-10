package com.wesll.util;

import com.wesll.beans.Item;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextFlow;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Calc {

    private static final BigDecimal POTS_PROC_RATE = new BigDecimal("1.5");
    private static final BigDecimal FOOD_PROC_RATE = new BigDecimal("10");
    private static final BigDecimal AH_TAX = new BigDecimal("0.05");

    private Calc() {}

    public static String buildIntPrice(String rawPrice) {
        int length = rawPrice.length();
        String gold = "0";

        if (length > 4) {
            gold = rawPrice.substring(0, length - 4);
        } else if (length > 2) {
            gold = "0." + rawPrice.substring(0, length - 2);
        }
        return gold;
    }

    public static BigInteger calcFinalProfitMargin(BigInteger cost) {
        BigDecimal temp = new BigDecimal(cost);
        temp = temp.subtract(temp.multiply(AH_TAX));
        temp = temp.multiply(POTS_PROC_RATE);
        return temp.toBigInteger();
    }

    public static BigInteger buildCost(String material, String quantity, Item item) {
        BigInteger calcCost = new BigInteger("0");
        try {
            Item matItem = ItemMap.getInstance().getItem(Integer.valueOf(material));
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

    public static TextFlow getMatView(Item item) {
        TextFlow materialsFlow = new TextFlow();

        for (String matPlusAmount : item.getMaterials()) {
            String material = matPlusAmount.substring(0, matPlusAmount.indexOf('x'));
            ImageView matView = new ImageView(new Image("images/" + material + ".jpg", 20, 20, false, true));
            materialsFlow.getChildren().addAll(matView);
        }
        return materialsFlow;
    }

    public static Label getValueLabel(Item item) {
        BigInteger costTotal = new BigInteger("0");
        for (String matPlusAmount : item.getMaterials()) {
            String material = matPlusAmount.substring(0, matPlusAmount.indexOf('x'));
            String quantity = matPlusAmount.substring(matPlusAmount.indexOf('x') + 1);
            costTotal = costTotal.add(Calc.buildCost(material, quantity, item));
        }
        Label valueResult = new Label("");

        if (!costTotal.equals(new BigInteger("0"))) {

            BigInteger value = new BigInteger("0");

            if (item.getCategory().equals(Item.Category.BP) || item.getCategory().equals(Item.Category.FLASK) || item.getCategory().equals(Item.Category.COMBAT)) {
                value = Calc.calcFinalProfitMargin(item.getPrice()).subtract(costTotal);
            } else {
                value = item.getPrice().subtract(costTotal);
            }

            valueResult = new Label(Calc.buildIntPrice(String.valueOf(value)));

            if (value.compareTo(new BigInteger("0")) > 0) {
                valueResult.getStyleClass().add("highlight");
            }
        }
        return valueResult;
    }


}
