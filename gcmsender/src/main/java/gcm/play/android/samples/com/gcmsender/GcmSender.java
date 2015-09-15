package gcm.play.android.samples.com.gcmsender;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GcmSender {

    public static final String API_KEY = "AIzaSyCI10sbdlKv-EbJO0r7Ai1YxD-_oWV0B0U";

    public static void main(String[] args) {
        try {
            // Prepare JSON containing the GCM message content. What to send and where to send.

            JSONObject jGcmData = new JSONObject();
            JSONObject jData = new JSONObject();
            jData.put("message", args[0].trim());
            // Where to send GCM message.
            if (args.length > 1 && args[1] != null) {
                jGcmData.put("to", args[1].trim());
            } else {
                //jGcmData.put("to", "/topics/global");
                jGcmData.put("to", "c-L1nv5QoJs:APA91bFlJ75ECxFqiK1LB49glMEJ21bVf1byYWF_y_RBBZmS_T0uxD8SmqsI7mpiVTzkMDClXLBGVuGS4BvGJrdZBnVdY9Ypn14uqmic2Q_m75oR3dlafzjGQFVs6CPCWac3qiDQIlmL");
            }
            // What to send in GCM message.
            jGcmData.put("data", jData);

            //String str = "{\"data\":{\"message\": \"Lassssstest with response\"}, \"registration_ids\": [\"e6uVQPjNIUg:APA91bGxp51RTz83HDEWqvdRW_Q-JUVOgOhOuAULhsV-eXf8I7eefy2Svrj5L3yTjfEiGlt5SVaOx_MXmEcilrfmPSYtBTeSris9KI-fKr2HJA4Dl_2Bmcd1ZNpsKoMUzAK3b9Wgvx8X\"]}";

            // Create connection to send GCM Message request.
            URL url = new URL("https://android.googleapis.com/gcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Authorization", "key=" + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            System.out.println(jGcmData);
            //System.out.println(str);

            // Send GCM message content.
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jGcmData.toString().getBytes());
            //outputStream.write(str.getBytes());

            // Read GCM response.
            InputStream inputStream = conn.getInputStream();
            String resp = IOUtils.toString(inputStream);
            System.out.println(resp);
            System.out.println("Check your device/emulator for notification or logcat for " +
                    "confirmation of the receipt of the GCM message.");
        } catch (IOException e) {
            System.out.println("Unable to send GCM message.");
            System.out.println("Please ensure that API_KEY has been replaced by the server " +
                    "API key, and that the device's registration token is correct (if specified).");
            e.printStackTrace();
        }
    }

}
