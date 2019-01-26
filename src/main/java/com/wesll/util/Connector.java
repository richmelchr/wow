package com.wesll.util;

import com.wesll.beans.Auction;
import com.wesll.beans.Item;
import javafx.application.Platform;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Map;

public class Connector {

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String USER = "Cpe";

    private Connector() {
    }

    private static JSONArray sendGet() throws Exception {
        String url = "http://auction-api-us.worldofwarcraft.com/auction-data/a6b6ca548431e1202e4248ccf28fd4ad/auctions.json";
        JSONObject jsonObject;
        JSONArray jsonArray;
        JSONParser parser = new JSONParser();

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        System.out.println("REST Service contacted");
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        System.out.println("REST Response Parsed");
        in.close();

        jsonObject = (JSONObject) parser.parse(response.toString());
        jsonArray = (JSONArray) jsonObject.get("auctions");

        return jsonArray;
    }

    public static Runnable r = new Runnable() {
        public void run() {
            try {
                running();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private static void running() throws Exception {
        // call API
        JSONArray auctions = Connector.sendGet();
        ArrayList list = arrayToList(auctions);
        parseTracked(list);
    }

    private static void parseTracked(ArrayList list) {
        BigInteger zero = new BigInteger("0");
        ItemMap itemMap = ItemMap.getInstance();
        itemMap.zeroMyListedCount();
        Map<Integer, Item> mapOfItems = itemMap.getMapOfItems();

        for (Object obj : list) {
            Auction auction = (Auction) obj;
            int key = auction.getItem();
            if (mapOfItems.containsKey(key)) {
                BigInteger newPrice = (auction.getBuyout().divide(BigInteger.valueOf(auction.getQuantity())));
                BigInteger lowestPrice = mapOfItems.get(key).getPrice();

                if (auction.getOwner().equals(USER)) {
                    mapOfItems.get(key).iterateListed();
                }

                // if lowest price is 0, or new price is less than lowest price
                if (lowestPrice.compareTo(zero) == 0 || lowestPrice.compareTo(newPrice) == 1) {

                    // check for auctions without a buyout value (bid only)
                    if (!String.valueOf(newPrice).equals("0")) {
                        mapOfItems.get(key).setPrice(newPrice);
                    }
                }
            }
        }
        itemMap.setMapOfItems(mapOfItems);
    }

    private static ArrayList arrayToList(JSONArray array) {
        JSONObject jsonObject;
        ArrayList list = new ArrayList();
        for (int i = 0; i < array.size(); i++) {
            jsonObject = (JSONObject) array.get(i);
            list.add(new Auction(jsonObject));
        }
        return list;
    }

}
