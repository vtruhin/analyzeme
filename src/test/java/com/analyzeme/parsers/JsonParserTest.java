package com.analyzeme.parsers;

import com.analyzeme.analyze.Point;
import com.analyzeme.parsers.InvalidFileException;
import com.analyzeme.parsers.JsonParser;
import com.analyzeme.parsers.JsonParserException;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by Sergey
 */
public class JsonParserTest {
    private JsonParser jsonParser;
    private Point[] points;

    @Test(expected = NullPointerException.class)
    public void testNullArgumentInConstructor() throws Exception {
        jsonParser = new JsonParser();
        jsonParser.parse(null);
    }

    @Test
    public void testIncorrectFile() throws InvalidFileException {
        String s = join("\n", new String[]{
                "{",
                "\"x\":"
        });

        InputStream is = new ByteArrayInputStream(s.getBytes());
        jsonParser = new JsonParser();
        try {
            points = jsonParser.parse(is).toPointArray();
            Assert.fail();
        } catch (JsonParserException ex) {
            Assert.assertEquals(JsonParserException.ExceptionType.PARSE_FILE,
                    ex.getExType());
        }
    }




    @Test
    public void testPointsDoubleWithInteger() throws InvalidFileException {
        String s = join("\n", new String[]{
                "{\"Data\":[{ \"x\": \"1\",\"y\": \"1\" },{\"x\": \"20\",\"y\": \"20\"}]}"
        });

        InputStream is = new ByteArrayInputStream(s.getBytes());
        jsonParser = new JsonParser();
        points = jsonParser.parse(is).toPointArray();
        Assert.assertArrayEquals(new Point[]{new Point(1.0, 1.0),
                new Point(20.0, 20.0)},
                points);
    }

    static public String join(String delimiter, String[] list) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : list) {
            if (first)
                first = false;
            else
                sb.append(delimiter);
            sb.append(item);
        }
        return sb.toString();
    }
}
