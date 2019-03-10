package francesca.pascalau.thesis.common;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

    public <T> void postRequestWithResponseHandler(String url, Consumer<T> responseHandler) {
        doStringRequestWithResponseHandler(url, Request.Method.POST, responseHandler);
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

    // Requests for JsonObject
    public void postRequestForObject(String url) {
        doJsonObjectRequest(url, null, Request.Method.POST);
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

    private <T> void doStringRequestWithResponseHandler(String url, int method, Consumer<T> responseHandler) {
        StringRequest jsonRequest = new StringRequest
                (method, url,
                        response -> {
                            responseHandler.accept((T) response);
                        },
                        error -> {
                            // TODO: Handle error
                            Log.e(TAG, error.toString());
                        }
                );
        // Add the request to the RequestQueue.
        requestQueue.add(jsonRequest);
    }
}
