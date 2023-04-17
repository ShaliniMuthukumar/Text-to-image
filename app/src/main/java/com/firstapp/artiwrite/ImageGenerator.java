package com.firstapp.artiwrite;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public class ImageGenerator {
    private final String url = "https://stablediffusionapi.com/api/v3/text2img";

    private final Context context;

    public ImageGenerator(Context context){
        this.context=context;
    }
    public void generate(String prompt, int width, int height,int count, OnLoaded onLoaded){
        JSONObject js = new JSONObject();
        ArrayList<String> arrayList = new ArrayList<>();

        try{
            String key ="Zef4MGOhhNNi7S6N4g6NTLcT5fRBq2CR1EwOmwOJ4PxurEzKdXGy3uc8y5UQ";

            js.put("key",key);
            js.put("prompt",prompt);
            js.put("samples" ,count);
            js.put("width",width);
            js.put("height",height);
        }catch (JSONException e){
            e.printStackTrace();
        }


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, js, new Response.Listener<JSONObject>(){
         @Override
            public void onResponse(JSONObject response){
             if (response != null) {
                 JSONArray dataArray;
                 try {
                     dataArray = response.getJSONArray("output");
//                     ArrayList<String> arrayList = null;
                     for (int i = 0; i < count; i++) {
                         arrayList.add(dataArray.getString(i));
                     }
                     onLoaded.loaded(arrayList);
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
             }


         }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error){

                Toast.makeText(context, "There was a error while getting images", Toast.LENGTH_SHORT).show();


            }
        }){
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-type", "application/json");
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(request);
    }
}
