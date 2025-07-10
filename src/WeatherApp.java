import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;


//Backend where we'll retrive weather data from API
public class WeatherApp {
    public static JSONObject getWeatherData(String locationName) {
        JSONArray locationData = getLocationData(locationName);
        if (locationData == null || locationData.isEmpty()) {
            // Return null or an empty object to handle it in GUI
            return null;
        }

        JSONObject location=(JSONObject) locationData.get(0);
        double latitude=(double) location.get("latitude");
        double longitude=(double) location.get("longitude");

        String urlString="https://api.open-meteo.com/v1/forecast?"+"latitude="+latitude+"&longitude="+longitude+"&hourly=temperature_2m,relativehumidity_2m,weather_code,windspeed_10m&timezone=America%2FLos_Angeles\n";

        try{
            HttpURLConnection conn= fetchAPIResponse(urlString);

            if(conn.getResponseCode()!=200){
                System.out.println("Error:Could not connect to API");
                return null;
            }
            StringBuilder resultJson=new StringBuilder();
            Scanner scanner=new Scanner(conn.getInputStream());
            while(scanner.hasNext()){
                resultJson.append(scanner.nextLine());
            }
            scanner.close();
            conn.disconnect();

            JSONParser parser=new JSONParser();
            JSONObject resultJsonObj=(JSONObject)  parser.parse(String.valueOf(resultJson));

            JSONObject hourly=(JSONObject) resultJsonObj.get("hourly");

            JSONArray time=(JSONArray) hourly.get("time");
            int index= findIndexOfCurrentTime(time);

            JSONArray temperatureData=(JSONArray) hourly.get(("temperature_2m"));

            double temperature=(double) temperatureData.get(index);

            JSONArray weather_code=(JSONArray) hourly.get("weather_code");
            String weatherCondition=convertWeatherCode((long) weather_code.get(index));

            JSONArray relativeHumidity=(JSONArray)  hourly.get("relativehumidity_2m");
            long humidity=(long) relativeHumidity.get(index);

            JSONArray windspeedData=(JSONArray) hourly.get("windspeed_10m");
            double windspeed=(double) windspeedData.get(index);

            JSONObject weatherData=new JSONObject();
            weatherData.put("temperature",temperature);
            weatherData.put("weather_condition",weatherCondition);
            weatherData.put("humidity",humidity);
            weatherData.put("windspeed",windspeed);

            return weatherData;

        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray getLocationData(String locationName){
        locationName=locationName.replaceAll(" ","+");

        String urlString="https://geocoding-api.open-meteo.com/v1/search?name="+locationName+"&count=10&language=en&format=json";

        try {
            HttpURLConnection conn = fetchAPIResponse(urlString);

            if(conn.getResponseCode()!=200){
                System.out.println("Error:Could not connect to API");
                return null;
            }
            else{
                StringBuilder resultJson=new StringBuilder();
                Scanner scanner=new Scanner(conn.getInputStream());

                while (scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }
                scanner.close();
                conn.disconnect();

                JSONParser parser=new JSONParser();
                JSONObject resultJsonObj=(JSONObject) parser.parse(String.valueOf(resultJson));

                JSONArray locationData=(JSONArray) resultJsonObj.get("results");
                return locationData;
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static  HttpURLConnection fetchAPIResponse(String urlString){
        try{
            URL url=new URL(urlString);
            HttpURLConnection conn=(HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            conn.connect();
            return conn;
        }
        catch(IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private static int findIndexOfCurrentTime(JSONArray timelist){
        String currentTime=getCurrentTime();
        for (int i=0;i<timelist.size();i++){
            String time=(String) timelist.get(i);
            if(time.equalsIgnoreCase(currentTime)){
                return i;
            }
        }

        return 0;
    }
    public static String getCurrentTime(){
        LocalDateTime currentDataTime=LocalDateTime.now();

        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        String formatDateTime=currentDataTime.format(formatter);

        return formatDateTime;

    }

    private static String convertWeatherCode(long weather_code){
        String weatherCondition="";
        if(weather_code==0L){
            weatherCondition="Clear";
        }
        else if(weather_code > 0L && weather_code <= 3L){
            // cloudy
            weatherCondition = "Cloudy";
        }else if((weather_code >= 51L && weather_code <= 67L)
                || (weather_code >= 80L && weather_code <= 99L)){
            // rain
            weatherCondition = "Rain";
        }else if(weather_code >= 71L && weather_code <= 77L){
            // snow
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }
}
