package info.pnddch.meetingmanagement.utilities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import info.pnddch.meetingmanagement.utilInterface.CMResponse;

public class CommunicationManager {
    Activity activity = null;
//        String host = "http://192.168.8.101:52";
    String host = "http://pnddch.info";

    class C05321 implements Listener<String> {
        C05321() {
        }

        public void onResponse(String response) {
            try {
                ((CMResponse) CommunicationManager.this.activity).consumeResponse(response, true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class C05332 implements ErrorListener {
        C05332() {
        }

        public void onErrorResponse(VolleyError error) {
            try {
                ((CMResponse) CommunicationManager.this.activity).consumeResponse(error.getMessage(), false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Toast.makeText(CommunicationManager.this.activity, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    class C05343 implements RetryPolicy {
        C05343() {
        }

        public int getCurrentTimeout() {
            return 50000;
        }

        public int getCurrentRetryCount() {
            return 50000;
        }

        public void retry(VolleyError error) throws VolleyError {
        }
    }

    class C05377 implements RetryPolicy {
        C05377() {
        }

        public int getCurrentTimeout() {
            return 50000;
        }

        public int getCurrentRetryCount() {
            return 50000;
        }

        public void retry(VolleyError error) throws VolleyError {
        }
    }

    public CommunicationManager(Activity c) {
        this.activity = c;
    }

    public void getRequest(String url) {
        RequestQueue queue = Volley.newRequestQueue(this.activity.getApplicationContext());
        String hostURL = getHostURL(this.activity);
        StringRequest stringRequest = new StringRequest(0, "http://pnddch.info/mm" + "/" + url, new C05321(), new C05332());
        stringRequest.setRetryPolicy(new C05343());
        queue.add(stringRequest);
    }

    public void postRequest(String url, JSONObject parameters, final CMResponse responseInterface) {
        final ProgressDialog progressDialog = new ProgressDialog(this.activity);
        progressDialog.setMessage("Working, Please Wait...");
        progressDialog.setProgressStyle(0);
        progressDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this.activity.getApplicationContext());
        String hostURL = getHostURL(this.activity);
        final JSONObject jSONObject = parameters;
        StringRequest stringRequest = new StringRequest(1, host + "/mm" + "/" + url, new Listener<String>() {
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    responseInterface.consumeResponse(response, true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                try {
                    responseInterface.consumeResponse(error.getMessage(), false);
                    Toast.makeText(CommunicationManager.this.activity, error.getMessage(), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                Iterator<String> iter = jSONObject.keys();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    try {
                        if (jSONObject.get(key) instanceof Boolean) {
                            params.put(key, String.valueOf(jSONObject.get(key)));
                        } else {
                            params.put(key, (String) jSONObject.get(key));
                        }

                    } catch (JSONException e) {
                    }
                }
                return params;
            }

            public Map<String, String> getHeaders() throws AuthFailureError {
                return new HashMap();
            }
        };
        stringRequest.setRetryPolicy(new C05377());
        queue.add(stringRequest);
    }

    public void postRequestScheduleWithoutDialogue(String url, JSONObject parameters, final CMResponse responseInterface) {
        RequestQueue queue = Volley.newRequestQueue(this.activity.getApplicationContext());
        String hostURL = getHostURL(this.activity);
        final JSONObject jSONObject = parameters;
        StringRequest stringRequest = new StringRequest(1, host + "/mm" + "/" + url, new Listener<String>() {
            public void onResponse(String response) {
                try {
                    responseInterface.consumeResponse(response, true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                try {
                    responseInterface.consumeResponse(error.getMessage(), false);
                    Toast.makeText(CommunicationManager.this.activity, error.getMessage(), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap();
                Iterator<String> iter = jSONObject.keys();
                while (iter.hasNext()) {
                    String key = (String) iter.next();
                    try {
                        if (jSONObject.get(key) instanceof Boolean) {
                            params.put(key, String.valueOf(jSONObject.get(key)));
                        } else {
                            params.put(key, (String) jSONObject.get(key));
                        }

                    } catch (JSONException e) {
                    }
                }
                return params;
            }

            public Map<String, String> getHeaders() throws AuthFailureError {
                return new HashMap();
            }
        };
        stringRequest.setRetryPolicy(new C05377());
        queue.add(stringRequest);
    }

    private String getHostURL(Activity activity) {
        String url = "";
        try {
//            JSONArray rs = new DatabaseManager(activity).selectResultFromDB("select host_name , project_name from tbl_settings");
//            for (int j = 0; j < rs.length(); j++) {
//                JSONObject row = new JSONObject(rs.getString(j));
//                String hostname = row.getString("host_name");
//                url = "http://" + hostname + "/" + row.getString("project_name") + "/";
//            }
            return url;
        } catch (Exception ex) {
            return ex.getLocalizedMessage();
        }
    }
}
