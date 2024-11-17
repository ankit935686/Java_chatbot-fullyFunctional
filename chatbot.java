import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;


public class Chatbot {
    // Define the API endpoint and your API key here
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions"; // Replace with the correct endpoint if needed
    private static final String API_KEY = "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"; // Replace with your actual API key

    // Method to interact with the chatbot
    private static String getChatbotResponse(String userMessage) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setDoOutput(true);

            // JSON payload for the API request
            String jsonInput = "{"
                    + "\"model\": \"llama-3.1-8b-instant\","
                    + "\"messages\": [{\"role\": \"user\", \"content\": \"" + userMessage + "\"}]"
                    + "}";

            // Write JSON input to output stream
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInput.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read success response
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                br.close();
                connection.disconnect();
                return parseResponse(response.toString());
            } else {
                // Read error response
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
                StringBuilder errorResponse = new StringBuilder();
                String errorLine;
                while ((errorLine = errorReader.readLine()) != null) {
                    errorResponse.append(errorLine.trim());
                }
                errorReader.close();
                System.out.println("Error: HTTP " + responseCode + " - " + errorResponse.toString());
                return "Failed to get a valid response from the server.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Sorry, I couldn't process your request.";
        }
    }

    // Simple method to parse response (update as per your API's response format)
    private static String parseResponse(String jsonResponse) {
        try {
            // Find the start and end of the "content" field
            int startIndex = jsonResponse.indexOf("\"content\":\"") + "\"content\":\"".length();
            int endIndex = jsonResponse.indexOf("\"", startIndex);

            // Extract the content
            String botMessage = jsonResponse.substring(startIndex, endIndex);
            return botMessage;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to parse the response.";
        }
    }


    // Main method with menu system to interact with the chatbot
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the AI Chatbot!");
        System.out.println("Type 'exit' to quit.\n");

        while (true) {
            System.out.print("You: ");
            String userMessage = scanner.nextLine();

            if ("exit".equalsIgnoreCase(userMessage)) {
                System.out.println("Goodbye!");
                break;
            }

            String botResponse = getChatbotResponse(userMessage);
            System.out.println("Bot: " + botResponse);
        }

        scanner.close();
    }
}
