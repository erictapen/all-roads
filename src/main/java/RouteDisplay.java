import processing.core.PApplet;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class RouteDisplay extends PApplet {
	
	private final int WINDOW_SIZE = 1024;

	private final double CENTER_LAT = 48.52022;
	private final double CENTER_LON = 9.05407;
	private final double RADIUS = 0.01;
	
	private DecimalFormat df = new DecimalFormat("#.######");

	/**
	 * 
	 */
	private static final long serialVersionUID = 3973182331557558944L;

	public RouteDisplay() {
		

	}

	@Override
	public void setup() {
		size(WINDOW_SIZE, WINDOW_SIZE);
		background(0xffffff);
	}

	@Override
	public void draw() {
		double randLat = (Math.random() * 2.0 - 1.0) * RADIUS + CENTER_LAT;
		double randLon = (Math.random() * 2.0 - 1.0) * RADIUS + CENTER_LON;
		
		String url = "http://www.yournavigation.org/api/1.0/gosmore.php?" 
				+ "flat=" + df.format(randLat) 
				+ "&flon=" + df.format(randLon)
				+ "&tlat=" + df.format(CENTER_LAT)
				+ "&tlon=" + df.format(CENTER_LON) 
				+ "&v=motorcar" 
				+ "&fast=1" 
				+ "&layer=mapnik" 
				+ "&format=geojson";
		JSONArray json = getJSONFromAPI(url).getJSONArray("coordinates");
		
		
		//do not use API too much
		delay(3000);
		
		ArrayList<PVector> route = new ArrayList<PVector>();
		for (int i = 0; i < json.size(); i++) {
			float lon = json.getJSONArray(i).getFloat(0);
			float lat = json.getJSONArray(i).getFloat(1);
			//normalize
			lat -= CENTER_LAT;
			lon -= CENTER_LON;
			lat /= RADIUS;
			lon /= RADIUS;
			lat *= WINDOW_SIZE/2;
			lon *= WINDOW_SIZE/2;
			route.add(new PVector(lat, lon));
		}
		
		System.out.println(route);
		
		stroke(0);
		
		for (int i = 0; i < route.size() - 1; i++) {
			line(route.get(i).x + WINDOW_SIZE/2, route.get(i).y + WINDOW_SIZE/2, route.get(i+1).x + WINDOW_SIZE/2, route.get(i+1).y + WINDOW_SIZE/2);
//			System.out.println((route.get(i).x + WINDOW_SIZE/2) + " h " + (route.get(i).y + WINDOW_SIZE/2)  + " h " +  (route.get(i+1).x + WINDOW_SIZE/2) + " h " + (route.get(i+1).y + WINDOW_SIZE/2));
		}
		saveFrame();

	}

	private JSONObject getJSONFromAPI(String urltext) {
		URL url = null;
		URLConnection uc = null;
		JSONObject json = null;
		try {
			url = new URL(urltext);
			uc = url.openConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try (BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));) {
			json = new JSONObject(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}

}
