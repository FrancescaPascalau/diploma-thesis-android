package francesca.pascalau.thesis.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * This is a singleton class used to make REST calls.
 * Has implementations for all the requests/responses types.
 *
 * @author Francesca
 */
public class RequestOperations {
    private static final String TAG = "Request Operations";

    private static RequestOperations instance;
    private RequestQueue requestQueue;
    private Context context;

    //Private Constructor to don't create a new instance of class
    private RequestOperations(Context context) {
        this.context = context;
        this.requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized RequestOperations getInstance(Context ctx) {
        if (instance == null) {
            instance = new RequestOperations(ctx);
        }
        return instance;
    }

    // Requests for String
    public void postRequest(String url) {
        doStringRequest(url, Request.Method.POST);
    }

    public <T> void postRequest(String url, Consumer<T> responseHandler) {
        doStringRequest(url, Request.Method.POST, responseHandler);
    }

    public void getRequest(String url) {
        doStringRequest(url, Request.Method.GET);
    }

    public void putRequest(String url) {
        doStringRequest(url, Request.Method.PUT);
    }

    public void deleteRequest(String url) {
        doStringRequest(url, Request.Method.DELETE);
    }

    // Requests for JsonObject - Without object
    public void postRequestForObject(String url) {
        doJsonObjectRequest(url, null, Request.Method.POST);
    }

    // Requests for JsonObject - With object
    public <T> void postRequestForObject(String url, T t) {
        JSONObject jsonObject = getJsonObject(t);
        doJsonObjectRequest(url, jsonObject, Request.Method.POST);
    }

    public <T> void postRequestForObject(String url, T t, Consumer<String> responseHandler) {
        JSONObject jsonObject = getJsonObject(t);
        doJsonObjectRequest(url, jsonObject, Request.Method.POST, responseHandler);
    }

    public void getRequestForObject(String url) {
        doJsonObjectRequest(url, null, Request.Method.GET);
    }

    public void putRequestForObject(String url) {
        doJsonObjectRequest(url, null, Request.Method.PUT);
    }

    public void deleteRequestForObject(String url) {
        doJsonObjectRequest(url, null, Request.Method.DELETE);
    }

    // Requests for JsonArray
    public <T> void postRequestForArray(String url, ArrayList<T> list) {
        JSONArray array = getJsonArray(list);

        doJsonArrayRequest(url, array, Request.Method.POST);
    }

    public <T> void getRequestForArray(String url, ArrayList<T> list) {
        JSONArray array = getJsonArray(list);

        doJsonArrayRequest(url, array, Request.Method.GET);
    }

    public <T> void putRequestForArray(String url, ArrayList<T> list) {
        JSONArray array = getJsonArray(list);

        doJsonArrayRequest(url, array, Request.Method.PUT);
    }

    public <T> void deleteRequestForArray(String url, ArrayList<T> list) {
        JSONArray array = getJsonArray(list);

        doJsonArrayRequest(url, array, Request.Method.DELETE);
    }

    // For Image type
    public <T> void getRequestForImage(String url, HashMap<String, String> params, Consumer<T> responseHandler) {

        String uri = buildUriWithParams(url, params);
        doImageRequest(uri, responseHandler);
    }

    /**
     * This method builds the uri with necessary parameters to do a request
     *
     * @param url    represents the uri without individual params for each request
     * @param params necessary details about images requests
     * @return the complete uri for an image request
     */
    private String buildUriWithParams(String url, HashMap<String, String> params) {
        StringBuilder uri = new StringBuilder(url);
        uri.append("?");
        params.forEach((key, value) -> uri.append(String.format("%s=%s&", key, value)));
        return uri.toString();
    }

    private <T> JSONArray getJsonArray(ArrayList<T> list) {
        // RequestQueue specifically needs a JSONArray object as the request body
        // Due to this, we need to first make some conversions on the originally
        // received ArrayList.
        JSONArray array = null;
        try {
            String x = new Gson().toJson(list);

            // Default JSONArray constructor can only receive primitive type inputs
            // Because of this, we need to create first a String object
            // and pass that object as argument to the JSONArray constructor
            array = new JSONArray(x);
        } catch (JSONException e) {
            final String message = "Could not convert list to JSON.";
            Log.e(TAG, message);
        }
        return array;
    }

    private <T> JSONObject getJsonObject(T object) {
        // RequestQueue specifically needs a JSONObject object as the request body
        // Due to this, we need to first make some conversions on the originally
        // received <T> object.
        JSONObject jsonObject = null;
        try {
            String x = new Gson().toJson(object);

            // Default JSONObject constructor can only receive primitive type inputs
            // Because of this, we need to create first a String object
            // and pass that object as argument to the JSONObject constructor
            jsonObject = new JSONObject(x);
        } catch (JSONException e) {
            final String message = "Could not convert object to JSON.";
            Log.e(TAG, message);
        }
        return jsonObject;
    }

    private void doJsonArrayRequest(String url, JSONArray array, int method) {
        JsonArrayRequest jsonRequest = new JsonArrayRequest
                (method, url, array,
                        response -> {
                            // TODO: Handle success
                            Log.e(TAG, response.toString());
                        },
                        error -> {
                            // TODO: Handle error
                            Log.e(TAG, error.toString());
                        }
                );
        // Add the request to the RequestQueue.
        requestQueue.add(jsonRequest);
    }

    private void doJsonObjectRequest(String url, JSONObject object, int method) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (method, url, object,
                        response -> {
                            // TODO: Handle success
                            Log.e(TAG, response.toString());
                        },
                        error -> {
                            // TODO: Handle error
                            Log.e(TAG, error.toString());
                        }
                );
        // Add the request to the RequestQueue.
        requestQueue.add(jsonRequest);
    }

    private void doJsonObjectRequest(String url, JSONObject object, int method, Consumer<String> consumer) {
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (method, url, object,
                        response -> {
                            consumer.accept(response.toString());
                            Log.e(TAG, response.toString());
                        },
                        error -> {
                            // TODO: Handle error
                            Log.e(TAG, error.toString());
                        }
                );
        // Add the request to the RequestQueue.
        requestQueue.add(jsonRequest);
    }

    private void doStringRequest(String url, int method) {
        StringRequest stringRequest = new StringRequest
                (method, url,
                        (String response) -> {
                            // TODO: Handle success
                            Log.e(TAG, response);
                        },
                        error -> {
                            // TODO: Handle error
                            Log.e(TAG, "Error detected!");
                        }
                );
        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    private <T> void doStringRequest(String url, int method, Consumer<T> responseHandler) {
        StringRequest jsonRequest = new StringRequest
                (method, url,
                        response -> responseHandler.accept((T) response),
                        error -> {
                            // TODO: Handle error
                            Log.e(TAG, error.toString());
                        }
                );
        // Add the request to the RequestQueue.
        requestQueue.add(jsonRequest);
    }

    /**
     * This method creates a request for an image to a specified url
     * ImageRequest with specified parameters (url, responseHandler, width, height, scaleType, Config, error)
     * The response is of Bitmap type
     *
     * @param url             to do the request
     * @param responseHandler consumer that manages the response of the request
     * @param <T>
     */
    private <T> void doImageRequest(String url, Consumer<T> responseHandler) {
        ImageRequest imageRequest = new ImageRequest(
                url,
                (Bitmap response) -> responseHandler.accept((T) response),
                400,
                400,
                ImageView.ScaleType.CENTER,
                Bitmap.Config.ALPHA_8,
                error -> {
                    //TODO: Handle error
                    Log.e(TAG, error.toString());
                }
        );
        requestQueue.add(imageRequest);
    }
}
