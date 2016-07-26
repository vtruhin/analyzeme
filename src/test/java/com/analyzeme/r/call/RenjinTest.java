package com.analyzeme.r.call;

import com.analyzeme.analyzers.result.VectorResult;
import com.analyzeme.analyzers.result.VectorsResult;
import com.analyzeme.analyzers.result.ScalarResult;
import com.analyzeme.data.dataset.DataSet;
import com.analyzeme.data.dataset.DataEntry;
import com.analyzeme.data.dataset.DataEntryType;
import com.analyzeme.data.dataset.DataArray;
import com.analyzeme.data.resolvers.sourceinfo.DataRepositoryInfo;
import com.analyzeme.data.resolvers.sourceinfo.ISourceInfo;
import com.analyzeme.parsers.JsonParser;
import com.analyzeme.repository.filerepository.FileRepository;
import com.analyzeme.repository.filerepository.TypeOfFile;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

public class RenjinTest {
    private static final double EPS = 0.00001;
    private static IRCaller call;
    private static DataArray dataArray;

    private static final String TEST_DATA =
            "{\"Data\":[{ \"x\": \"0\",\"y\": \"0\" }, " +
                    "{ \"x\": \"1\",\"y\": \"1\" }, {\"x\": \"2\",\"y\": \"2\"}," +
                    "{ \"x\": \"3\",\"y\": \"3\" }, { \"x\": \"4\",\"y\": \"4\" }," +
                    "{ \"x\": \"5\",\"y\": \"5\" }, { \"x\": \"6\",\"y\": \"6\" }," +
                    "{ \"x\": \"7\",\"y\": \"7\" }, { \"x\": \"8\",\"y\": \"8\" }," +
                    "{ \"x\": \"9\",\"y\": \"9\" }, { \"x\": \"10\",\"y\": \"10\" }]}";
    private static final String WRONG_TEST_DATA =
            "{\"Data\":[{ \"x\": \"0\",\"y\": \"0\" ," +
                    "{ \"x\": \"1\",\"y\": \"1\" }, {\"x\": \"2\",\"y\": \"2\"}," +
                    "{ \"x\": \"3\",\"y\": \"3\" }, { \"x\": \"4\",\"y\": \"4\" }," +
                    "{ \"x\": \"5\",\"y\": \"5\" }, { \"x\": \"6\",\"y\": \"6\" }," +
                    "{ \"x\": \"7\",\"y\": \"7\" },{ \"x\" \"8\",\"y\": \"8\" }," +
                    "{ \"x\": \"9\",\"\": \"9\" }, { \"x\": \"10\",\"y\": \"10\" }]}";
    private static ByteArrayInputStream correctFile;
    private static final String CORRECT_FILENAME =
            "fileRenjin.json";
    private static String correctFileId;
    private static String correctX;
    private static String correctY;
    private static ArrayList<DataSet> correct;

    private static ByteArrayInputStream incorrectFile;
    private static final String INCORRECT_FILENAME =
            "incorrectFileRenjin.json";
    private static String incorrectFileId;
    private static String incorrectX;
    private static String incorrectY;
    private static ArrayList<DataSet> incorrect;

    private static String correctScriptForCorrectFileString;

    private static String incorrectScriptForCorrectFileString;

    private static String correctScriptForIncorrectFileString;


    public static boolean doubleEqual(double a, double b) {

        return Math.abs(a - b) < EPS;
    }

    private static ByteArrayInputStream convertStringToStream(String data) {
        byte[] b = new byte[data.length()];
        for (int i = 0; i < data.length(); i++) {
            b[i] = (byte) data.charAt(i);
        }
        return new ByteArrayInputStream(b);
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        InputStream is = new ByteArrayInputStream(
                TEST_DATA.getBytes());
        JsonParser jsonParser;
        jsonParser = new JsonParser();
        dataArray = jsonParser.parse(is);
        call = new Renjin();

        correctFile = convertStringToStream(TEST_DATA);
        correctFileId =
                FileRepository.getRepo().persist(
                        correctFile,
                        CORRECT_FILENAME);
        if (correctFileId == null) {
            throw new IllegalArgumentException(
                    "Repository doesn't work");
        }
        correctX = "x_from__repo__" + correctFileId + "__";
        correctY = "y_from__repo__" + correctFileId + "__";
        correct = new ArrayList<DataSet>();
        ISourceInfo correctInfo =
                new DataRepositoryInfo(correctFileId,
                        TypeOfFile.SIMPLE_JSON);
        DataSet setCorrect =
                new DataSet(CORRECT_FILENAME, correctInfo);
        setCorrect.addField("x");
        setCorrect.addField("y");
        correct.add(setCorrect);

        incorrectFile = convertStringToStream(WRONG_TEST_DATA);
        incorrectFileId =
                FileRepository.getRepo().persist(
                        incorrectFile, INCORRECT_FILENAME);
        incorrectX = "x_from__repo__" + incorrectFileId + "__";
        incorrectY = "y_from__repo__" + incorrectFileId + "__";
        incorrect = new ArrayList<DataSet>();
        ISourceInfo incorrectInfo =
                new DataRepositoryInfo(incorrectFileId,
                        TypeOfFile.SIMPLE_JSON);
        DataSet setIncorrect = new DataSet(
                INCORRECT_FILENAME,
                incorrectInfo);
        setIncorrect.addField("x");
        setIncorrect.addField("y");
        incorrect.add(setIncorrect);
        correctScriptForCorrectFileString =
                "x<-c(" + correctX + "[1], " + correctX + "[2], "
                        + correctX + "[3]); y<-c(" + correctY + "[1], "
                        + correctY + "[2], " + correctY
                        + "[3]); z<-data.frame(x, y); " +
                        "names(z) <- c(\"new X name\", \"new Y name\"); z";
        incorrectScriptForCorrectFileString =
                "x<-c(" + correctX + "[1], " + correctX
                        + "[2], " + correctX + "[3]); y<c("
                        + correctY + "[1], " + correctY + "[2], "
                        + correctY + "[3]); z<-data.frame(x, y); " +
                        "names(z <- c(\"new X name\", \"new Y name\"); z";
        correctScriptForIncorrectFileString =
                "x<-c(" + incorrectX + "[1], "
                        + incorrectX + "[2], " + incorrectX + "[3]); y<-c("
                        + incorrectY + "[1], " + incorrectY + "[2], "
                        + incorrectY + "[3]); z<-data.frame(x, y); " +
                        "names(z) <- c(\"new X name\", \"new Y name\"); z";

    }

    @AfterClass
    public static void after() throws Exception {
        FileRepository.getRepo().deleteFileById(correctFileId);
        FileRepository.getRepo().deleteFileById(incorrectFileId);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalArgument4() throws Exception {
        call.runScriptToGetVector((String) null,
                (String) null,
                (ArrayList<DataSet>) null);
    }

    @Test
    public void testCorrectCommandToGetScalarCorrectFile() {
        try {
            ScalarResult resX;
            ScalarResult resY;
            for (int i = 0; i < dataArray.getByKey("x").size(); i++) {
                resX = call.runScriptToGetScalar("", correctX + "[" +
                        (int) (i + 1) + "]", correct);
                resY = call.runScriptToGetScalar("", correctY + "[" +
                        (int) (i + 1) + "]", correct);
                assertTrue("Scalar isn't returned correctly from Renjin",
                        doubleEqual(resX.getValue().getDoubleValue(),
                                dataArray.getByKey("x").get(i).getDoubleValue()) &&
                                doubleEqual(resY.getValue().getDoubleValue(),
                                        dataArray.getByKey("y").get(i).getDoubleValue()));
            }
        } catch (Exception e) {
            fail("Scalar isn't returned  correctly from Renjin");
        }
    }

    @Test
    public void testCorrectCommandToGetVectorCorrectFile() {
        try {
            VectorResult res = call.runScriptToGetVector("", "c(" + correctX +
                    "[5], " + correctY + "[5])", correct);
            assertTrue("Vector isn't returned correctly from Renjin",
                    doubleEqual(dataArray.getByKey("x").get(4).getDoubleValue(),
                            res.getValue().get(0).getDoubleValue()) &&
                            doubleEqual(dataArray.getByKey("y").get(4).getDoubleValue(),
                                    res.getValue().get(1).getDoubleValue()));
        } catch (Exception e) {
            fail("Vector isn't returned correctly from Renjin");
        }
    }

    @Test
    public void testCorrectCommandToGetVectorsCorrectFileFromDataFrame() {
        try {
            Map<String, List<DataEntry>> w = new HashMap<String, List<DataEntry>>();
            w.put("new X name", new ArrayList<DataEntry>());
            w.put("new Y name", new ArrayList<DataEntry>());
            for (Map.Entry<String, List<DataEntry>> entry : w.entrySet()) {
                entry.getValue().add(new DataEntry(DataEntryType.DOUBLE, 0.));
                entry.getValue().add(new DataEntry(DataEntryType.DOUBLE, 1.));
                entry.getValue().add(new DataEntry(DataEntryType.DOUBLE, 2.));
            }
            VectorsResult was = new VectorsResult(w);
            VectorsResult res =
                    call.runScriptToGetVectors("",
                            correctScriptForCorrectFileString,
                            correct);
            assertTrue("Vectors aren't returned correctly from Renjin",
                    was.equals(res));
        } catch (Exception e) {
            fail("Vectors aren't returned correctly from Renjin");
        }
    }


    @Test(expected = Exception.class)
    public void testIncorrectCommandToGetScalarCorrectFile() throws Exception {
        call.runScriptToGetScalar("", correctX + "]", correct);
    }

    @Test(expected = Exception.class)
    public void testIncorrectCommandToGetVectorCorrectFile() throws Exception {
        call.runScriptToGetVector("", "c" + correctX + "[5," +
                correctY + "[5)", correct);
    }

    @Test(expected = Exception.class)
    public void testIncorrectCommandToGetVectorsCorrectFile() throws Exception {
        call.runScriptToGetVectors("", incorrectScriptForCorrectFileString,
                correct);
    }

    @Test(expected = Exception.class)
    public void testCorrectCommandToGetScalarIncorrectFile() throws Exception {
        call.runScriptToGetScalar("", incorrectX + "[5]", incorrect);
    }

    @Test(expected = Exception.class)
    public void testCorrectCommandToGetVectorIncorrectFile() throws Exception {
        call.runScriptToGetVector("", "c(" + incorrectX + "[5], " +
                incorrectY + "[5])", incorrect);
    }

    @Test(expected = Exception.class)
    public void testCorrectCommandToGetVectorsIncorrectFile() throws Exception {
        call.runScriptToGetVectors("", correctScriptForIncorrectFileString,
                incorrect);
    }
}
