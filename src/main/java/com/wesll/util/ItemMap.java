package com.wesll.util;

import com.wesll.beans.Item;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class ItemMap extends Observable {

    private Map<Integer, Item> mapOfItems = new HashMap<>();
    private static String FILE_NAME = "D:\\Code\\wow\\items.json";
    private static ItemMap instance;

    private ItemMap() {
        buildMapOfItems();
    }

    public static ItemMap getInstance() {
        synchronized (ItemMap.class) {
            if (instance == null) {
                instance = new ItemMap();
            }
        }
        return instance;
    }

    private void buildMapOfItems() {
        JSONParser parser = new JSONParser();
        try {
            JSONArray itemArray = (JSONArray) parser.parse(new FileReader(FILE_NAME));

            for (Object obj : itemArray) {
                JSONObject jo = (JSONObject) obj;

                int itemNumber = Integer.valueOf(String.valueOf(jo.get("item")));
                String name = String.valueOf(jo.get("name"));
                Item.Category category = Item.Category.valueOf(String.valueOf(jo.get("category")));
                String image = String.valueOf(jo.get("image"));
                JSONArray jsonArray = (JSONArray) jo.get("materials");
                ArrayList<String> meh = new ArrayList<>();

                for (Object o : jsonArray) {
                    meh.add(String.valueOf(o));
                }
                double procRate = Double.valueOf(String.valueOf(jo.get("procRate")));
                mapOfItems.put(itemNumber, new Item(itemNumber, name, new BigInteger("0"), category, image, 0, meh, procRate));
            }

        } catch (IOException | org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
        setChanged();
        notifyObservers();
    }

    public void setMapOfItems(Map<Integer, Item> mapOfItems) {
        this.mapOfItems = mapOfItems;
        setChanged();
        notifyObservers();
    }

    public Item getItem(int item) {
        return mapOfItems.get(item);
    }

    public Map<Integer, Item> getMapOfItems() {
        return mapOfItems;
    }

    public boolean saveMapOfItems() {
        Set<Integer> keys = mapOfItems.keySet();
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (Integer key : keys) {
            sb.append(mapOfItems.get(key).toJSONObjectString());
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");

        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(FILE_NAME));
            writer.write(sb.toString());
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void zeroMyListedCount() {
        Set<Integer> keys = mapOfItems.keySet();
        for (Integer key : keys) {
            mapOfItems.get(key).setMyListedCount(0);
        }
    }

    public ArrayList<Item> getCategory(Item.Category type) {
        ArrayList<Item> items = new ArrayList<>();
        Set<Integer> keys = mapOfItems.keySet();

        for (Integer key : keys) {
            Item item = mapOfItems.get(key);
            if (item.getCategory().equals(type)) {
                items.add(item);
            }
        }

        return items;
    }
}
