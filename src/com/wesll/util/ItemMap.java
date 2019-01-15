package com.wesll.util;

import com.wesll.beans.Item;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONObject;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class ItemMap {

    private static ItemMap instance;
    private Map<Integer, Item> itemMap = new HashMap<>();
    private String FILE_NAME = "D:\\Code\\wow\\items.json";

    private ItemMap() {
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
                itemMap.put(itemNumber, new Item(itemNumber, name, new BigInteger("0"), category, image, 0, meh));
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    //singleton
    public static ItemMap getInstance() {
        synchronized (ItemMap.class) {
            if (instance == null) {
                instance = new ItemMap();
            }
        }
        return instance;
    }

    public Item getItem(int item) {
        return itemMap.get(item);
    }

    public Map<Integer, Item> getItemMap() {
        return itemMap;
    }

    public void setItemMap(Map<Integer, Item> itemMap) {
        this.itemMap = itemMap;
    }

    public boolean saveItemMap() {
        Set<Integer> keys = itemMap.keySet();
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        for (Integer key : keys) {
            sb.append(itemMap.get(key).toJSONObjectString());
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
        Set keys = itemMap.keySet();
        for (Integer key : (Iterable<Integer>) keys) {
            itemMap.get(key).setMyListedCount(0);
        }
    }

    public ArrayList<Item> getCategory(Item.Category type) {
        ArrayList<Item> items = new ArrayList<>();
        Set keys = itemMap.keySet();

        for (Integer key : (Iterable<Integer>) keys) {
            Item item = itemMap.get(key);
            if (item.getCategory().equals(type)) {
                items.add(item);
            }
        }

        return items;
    }
}
