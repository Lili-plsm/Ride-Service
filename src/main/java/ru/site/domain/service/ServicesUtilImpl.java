package ru.site.domain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class ServicesUtilImpl implements ServicesUtil {

    private final ObjectMapper objectMapper;

    public ServicesUtilImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public double calculateDistance(double startlat, double startlon,
                                    double endlat, double endlon) {

        try {
            String stringUrl = String.format(
                Locale.US,
                "http://router.project-osrm.org/route/v1/driving/%f,%f;%f,%f?alternatives=true&geometries=polyline",
                startlon, startlat, endlon, endlat);
            URL url = URI.create(stringUrl).toURL();
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
                StringBuilder responseContent = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    responseContent.append(inputLine);
                }
                in.close();

                String jsonResponse = responseContent.toString();
                JsonNode root = objectMapper.readTree(jsonResponse);

                JsonNode routes = root.path("routes");
                if (routes.isArray() && routes.size() > 0) {
                    double distance = objectMapper.readTree(jsonResponse)
                                          .path("routes")
                                          .get(0)
                                          .path("legs")
                                          .get(0)
                                          .path("distance")
                                          .asDouble() /
                                      1000;
                    return distance;
                } else {
                    throw new RuntimeException(
                        "OSRM не смог построить маршрут для координат: " + startlat + "," + startlon + " -> " + endlat + "," + endlon);
                }
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Некорректный URL: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при подключении: " + e.getMessage(), e);
        }
        return -1;
    }
}
