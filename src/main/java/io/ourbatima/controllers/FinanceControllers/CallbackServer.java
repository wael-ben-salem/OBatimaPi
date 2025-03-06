package io.ourbatima.controllers.FinanceControllers;

import static spark.Spark.*;

public class CallbackServer {
    public static void main(String[] args) {
        port(4567); // Run on port 4567

        post("/payment-callback", (req, res) -> {
            String callbackData = req.body();
            System.out.println("Received callback: " + callbackData);

            // Process the callback data (e.g., update UI or database)
            return "Callback received!";
        });
    }
}