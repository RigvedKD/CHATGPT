import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class GPTServer {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(1234);
            System.out.println("Server started. Waiting for clients...");

            Socket socket = serverSocket.accept();
            System.out.println("Client connected.");

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            while (true) {
                String message = reader.readLine();
                System.out.println("Client: " + message);

                // Send the message to Chat GPT for processing and get the response
                String response = processMessageWithChatGPT(message);

                writer.write(response);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String processMessageWithChatGPT(String message) {
        String apiKey = "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";
        String apiUrl = "https://api.openai.com/v1/chat/completions";
        String model = "gpt-3.5-turbo";

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);

            // Construct the request payload
            String payload = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"system\", \"content\": \"You are a helpful assistant.\"}, {\"role\": \"user\", \"content\": \"" + message + "\"}]}";
            byte[] postData = payload.getBytes(StandardCharsets.UTF_8);
            connection.setDoOutput(true);
            try (DataOutputStream writer = new DataOutputStream(connection.getOutputStream())) {
                writer.write(postData);
            }

            // Read the response from Chat GPT
            InputStream responseStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            // Parse the response JSON and extract the generated response
            String jsonResponse = response.toString();
            String gptResponse = jsonResponse.split("\"content\": \"")[1].split("\"")[0];

            return "Chat GPT response: " + gptResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Failed to process the message.";
    }
}






