package com.wesll.beans;

import org.json.simple.JSONObject;
import java.math.BigInteger;

public class Auction {

    private BigInteger auc;
    private int item;
    private String owner;
    private BigInteger buyout;
    private int quantity;

    Auction(BigInteger auc, int item, String owner, BigInteger buyout, int quantity) {
        this.auc = auc;
        this.item = item;
        this.owner = owner;
        this.buyout = buyout;
        this.quantity = quantity;
    }

    public Auction(JSONObject jo) {
        this(
                new BigInteger(String.valueOf(jo.get("auc"))),
                Integer.valueOf(jo.get("item").toString()),
                String.valueOf(jo.get("owner")),
                new BigInteger(String.valueOf(jo.get("buyout"))),
                Integer.valueOf(jo.get("quantity").toString())
        );
    }

    public BigInteger getAuc() {
        return auc;
    }

    public void setAuc(BigInteger auc) {
        this.auc = auc;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public BigInteger getBuyout() {
        return buyout;
    }

    public void setBuyout(BigInteger buyout) {
        this.buyout = buyout;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
