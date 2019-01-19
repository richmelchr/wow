package com.wesll.util;

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

@RunWith(PowerMockRunner.class)
@PrepareForTest(com.wesll.util.Connector.class)
public class ConnectorTests {

    private static String FILE_NAME = "D:\\Code\\wow\\testAuctions.json";
    JSONArray jsonArray;

    public ConnectorTests() {}

    @Before
    public void before() {
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
        System.out.println();

    }

    @Test
    public void testParseTracked() {

    }

    @Test
    public void testArrayToList() {

    }



}
