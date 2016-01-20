package org.telegram;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Helper for communicating with the Telegram CLI Client via the Telegram UI-Test Request Server
 *
 * Created by Martin Perebner on 20/01/16.
 */
public class TelegramHelper {

    /**
     * @return last confirmation code (user is specified by the server)
     * @throws IOException
     * @throws JSONException
     */
    public static String getLastConfirmationCode() throws IOException, JSONException {
        String urlString = "http://10.0.2.2:3000/confirmationCode";
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String response = new Scanner(in,"UTF-8").useDelimiter("\\A").next();
            Log.i("TelegramHelper","GET Confirmation-Code: " + urlConnection.getResponseCode());
            JSONObject jsonObject = new JSONObject(response);
            String code = (String) jsonObject.get("code");
            return code;
        } finally{
            urlConnection.disconnect();
        }
    }

    /**
     * Sends a message to a telegram-user (user is specified by the server)
     * @param message
     * @throws IOException
     * @throws JSONException
     */
    public static void sendMessage(String message) throws IOException, JSONException {
        String urlString = "http://10.0.2.2:3000/message";
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConnection.setDoOutput(true);
            urlConnection.connect();

            JSONObject messageJson = new JSONObject();
            messageJson.put("message", message);

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
            outputStreamWriter.write(messageJson.toString());
            outputStreamWriter.flush();
            outputStreamWriter.close();

            Log.i("TelegramHelper","POST Message: " + urlConnection.getResponseCode());
        } finally{
            urlConnection.disconnect();
        }
    }

    /**
     * @return last message (user is specified by the server)
     * @throws IOException
     * @throws JSONException
     */
    public static String getLastMessage() throws IOException, JSONException {
        String urlString = "http://10.0.2.2:3000/lastMessage";
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String response = new Scanner(in,"UTF-8").useDelimiter("\\A").next();
            Log.i("TelegramHelper","GET Last-Message: " + urlConnection.getResponseCode());
            JSONObject jsonObject = new JSONObject(response);
            String message = (String) jsonObject.get("message");
            return message;
        } finally{
            urlConnection.disconnect();
        }
    }

}
