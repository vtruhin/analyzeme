package com.analyzeme.R.call;

import com.analyzeme.analyze.Point;
import com.analyzeme.data.DataSet;
import com.analyzeme.parsers.JsonParser;
import com.analyzeme.streamreader.StreamToString;
import org.rosuda.REngine.Rserve.RConnection;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lagroffe on 20.03.2016 0:38
 */

public class Rserve implements IRCaller {
	private static RConnection r = null;

	//TODO: in the future should use Settings (for host+port info)
	private void Initialize() throws Exception {
		if (r == null) {
			r = new RConnection();
		}
	}

	//------------------
	//default for scripts
	//return - json
	//may be errors
	//------------------

	/**
	 * @param scriptName - name of the script to be called
	 * @param rScript    - script to call, correct .R file as a stream
	 * @param dataFiles  - data necessary for the script
	 * @return json form of result (may be errors)
	 * @throws Exception if failed to call R or script errored
	 */
	public String runScript(String scriptName, ByteArrayInputStream rScript, ArrayList<DataSet> dataFiles) throws Exception {
		if (scriptName.equals("") || scriptName == null || rScript == null || dataFiles == null || dataFiles.isEmpty())
			throw new IllegalArgumentException();
		String result = null;
		//TODO: implement
		return result;
	}


	//------------------
	//script for files
	//------------------

	/**
	 * @param scriptName - name of the script to be called
	 * @param rScript    - script to call, correct .R file as a stream
	 * @param dataFiles  - data necessary for the script
	 * @return double result
	 * @throws Exception if failed to call R or script errored
	 */
	public double runScriptToGetNumber(String scriptName, ByteArrayInputStream rScript, ArrayList<DataSet> dataFiles) throws Exception {
		if (scriptName.equals("") || scriptName == null || rScript == null || dataFiles == null || dataFiles.isEmpty())
			throw new IllegalArgumentException();
		double result = 0;
		//TODO: implement
		return result;
	}

	/**
	 * @param scriptName - name of the script to be called
	 * @param rScript    - script to call, correct .R file as a stream
	 * @param dataFiles  - data necessary for the script
	 * @return one point
	 * @throws Exception if failed to call R or script errored
	 */
	public Point runScriptToGetPoint(String scriptName, ByteArrayInputStream rScript, ArrayList<DataSet> dataFiles) throws Exception {
		if (scriptName.equals("") || scriptName == null || rScript == null || dataFiles == null || dataFiles.isEmpty())
			throw new IllegalArgumentException();
		Point result = null;
		//TODO: implement
		return result;
	}

	/**
	 * @param scriptName - name of the script to be called
	 * @param rScript    - script to call, correct .R file as a stream
	 * @param dataFiles  - data necessary for the script
	 * @return List<Point>
	 * @throws Exception if failed to call R or script errored
	 */
	public List<Point> runScriptToGetPoints(String scriptName, ByteArrayInputStream rScript, ArrayList<DataSet> dataFiles) throws Exception {
		if (scriptName.equals("") || scriptName == null || rScript == null || dataFiles == null || dataFiles.isEmpty())
			throw new IllegalArgumentException();
		List<Point> result = new ArrayList<Point>();
		//TODO: implement
		return result;
	}

	//------------------
	//default for commands
	//return - json
	//may be errors
	//------------------

	/**
	 * @param rCommand  - string with a command in R language
	 * @param dataFiles - data necessary for the script
	 * @return json form of result (may be errors)
	 * @throws Exception if failed to call R or command errored
	 */
	public String runCommand(String rCommand, ArrayList<DataSet> dataFiles) throws Exception {
		if (rCommand.equals("") || rCommand == null || dataFiles == null || dataFiles.isEmpty())
			throw new IllegalArgumentException();
		String result = null;
		//TODO: implement
		return result;
	}

	/**
	 * @param rCommand - string with a command in R language
	 * @param jsonData - data necessary for the script
	 * @return json form of result (may be errors)
	 * @throws Exception if failed to call R or command errored
	 */
	public String runCommand(String rCommand, String jsonData) throws Exception {
		if (rCommand.equals("") || rCommand == null || jsonData == null || jsonData.isEmpty())
			throw new IllegalArgumentException();
		String result = null;
		//TODO: implement
		return result;
	}


	//------------------
	//command for files
	//------------------

	/**
	 * @param rCommand  - string with a command in R language
	 * @param dataFiles - data necessary for the script
	 * @return double result
	 * @throws Exception if failed to call R or command errored
	 */
	public double runCommandToGetNumber(String rCommand, ArrayList<DataSet> dataFiles) throws Exception {
		//dataFiles can be empty for simple commands
		if (rCommand.equals("") || rCommand == null || dataFiles == null)
			throw new IllegalArgumentException();
		Initialize();

		for (DataSet data : dataFiles) {
			if (data.getFields().contains("x") || data.getFields().contains("y")) {
				ByteArrayInputStream file = data.getData();
				InputStream is = new ByteArrayInputStream(StreamToString.ConvertStream(file).getBytes());
				JsonParser jsonParser;
				jsonParser = new JsonParser();
				Point[] points = jsonParser.getPointsFromPointJson(is);

				double[] x = new double[points.length];
				double[] y = new double[points.length];
				for (int i = 0; i < points.length; i++) {
					x[i] = points[i].GetX();
					y[i] = points[i].GetY();
				}
				if (data.getFields().contains("x"))
					r.assign("x_from__repo__" + data.getNameForUser() + "__", x);
				if (data.getFields().contains("y"))
					r.assign("y_from__repo__" + data.getNameForUser() + "__", y);
			}
		}
		double result = r.eval("result <-" + rCommand).asDouble();
		return result;
	}

	/**
	 * @param rCommand  - string with a command in R language
	 * @param dataFiles - data necessary for the script
	 * @return one point
	 * @throws Exception if failed to call R or command errored
	 */
	public Point runCommandToGetPoint(String rCommand, ArrayList<DataSet> dataFiles) throws Exception {
		if (rCommand.equals("") || rCommand == null || dataFiles == null)
			throw new IllegalArgumentException();
		Initialize();

		for (DataSet data : dataFiles) {
			if (data.getFields().contains("x") || data.getFields().contains("y")) {
				ByteArrayInputStream file = data.getData();
				InputStream is = new ByteArrayInputStream(StreamToString.ConvertStream(file).getBytes());
				JsonParser jsonParser;
				jsonParser = new JsonParser();
				Point[] points = jsonParser.getPointsFromPointJson(is);

				double[] x = new double[points.length];
				double[] y = new double[points.length];
				for (int i = 0; i < points.length; i++) {
					x[i] = points[i].GetX();
					y[i] = points[i].GetY();
				}
				if (data.getFields().contains("x"))
					r.assign("x_from__repo__" + data.getNameForUser() + "__", x);
				if (data.getFields().contains("y"))
					r.assign("y_from__repo__" + data.getNameForUser() + "__", y);
			}
		}
		double[] res = r.eval(rCommand).asDoubles();
		Point result = new Point();
		result.SetX(res[0]);
		result.SetY(res[1]);
		return result;
	}

	/**
	 * @param rCommand  - string with a command in R language
	 * @param dataFiles - data necessary for the script
	 * @return List<Point>
	 * @throws Exception if failed to call R or command errored
	 */
	public List<Point> runCommandToGetPoints(String rCommand, ArrayList<DataSet> dataFiles) throws Exception {
		if (rCommand.equals("") || rCommand == null || dataFiles == null)
			throw new IllegalArgumentException();
		Initialize();

		for (DataSet data : dataFiles) {
			if (data.getFields().contains("x") || data.getFields().contains("y")) {
				ByteArrayInputStream file = data.getData();
				InputStream is = new ByteArrayInputStream(StreamToString.ConvertStream(file).getBytes());
				JsonParser jsonParser;
				jsonParser = new JsonParser();
				Point[] points = jsonParser.getPointsFromPointJson(is);

				double[] x = new double[points.length];
				double[] y = new double[points.length];
				for (int i = 0; i < points.length; i++) {
					x[i] = points[i].GetX();
					y[i] = points[i].GetY();
				}
				if (data.getFields().contains("x"))
					r.assign("x_from__repo__" + data.getNameForUser() + "__", x);
				if (data.getFields().contains("y"))
					r.assign("y_from__repo__" + data.getNameForUser() + "__", y);
			}
		}
		double[][] res = r.eval(rCommand).asDoubleMatrix();
		ArrayList<Point> result = new ArrayList<Point>();
		for (int i = 0; i < res.length; i++) {
			Point p = new Point();
			p.SetX(res[i][0]);
			p.SetY(res[i][1]);
			result.add(p);
		}
		return result;
	}

	//------------------
	//command for data
	//------------------

	/**
	 * @param rCommand - string with a command in R language
	 * @param jsonData - data necessary for the script
	 * @return double result
	 * @throws Exception if failed to call R or command errored
	 */
	public double runCommandToGetNumber(String rCommand, String jsonData) throws Exception {
		if (rCommand.equals("") || rCommand == null || jsonData == null || jsonData.isEmpty())
			throw new IllegalArgumentException();
		Initialize();

		InputStream is = new ByteArrayInputStream(jsonData.getBytes());
		JsonParser jsonParser;
		jsonParser = new JsonParser();
		Point[] data = jsonParser.getPointsFromPointJson(is);

		double[] x = new double[data.length];
		double[] y = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			x[i] = data[i].GetX();
			y[i] = data[i].GetY();
		}
		r.assign("x", x);
		r.assign("y", y);
		double result = r.eval(rCommand).asDouble();
		return result;
	}

	/**
	 * @param rCommand - string with a command in R language
	 * @param jsonData - data necessary for the script
	 * @return one point
	 * @throws Exception if failed to call R or command errored
	 */
	public Point runCommandToGetPoint(String rCommand, String jsonData) throws Exception {
		if (rCommand.equals("") || rCommand == null || jsonData == null || jsonData.isEmpty())
			throw new IllegalArgumentException();
		Initialize();

		InputStream is = new ByteArrayInputStream(jsonData.getBytes());
		JsonParser jsonParser;
		jsonParser = new JsonParser();
		Point[] data = jsonParser.getPointsFromPointJson(is);

		double[] x = new double[data.length];
		double[] y = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			x[i] = data[i].GetX();
			y[i] = data[i].GetY();
		}
		r.assign("x", x);
		r.assign("y", y);
		double[] res = r.eval(rCommand).asDoubles();
		Point result = new Point();
		result.SetX(res[0]);
		result.SetY(res[1]);
		return result;
	}

	/**
	 * @param rCommand - string with a command in R language
	 * @param jsonData - data necessary for the script
	 * @return List<Point>
	 * @throws Exception if failed to call R or command errored
	 */
	public List<Point> runCommandToGetPoints(String rCommand, String jsonData) throws Exception {
		if (rCommand.equals("") || rCommand == null || jsonData == null || jsonData.isEmpty())
			throw new IllegalArgumentException();

		Initialize();

		InputStream is = new ByteArrayInputStream(jsonData.getBytes());
		JsonParser jsonParser;
		jsonParser = new JsonParser();
		Point[] data = jsonParser.getPointsFromPointJson(is);

		double[] x = new double[data.length];
		double[] y = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			x[i] = data[i].GetX();
			y[i] = data[i].GetY();
		}
		r.assign("x", x);
		r.assign("y", y);
		double[][] res = r.eval(rCommand).asDoubleMatrix();
		ArrayList<Point> result = new ArrayList<Point>();
		for (int i = 0; i < res.length; i++) {
			Point p = new Point();
			p.SetX(res[i][0]);
			p.SetY(res[i][1]);
			result.add(p);
		}
		return result;
	}
}