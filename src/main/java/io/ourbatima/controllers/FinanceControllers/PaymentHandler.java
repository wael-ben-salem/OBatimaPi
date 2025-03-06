package io.ourbatima.controllers.FinanceControllers;
import okhttp3.*;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PaymentHandler {

    private static final String API_KEY = "fd2364a8-d309-410d-9a33-fb194cba75e2";
    private static final String API_SECRET = "e6d6e35f-c7c1-4a7d-9c31-30d39d40e6f5";
    private static final String INITIATE_PAYMENT_URL = "https://api.flouci.com/api/payment/initiate";

    public void startPayment(double amount, String currency, String callbackUrl) {
        OkHttpClient client = new OkHttpClient();

        // Create JSON request body
        String json = String.format("{\"amount\": %.2f, \"currency\": \"%s\", \"callback_url\": \"%s\"}",
                amount, currency, callbackUrl);
        RequestBody body = RequestBody.create(json, MediaType.parse("application/json"));

        // Create request
        Request request = new Request.Builder()
                .url(INITIATE_PAYMENT_URL)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("API-Key", API_KEY)
                .addHeader("API-Secret", API_SECRET)
                .build();

        // Execute request asynchronously
        client.newCall(request).enqueue(new Callback() {


            @Override
            public void onFailure(Call call, IOException e) {
                // Handle failure
                Platform.runLater(() -> {
                    System.err.println("Payment failed: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Platform.runLater(() -> {
                        System.out.println("Payment initiated successfully: " + responseBody);
                        // Redirect the user to the payment URL (if provided in the response)
                        // Example: openUrlInBrowser(responseBody.getPaymentUrl());
                    });
                } else {
                    Platform.runLater(() -> {
                        try {
                            System.err.println("Payment failed: " + response.body().string());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        });
    }
}


