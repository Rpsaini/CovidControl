package apps.envision.musicvibes;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import fcm.FusedLocationNew;


public class Upload {

    private String UPLOAD_URL = "";
    private int serverResponseCode;
    private Context ct;

    public String uploadVideo(String file, String imageType,  Context ct,String mobile,String uploadurl,String foldername)
    {
        this.ct = ct;
        UPLOAD_URL = uploadurl;
        String lat= FusedLocationNew.mCurrentLocation.getLatitude()+"";
        String lng= FusedLocationNew.mCurrentLocation.getLongitude()+"";


        String fileName = file;
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;

        File sourceFile = new File(file);
        if (!sourceFile.isFile()) {
            Log.e("Huzza", "Source File Does not exist");
            return null;
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(UPLOAD_URL);
            System.out.println("Uploaded_url" + url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty(foldername+"", fileName + "?" + imageType + "?" + mobile+"?"+lat+"?"+lng);


            System.out.println("fil name==="+fileName+"=="+imageType+"==="+mobile+"=="+lat+"=="+lng);

            dos = new DataOutputStream(conn.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + lineEnd);
//            dos.writeBytes("Content-Disposition: form-data; name=\"selfie\"; filename=\"" + fileName + "?" + imageType + "?" + mobile +"?" + lat + "?" + lng+" \"" + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name="+foldername+"; filename=\"" + fileName + "?" + imageType + "?" + mobile +"?" + lat + "?" + lng+" \"" + lineEnd);

            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            serverResponseCode = conn.getResponseCode();
            System.out.println("serverResponseCode===" + serverResponseCode);
            fileInputStream.close();
            dos.flush();
            dos.close();


        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (serverResponseCode == 200) {
            StringBuilder sb = new StringBuilder();
            try {
                BufferedReader rd = new BufferedReader(new InputStreamReader(conn
                        .getInputStream()));
                String line;
                while ((line = rd.readLine()) != null) {
                    sb.append(line);
                }
                rd.close();
            } catch (IOException ioex) {
            }
            return sb.toString();
        } else {
            return serverResponseCode + "";
        }
    }

//
}