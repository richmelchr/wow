package com.wesll.util;

import com.wesll.beans.Item;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(com.wesll.util.Connector.class)
public class ConnectorTests {

    private static String FILE_NAME = "D:\\Code\\wow\\testAuctions.json";
    JSONArray jsonArray;

    private Map<Integer, Item> testItemMap = new HashMap<>();
    private ArrayList<Item> auctionList = new ArrayList<>();

    public ConnectorTests() {}

    @Before
    public void before() {
        buildAuctionList();
        buildItemMap();

        JSONParser parser = new JSONParser();
        try {
            jsonArray = (JSONArray) parser.parse(new FileReader(FILE_NAME));

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testRunning() throws Exception {
        PowerMockito.spy(Connector.class);
        PowerMockito.doReturn(jsonArray).when(Connector.class, "sendGet");

        Connector.running();

        Map<Integer, Item> builtItemMap = ItemMap.getItemMap();

        Set<Integer> keys = testItemMap.keySet();
        for (Integer key : keys) {
            assertEquals("failed key check", testItemMap.get(key).getItem(), builtItemMap.get(key).getItem());
            assertEquals("failed price check", testItemMap.get(key).getPrice(), builtItemMap.get(key).getPrice());
            assertEquals("failed myListedCount check", testItemMap.get(key).getMyListedCount(), builtItemMap.get(key).getMyListedCount());
        }

    }

    @Test
    public void testParseTracked() {

    }

    @Test
    public void testArrayToList() {

    }

    public void buildAuctionList() {
        auctionList.add(new Item(162113, new BigInteger("20000000"), 1)) ; // lowest price "Potion of herb tracking"
        auctionList.add(new Item(152561, new BigInteger("44440000"), 2)); // "Replenishment"
        auctionList.add(new Item(152506, new BigInteger("100000"), 0)); // "Star Moss"
        auctionList.add(new Item(152638, new BigInteger("5000000"), 1)); // "Currents"
    }

    public void buildItemMap() {
        for (Item item : auctionList) {
            testItemMap.put(item.getItem(), item);
        }
    }



}
