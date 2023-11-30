package CryptoApiRest;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tools {
    private final static String URL_CRYPTO = "https://min-api.cryptocompare.com";
    private final static String API_KEY_CRYPTO = "cd195bf0f18e581f6fbad73c4a182c670b67c1afd2738ce6267ca2b953ffef21";
    private final static String URL_NOTIFICATION = "https://fcm.googleapis.com/fcm/send";
    private final static String NOTIFICATION_KEY = "key=AAAAn1IkF5Q:APA91bESBoBDm7wKBySX1tcbaIO41MTW6l2Az1GO301ee2t9zBV4TLnNpp5mAY0TTQWNpboZXdwt1dyEefEjOo-7uv35wjhj0GDKjmHPnECruhKynFIRwlD3N9_zMj747YZOwNe_8ZPM";

    final static Logger logger = LoggerFactory.getLogger(Tools.class);

    public static String getUrlCrypto() {
        return URL_CRYPTO;
    }

    public static String getApiKeyCrypto() {
        return API_KEY_CRYPTO;
    }

    public static String getResponse(final String action, final String urlString){
        final StringBuilder content = new StringBuilder();

        try {
            final HttpURLConnection con = (HttpURLConnection) new URL(urlString).openConnection();
            con.setRequestMethod(action);
            con.connect();

            final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            con.disconnect();
        } catch (IOException e) {
            logger.error("CryptoTrade -> "+ e.getMessage());
        }

        return content.toString();
    }

    public static void sendNotification(final String title, final String body, final String userKeyNotification){
        final JSONObject jsonObject = new JSONObject();
        final JSONObject transaction = new JSONObject();

        try {
            transaction.put("title", title);
            transaction.put("body", body);

            jsonObject.put("notification", transaction);
            jsonObject.put("to", userKeyNotification);

            try {
                final HttpURLConnection con = (HttpURLConnection) new URL(URL_NOTIFICATION).openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("Authorization", NOTIFICATION_KEY);

                con.setDoOutput(true);
                OutputStream outStream = con.getOutputStream();
                OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, StandardCharsets.UTF_8);
                outStreamWriter.write(jsonObject.toString());
                outStreamWriter.flush();
                outStreamWriter.close();
                outStream.close();

                (new BufferedReader(new InputStreamReader(con.getInputStream()))).close();

                con.disconnect();
            } catch (IOException e) {
                logger.error("CryptoTrade -> "+ e.getMessage());
            }
        } catch (JSONException e) {
            logger.error("CryptoTrade -> "+ e.getMessage());
        }
    }
}
