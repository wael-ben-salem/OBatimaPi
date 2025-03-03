package io.ourbatima.controllers;


import com.google.gson.*;
import io.ourbatima.core.interfaces.ActionView;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.time.format.*;

public class Weather extends ActionView {
    private static final String API_KEY = "11e2adbc6bf84b61bbd03821250303";
    private static final String BASE_URL = "http://api.weatherapi.com/v1/forecast.json?key=" + API_KEY;

    @FXML private Label currentTemp, conditionText, feelsLike, humidity, windSpeed, uvIndex;
    @FXML private HBox forecastContainer;
    @FXML private TextField locationField;

    @FXML
    public void initialize() {
        fetchWeather();
    }

    @FXML
    private void fetchWeather() {
        String location = locationField.getText().isEmpty() ? "Tunis" : locationField.getText();

        try {
            String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
            String apiUrl = BASE_URL + "&q=" + encodedLocation + "&days=3&aqi=no&alerts=no";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(apiUrl))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenAccept(this::updateUI)
                    .exceptionally(e -> {
                        showAlert("Error: " + e.getCause().getMessage());
                        return null;
                    });

        } catch (Exception e) {
            showAlert("Error creating request: " + e.getMessage());
        }
    }

    private void updateUI(String jsonResponse) {
        try {
            Gson gson = new Gson();
            WeatherData data = gson.fromJson(jsonResponse, WeatherData.class);

            javafx.application.Platform.runLater(() -> {
                // Update current weather
                currentTemp.setText(String.format("%.1f°C", data.current.temp_c));
                conditionText.setText(data.current.condition.text);
                feelsLike.setText(String.format("Feels like: %.1f°C", data.current.feelslike_c));
                humidity.setText("Humidity: " + data.current.humidity + "%");
                windSpeed.setText("Wind: " + data.current.wind_kph + " km/h");
                uvIndex.setText("UV Index: " + data.current.uv);

                // Update forecast
                forecastContainer.getChildren().clear();
                for (ForecastDay day : data.forecast.forecastday) {
                    forecastContainer.getChildren().add(createForecastCard(day));
                }
            });
        } catch (JsonSyntaxException e) {
            showAlert("Error parsing response: " + e.getMessage());
        }
    }

    private VBox createForecastCard(ForecastDay day) {
        VBox card = new VBox(10);
        card.getStyleClass().add("forecast-card");

        Label dateLabel = new Label(LocalDate.parse(day.date)
                .format(DateTimeFormatter.ofPattern("E, MMM d")));
        dateLabel.getStyleClass().add("forecast-date");

        Label tempLabel = new Label(String.format("Max: %.1f°C\nMin: %.1f°C",
                day.day.maxtemp_c, day.day.mintemp_c));
        tempLabel.getStyleClass().add("forecast-temp");

        Label conditionLabel = new Label(day.day.condition.text);
        conditionLabel.getStyleClass().add("forecast-condition");

        card.getChildren().addAll(dateLabel, tempLabel, conditionLabel);
        return card;
    }

    private void showAlert(String message) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    // Public data classes with exact API response structure
    public class WeatherData {
        public Location location;
        public Current current;
        public Forecast forecast;
    }

    public class Location {
        public String name;
        public String country;
    }

    public class Current {
        public double temp_c;
        public double feelslike_c;
        public int humidity;
        public double wind_kph;
        public double uv;
        public Condition condition;
    }

    public class Forecast {
        public ForecastDay[] forecastday;
    }

    public class ForecastDay {
        public String date;
        public Day day;
    }

    public class Day {
        public double maxtemp_c;
        public double mintemp_c;
        public Condition condition;
    }

    public class Condition {
        public String text;
    }
}