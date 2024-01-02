package com.seminar.rust;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;

public class Main {
    private static final int PORT = 8080;
    private static final ExecutorService executorService = new ThreadPoolExecutor(50, 1000, 100, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.submit(() -> handleRequest(clientSocket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String handleGet(String request, ObjectMapper objectMapper) {
        try {
            String response_data = "";
            int username_index = request.indexOf("?username=");
            if(username_index >= 0) {
                username_index += 10;
                int username_end = request.indexOf(" ", username_index);
                String username = request.substring(username_index, username_end);
                User user = Map.getUser(username);
                response_data = objectMapper.writeValueAsString(user);
                if(response_data == null) 
                    return "HTTP/1.1 404 Not Found\r\n\r\nUser not found!";
            } else {
                response_data = objectMapper.writeValueAsString(Map.getAllUsers());
            }
            return "HTTP/1.1 200 OK\r\n\r\n" + response_data;
        } catch (Exception e) {
            e.printStackTrace();
            return "HTTP/1.1 500 Internal Server Error\r\n\r\n" + e.getMessage();
        }
    }

    private static String handlePut(String request, ObjectMapper objectMapper, BufferedReader reader) {       
        try {
            StringBuilder requestBody = new StringBuilder();
            while (reader.ready()) {
                requestBody.append((char) reader.read());
            }

            Integer bodyStart = requestBody.indexOf("{"), bodyEnd = requestBody.lastIndexOf("}") + 1;
            if(bodyStart < 0 || bodyEnd < 0) {
                return "HTTP/1.1 400 Bad Request\r\n\r\nInvalid request body!";
            } else {
                String body = requestBody.substring(bodyStart, bodyEnd);
                User user = objectMapper.readValue(body, User.class);
                Map.addUser(user);
                return "HTTP/1.1 200 OK\r\n\r\nReceived PUT request!";
            }
        } catch(Exception e){
            e.printStackTrace();
            return "HTTP/1.1 500 Internal Server Error\r\n\r\n" + e.getMessage();
        }
    }

    private static void handleRequest(Socket clientSocket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {
            //Thread.sleep(10);
            while (!reader.ready()) continue;
            
            String request = reader.readLine();
            ObjectMapper objectMapper = new ObjectMapper();

            if (request.startsWith("GET")) {
                writer.write(handleGet(request, objectMapper));
            } else if (request.startsWith("PUT")) {
                writer.write(handlePut(request, objectMapper, reader));
            } else {
                writer.write("HTTP/1.1 405 Method Not Allowed\r\n\r\nUnsupported method!");
            }


            writer.flush();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
