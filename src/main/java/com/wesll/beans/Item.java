package com.wesll.beans;

import java.math.BigInteger;
import java.util.ArrayList;

public class Item {

    public enum Category {
        FLASK, COMBAT, HERB, FOLLOWER, UTILITY, BP, FOOD, MEAT, NA
    }

    private int item;
    private String name;
    private BigInteger price;
    private Category category;
    private String image;
    private int myListedCount;
    private ArrayList<String> materials;
    private double procRate;

    public Item() {
        this(0, new BigInteger("0"), 0);
    }

    public Item(int item, BigInteger price, int myListedCount) {
        this(item, "", price, Category.NA, "", myListedCount, new ArrayList<>(), 0.0);
    }

    public Item(int item, String name, BigInteger price, Category category, String image, int myListedCount, ArrayList<String> materials, double procRate) {
        this.item = item;
        this.name = name;
        this.price = price;
        this.category = category;
        this.image = image;
        this.myListedCount = myListedCount;
        this.materials = materials;
        this.procRate = procRate;
    }

    public int getItem() {
        return item;
    }

    public String getName() {
        return name;
    }

    public BigInteger getPrice() {
        return price;
    }

    public Category getCategory() {
        return category;
    }

    public String getImage() {
        return image;
    }

    public int getMyListedCount() {
        return myListedCount;
    }

    public ArrayList<String> getMaterials() {
        return materials;
    }

    public double getProcRate() {
        return procRate;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(BigInteger price) {
        this.price = price;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setMyListedCount(int myListedCount) {
        this.myListedCount = myListedCount;
    }

    public void setMaterials(ArrayList<String> materials) {
        this.materials = materials;
    }

    public void setProcRate(double procRate) {
        this.procRate = procRate;
    }

    @Override
    public String toString() {
        return "Item{" +
                "item=" + item +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", category=" + category +
                ", myListedCount=" + myListedCount +
                '}';
    }

    public void iterateListed() {
        myListedCount++;
    }

    public String toJSONObjectString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"item\":");
        sb.append(item);
        sb.append(",");
        sb.append("\"name\":");
        sb.append("\"" + name + "\"");
        sb.append(",");
        sb.append("\"category\":");
        sb.append("\"" + category + "\"");
        sb.append(",");
        sb.append("\"image\":");
        sb.append("\"" + image + "\"");
        sb.append(",");
        sb.append("\"materials\":[");

        for (String material : materials) {
            sb.append("\"" + material + "\"");
            sb.append(",");
        }

        if (!materials.isEmpty()) {
            sb.setLength(sb.length() - 1);
        }

        sb.append("],");
        sb.append("\"procRate\":");
        sb.append(procRate);
        sb.append("}");
        return sb.toString();
    }
}
