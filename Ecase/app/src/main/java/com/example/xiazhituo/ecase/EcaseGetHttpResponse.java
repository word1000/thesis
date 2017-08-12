package com.example.xiazhituo.ecase;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xiazhituo on 2016/12/22.
 */

public class EcaseGetHttpResponse {
    EcaseGetHttpResponse() {

    }

    public String getHttpResponse(String strUrl) {
        try {
            URL url = new URL(strUrl.trim());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(10000);

            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200) {
                InputStream is = httpURLConnection.getInputStream();
                String responseString = inputStream2String(is);

                return responseString;

            } else {
                System.out.println(" GET something wrong ");

                return null;
            }
        }catch (IOException e) {
            e.printStackTrace();
            System.out.println("What the Fuck!");
            return null;
        }
    }

    public String getPostHttpResponse(String strUrl, String jsonStr) {
        try {
            URL url = new URL(strUrl.trim());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(10000);

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(jsonStr);
            wr.flush();
            wr.close();

            int responseCode = httpURLConnection.getResponseCode();
            if (responseCode == 200) {
                InputStream is = httpURLConnection.getInputStream();
                String responseString = inputStream2String(is);

                return responseString;

            } else {
                System.out.println(" GET something wrong ");

                return null;
            }
        }catch (IOException e) {
            e.printStackTrace();
            System.out.println("What the Fuck!");
            return null;
        }
    }

    public String inputStream2String(InputStream is) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = in.readLine()) != null){
            buffer.append(line);
        }
        return buffer.toString();
    }
}
