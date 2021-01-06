package edu.sysu.citystorymap.utils;

import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_OK;

public class HttpUtils {
    private final static String PATH = "http://nelson-cheung.cn:8080/CityStoryMapServer_v1";
    private static HttpURLConnection connection;

    private static void openConnection(String path) {
        try {
            URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(1000);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Accept-Charset", "utf-8");
            connection.setUseCaches(false);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static JSONObject post(String dst, JSONObject param) {
        try {
            String path = PATH + "/" + dst;
            openConnection(path);

            String jsonString = HttpUtils.httpBase64Encode(param.toString());
            OutputStream out = connection.getOutputStream();
            out.write(jsonString.getBytes());

            int res = connection.getResponseCode();

            System.out.println("Http Code:" + res);

            if (res == HTTP_OK) {
                InputStream in = connection.getInputStream();
                jsonString = StreamUtils.inputStreamToString(in);
                if (jsonString.isEmpty()) {
                    return new JSONObject();
                } else {
                    jsonString = httpBase64Decode(jsonString);
                    JSONObject jsonObject = JSONObject.fromObject(jsonString);
                    return jsonObject;
                }
            } else {
                return new JSONObject();
            }

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return new JSONObject();
    }

    /*
    public static void post(String serverPath, HashMap<String, Object> params, HttpResult result) {
        String path = PATH + "/" + serverPath;
        JSONObject jsonObject = new JSONObject();

        try {
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
            openConnection(path);

            String jsonString = jsonObject.toString();
            Base64 base64 = new Base64();
            jsonString = base64.encodeToString(jsonString.getBytes());

            connection.setRequestProperty("Content-Length", String.valueOf(jsonString.getBytes().length));


            OutputStream out = connection.getOutputStream();
            out.write(jsonString.getBytes());
            out.flush();

            int res = connection.getResponseCode();

            System.out.println("Http Code:"+res);

            if (res == HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                String content = StreamUtils.inputStreamToString(inputStream);
                result.onSuccess(content);
            } else {
                result.onError();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void post(String serverPath, String params, RequestResult result) {
        try {
            String path = PATH + "/" + serverPath;
            openConnection(path);

            String temp = HttpUtils.httpBase64Encode(params);
            OutputStream out = connection.getOutputStream();
            out.write(temp.getBytes());

            int res = connection.getResponseCode();
            System.out.println("Http Code:"+res);
            if (res == HTTP_OK) {
                InputStream in = connection.getInputStream();
                String jsonString = StreamUtils.inputStreamToString(in);
                jsonString = httpBase64Decode(jsonString);

                if (jsonString.isEmpty()) {
                    result.onError();
                }

                JSONArray array = new JSONArray(jsonString);
                ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
                JSONObject jsonObject;

                for (int i = 0; i < array.length(); ++i) {
                    jsonObject = (JSONObject) array.get(i);
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    Iterator<String> iterator = jsonObject.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next();
                        map.put(key, jsonObject.get(key));
                    }
                    list.add(map);
                }

                result.onSuccess(list);

            } else {
                result.onError();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    */
    public static String httpBase64Decode(String content) {
        try {
            content = content.replace(' ', '+');
            byte[] temp = content.getBytes("utf-8");
            Base64 base64 = new Base64();
            temp = base64.decode(temp);
            return new String(temp, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String httpBase64Encode(String content) {
        try {
            byte[] temp = content.getBytes("utf-8");
            Base64 base64 = new Base64();
            return base64.encodeToString(temp);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "";
    }
 /*   public interface HttpResult {
        void onSuccess(String response);
        void onSuccess(int response);
        void onError();
    }

    public interface RequestResult {
        void onSuccess(List<HashMap<String, Object>> result);
        void onSuccess(int response);
        void onError();
    }*/

}
