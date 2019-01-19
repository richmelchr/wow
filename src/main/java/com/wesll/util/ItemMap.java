package com.wesll.util;

import com.wesll.beans.Item;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

public class ItemMap {

    private static Map<Integer, Item> itemMap = new HashMap<>();
    private static String FILE_NAME = "D:\\Code\\wow\\items.json";

    private ItemMap() {
    }

    public static void buildItemMap() {
        if (itemMap.isEmpty()) {
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

            } catch (IOException | org.json.simple.parser.ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public static Item getItem(int item) {
        buildItemMap();
        return itemMap.get(item);
    }

    public static Map<Integer, Item> getItemMap() {
        buildItemMap();
        return itemMap;
    }

    public static boolean saveItemMap() {
        buildItemMap();
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

    public static void zeroMyListedCount() {
        buildItemMap();
        Set<Integer> keys = itemMap.keySet();
        for (Integer key : keys) {
            itemMap.get(key).setMyListedCount(0);
        }
    }

    public static ArrayList<Item> getCategory(Item.Category type) {
        buildItemMap();
        ArrayList<Item> items = new ArrayList<>();
        Set<Integer> keys = itemMap.keySet();

        for (Integer key : keys) {
            Item item = itemMap.get(key);
            if (item.getCategory().equals(type)) {
                items.add(item);
            }
        }

        return items;
    }
}
