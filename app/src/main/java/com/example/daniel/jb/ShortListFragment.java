package com.example.daniel.jb;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ShortListFragment extends Fragment {
    //**********************************************************************************************************************
    private UserLoginTask mAuthTask = null;
    private String id, pass;

    private String mEmail;
    private String mPassword;
    private String data, loginUrl, Login;
    private View layout;


    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {



        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            DefaultHttpClient httpclient = new DefaultHttpClient();

            HttpGet httpget = new HttpGet("https://jobmine.ccol.uwaterloo.ca/psp/SS/?cmd=login&languageCd=ENG&sessionId=");

            HttpResponse response = null;
            try {
                response = httpclient.execute(httpget);
            } catch (IOException e) {
                e.printStackTrace();
            }
            HttpEntity entity = response.getEntity();

            System.out.println("Login form get: " + response.getStatusLine());
            if (entity != null) {
                try {
                    entity.consumeContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Initial set of cookies:");
            List<Cookie> cookies = httpclient.getCookieStore().getCookies();
            if (cookies.isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                    System.out.println("- " + cookies.get(i).toString());
                }
            }

            HttpPost httpost = new HttpPost("https://jobmine.ccol.uwaterloo.ca/psp/SS/?cmd=login&languageCd=ENG&sessionId=");

            List <NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("httpPort", ""));
            nvps.add(new BasicNameValuePair("timezoneOffset", "240"));
            nvps.add(new BasicNameValuePair("userid", "doshpit"));
            nvps.add(new BasicNameValuePair("pwd", "a5lm7lMM"));
            nvps.add(new BasicNameValuePair("submit", "Submit"));

            try {
                httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                response = httpclient.execute(httpost);
            } catch (IOException e) {
                e.printStackTrace();
            }

            httpost = new HttpPost("https://jobmine.ccol.uwaterloo.ca/psp/SS/EMPLOYEE/WORK/c/UW_CO_STUDENTS.UW_CO_JOB_SLIST.GBL?pslnkid=UW_CO_JOB_SLIST_LINK&FolderPath=PORTAL_ROOT_OBJECT.UW_CO_JOB_SLIST_LINK&IsFolder=false&IgnoreParamTempl=FolderPath%2cIsFolder");
            nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("httpPort", ""));
            nvps.add(new BasicNameValuePair("timezoneOffset", "240"));
            nvps.add(new BasicNameValuePair("userid", "doshpit"));
            nvps.add(new BasicNameValuePair("pwd", "a5lm7lMM"));
            nvps.add(new BasicNameValuePair("submit", "Submit"));

            try {
                httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                response = httpclient.execute(httpost);
            } catch (IOException e) {
                e.printStackTrace();
            }

            entity = response.getEntity();

            System.out.println("Login form post: " + response.getStatusLine());

            if (entity != null) {

                InputStream is = null;
                try {
                    is = entity.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String str ="";
                try {
                    while ((str = br.readLine()) != null){
                        Login+=str+"/n";
                        System.out.println(str);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            System.out.println("Post logon cookies:");
            cookies = httpclient.getCookieStore().getCookies();
            if (cookies.isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                    System.out.println("- " + cookies.get(i).toString());
                }
            }
            httpclient.getConnectionManager().shutdown();

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                System.out.println("Success!");
                layout.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                TextView textView = (TextView) layout.findViewById(R.id.section_short_list);
                textView.setMovementMethod(new ScrollingMovementMethod());
                Boolean found;
                found = Login.contains("AdRoll");
                if (!found) textView.setText(Login);
                else textView.setText(":(");
            }

        }
    }
    //**********************************************************************************************************************
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle args = getArguments();
        id = args.getString("id");
        pass = args.getString("pass");
        layout = inflater.inflate(R.layout.fragment_short_list, container, false);
        mAuthTask = new UserLoginTask(id, pass);
        mAuthTask.execute((Void) null);


        return layout;

    }
}