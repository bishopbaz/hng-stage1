package com.hng.stage1.service;

import com.hng.stage1.entities.HelloResponse;
import com.hng.stage1.entities.WeatherInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class HelloService {
    private static final String API_URL = "http://api.weatherapi.com/v1/current.json";
    private static final String API_KEY = "c65399fdabb2437ebc0131259240307";


    public ResponseEntity<HelloResponse> sayHello(String visitorName, HttpServletRequest request) {
        String clientIp = getClientIpAddress(request);

        WeatherInfo weatherInfo = getWeatherInfo(clientIp);
        String location = weatherInfo.getCity();

        double temperature = weatherInfo.getTemperatureC();


        String greeting = String.format("Hello, %s! The temperature is %f degrees Celsius in %s", removeSurroundingQuotes(visitorName), temperature, location);
        HelloResponse helloResponse = new HelloResponse(clientIp, location,greeting);
        return new ResponseEntity<>(helloResponse, HttpStatus.OK);

    }


    public String getClientIpAddress(HttpServletRequest request) {
        String ipAddress = getIpFromHeaders(request);
        return ipAddress != null ? ipAddress : request.getRemoteAddr();
    }

    private String getIpFromHeaders(HttpServletRequest request) {
        String[] headers = {
                "X-Forwarded-For",
                "X-Real-IP",
                "Forwarded",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_CLIENT_IP",
                "HTTP_X_FORWARDED_FOR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }
        return null;
    }


    private String removeSurroundingQuotes(String input) {
        // Use regex to remove leading and trailing escaped quotes
        Pattern pattern = Pattern.compile("^\"(.*)\"$");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return input;
    }

    public WeatherInfo getWeatherInfo(String clientIp) {
        WebClient webClient = WebClient.builder()
                .baseUrl(API_URL)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        String apiUrl = "?key=" + API_KEY + "&q=" + clientIp + "&aqi=no";

        try {
            String response = webClient.get()
                    .uri(apiUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block(); // Blocking to convert Mono to actual response (consider using non-blocking in real apps)

            JSONObject jsonResponse = new JSONObject(response);
            String locationName = jsonResponse.getJSONObject("location").getString("name");
            double temperatureC = jsonResponse.getJSONObject("current").getDouble("temp_c");

            return new WeatherInfo(locationName, temperatureC);

        } catch (WebClientResponseException e) {
            System.err.println("WebClient Error: " + e.getStatusCode() + " " + e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw e;
        }
    }


    public String getPublicIpAddress() {
        StringBuilder ipAddress = new StringBuilder();
        try {
            URL url = new URL("https://httpbin.org/ip");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                ipAddress.append(line);
            }

            reader.close();
            connection.disconnect();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Parse the JSON response to extract the public IP address using a JSON library
        String response = ipAddress.toString();
        JSONObject jsonResponse = new JSONObject(response);
        String publicIp = jsonResponse.getString("origin");

        // Remove any trailing backslash if present
        publicIp = publicIp.endsWith("\\") ? publicIp.substring(0, publicIp.length() - 1) : publicIp;

        return publicIp;
    }




    private int fetchTemperature(HttpServletRequest request) {
        try {
            // Replace with your weather API key and appropriate API endpoint
            String apiKey = "your_api_key";
            String ipAddress = request.getRemoteAddr(); // Get client IP address
            String weatherUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + ipAddress + "&appid=" + apiKey;

            URL url = new URL(weatherUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject jsonObject = new JSONObject(response.toString());
            JSONObject main = jsonObject.getJSONObject("main");
            double tempKelvin = main.getDouble("temp");
            return (int) (tempKelvin - 273.15); // Convert Kelvin to Celsius
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0; // Default temperature if fetch fails
    }
}
