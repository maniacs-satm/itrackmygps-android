package com.twormobile.trackble;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends Activity {
    private static final String TAG = "RegisterActivity";

    private GpsLoggerApplication gpsApp;

    private EditText etxtUsername;
    private EditText etxtPassword;
    private EditText etxtPasswordConfirmation;
    private EditText etxtEmail;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        this.gpsApp = (GpsLoggerApplication)getApplication();

        etxtUsername = (EditText)findViewById(R.id.etxt_username);
        etxtPassword = (EditText)findViewById(R.id.etxt_password);
        etxtPasswordConfirmation  = (EditText)findViewById(R.id.etxt_password_confirmation);
        etxtEmail = (EditText)findViewById(R.id.etxt_email);
    }

    public void buttonCancelPressed(View view){
        Intent resultIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, resultIntent);
        super.finish();
    }

    public void buttonSubmitPressed(View view){
        if( isUsernameValid() && isPasswordMatch() && isEmailValid()){
            register();
        }
    }

    private boolean isPasswordMatch(){
        boolean valid = true;
        String password = String.valueOf(etxtPassword.getText());
        String passwordConfirm = String.valueOf(etxtPasswordConfirmation.getText());

        if(password.isEmpty() || passwordConfirm.isEmpty() || password.equals(passwordConfirm) == false){
            valid = false;
        }

        if(!valid){
            String message = getResources().getString(R.string.password_mismatch);
            gpsApp.showDialog("Error", message, this);
        }

        return valid;
    }

    private boolean isUsernameValid(){
        boolean valid = false;
        String value = String.valueOf(etxtUsername.getText());
        if(value != null && value.trim().length() > 0) {
            int length = value.trim().length();
            if(length > 0 && length <= 16) {
                valid = true;
            }
        }

        if(!valid){
            String message = getResources().getString(R.string.invalid_username);
            gpsApp.showDialog("Error", message, this);
        }

        return valid;
    }

    private boolean isEmailValid(){
        boolean valid = false;
        String value = String.valueOf(etxtEmail.getText());
        if(value != null && value.trim().length() > 0) {
            valid = true;
        }

        if(!valid){
            String message = getResources().getString(R.string.invalid_email);
            gpsApp.showDialog("Error", message, this);
        }

        return valid;
    }

    private String getCleanString(EditText etxt) {
        return String.valueOf(etxt.getText()).trim();
    }

    private void register(){
        final String url = gpsApp.REGISTER_URL;

        //curl -i -H "Content-Type applicationjson" -X POST --data
        // 'user[username]=rupert
        // &user[email]=rupert@2rmobile.com
        // &user[password]=junjunmalupet
        // &user[password_confirmation]=junjunmalupet'
        // http://127.0.0.1:3000/api/users.json

        final String username = getCleanString(etxtUsername);
        final String email = getCleanString(etxtEmail);
        final String password = getCleanString(etxtPassword);
        final String passwordConfirmation = getCleanString(etxtPasswordConfirmation);
        final String uuid = gpsApp.getUUID();

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>(){
                    @Override
                    public void onResponse(String response){
                        VolleyLog.v("Response:%n %s", response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.d(TAG, "Error on " + url);
                        VolleyLog.e("Error: ", error.getMessage());
                    }
                })
        {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("uuid", uuid);
                params.put("user[username]", username);
                params.put("user[email]", email);
                params.put("user[password]", password);
                params.put("user[password_confirmation]", passwordConfirmation);

                return params;
            }
        };

        gpsApp.getVolleyRequestQueue().add(postRequest);

    }

}