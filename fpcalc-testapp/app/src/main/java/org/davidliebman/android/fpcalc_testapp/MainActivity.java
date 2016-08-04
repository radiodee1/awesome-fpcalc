package org.davidliebman.android.fpcalc_testapp;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.nononsenseapps.filepicker.FilePickerActivity;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;

public class MainActivity extends AppCompatActivity {

    /* * *
    * NOTE:
    * For this project to work you must register the project at the website below. Then you
    * must take the API-KEY that you receive on the site and insert it in the spot that says
    * 'YOUR_API_KEY' The program will work for a few days with the api key that is provided
    * in this code, but after a few days it will not work for you again.
    *
    *   https://acoustid.org/
    *
    * */


    public static final String YOUR_API_KEY = "ousigAs5JAM";//

    public static final int RC_PICKMUSIC = 1;

    public static final int BUTTON_TYPE_PICKER = 1;
    public static final int BUTTON_TYPE_SERVER_CONNECT = 2;

    public static final String CONST_FINGERPRINT = "FINGERPRINT=";
    public static final String CONST_DURATION = "DURATION=";
    public static final String CONST_API_KEY = YOUR_API_KEY;
    public static final String CONST_URL = "https://api.acoustid.org/v2/lookup";
    public static final String CONST_PARAM_FINDGERPRINT = "fingerprint";
    public static final String CONST_PARAM_CLIENT = "client";
    public static final String CONST_PARAM_DURATION = "duration";
    public static final String CONST_PARAM_META = "meta";
    public static final String CONST_PARAM_META_VALUE = "recordings releasegroups compress";

    public static final String URL_CONST_GOOGLE_PREFIX = "http://www.google.com/search?q=";
    public static final String URL_CONST_GOOGLE_SUFFIX = "";

    public static final String URL_CONST_BRAINZ_PREFIX = "http://musicbrainz.org/recording/";
    public static final String URL_CONST_BRAINZ_SUFFIX = "";

    String [] allowed = {"mp3","ogg","wav"};

    String msg = "";
    int duration = 0;

    String msgResend = "";
    int durationResend = 0;

    String mJsonIn = "";
    String mJsonArtist = "";
    String mJsonAlbum = "";
    String mJsonTitle = "";
    String mJsonId = "";

    TextView mScreenMsg = null;
    Button mScreenBtnPicker = null;
    Button mScreenBtnRetry = null;
    Button mScreenBtnGoogle = null;
    Button mScreenBtnBrainz = null;

    int mButtonType = 0;

    Context mContext = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this.getApplicationContext();

        mScreenMsg = (TextView) findViewById(R.id.hello_text);
        mScreenMsg.setText(msg);

        mScreenBtnPicker = (Button) findViewById(R.id.start_button);
        mScreenBtnRetry = (Button) findViewById(R.id.retry_button);
        mScreenBtnBrainz = (Button) findViewById(R.id.musicbrainz_button);
        mScreenBtnGoogle = (Button) findViewById(R.id.lyric_button);

        mButtonType = BUTTON_TYPE_PICKER;
        mScreenBtnPicker.setText("PICKER");
        mScreenBtnRetry.setVisibility(View.GONE);
        mScreenBtnGoogle.setVisibility(View.GONE);
        mScreenBtnBrainz.setVisibility(View.GONE);

        mScreenBtnPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mButtonType == BUTTON_TYPE_PICKER) {

                    mScreenBtnBrainz.setVisibility(View.GONE);
                    mScreenBtnGoogle.setVisibility(View.GONE);

                    mScreenMsg.setText("");
                    mScreenBtnRetry.setVisibility(View.GONE);

                    mJsonTitle = "";
                    mJsonArtist = "";
                    mJsonAlbum = "";

                    Intent i = new Intent(mContext, FilePickerActivity.class);

                    i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
                    i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
                    i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

                    i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

                    startActivityForResult(i, RC_PICKMUSIC);
                } else if (mButtonType == BUTTON_TYPE_SERVER_CONNECT) {

                    //sendHttpGetPost(msg,duration);
                    msgResend = msg;
                    durationResend = duration;

                    new DoConnect().execute(msg, String.valueOf(duration));

                    /////////// at end of code ////////
                    mButtonType = BUTTON_TYPE_PICKER;
                    mScreenBtnPicker.setText("PICKER");


                }

            }
        });

        mScreenBtnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DoConnect().execute(msgResend, String.valueOf(durationResend));

            }
        });

        mScreenBtnBrainz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String mUrl =  mJsonId;
                    launchWebBrowser(
                                    URL_CONST_BRAINZ_PREFIX +
                                    URLEncoder.encode(mUrl, "UTF-8") +
                                    URL_CONST_BRAINZ_SUFFIX);

                } catch (Exception e) {e.printStackTrace();}
            }
        });

        mScreenBtnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String mUrl = //"lyrics " +
                            mJsonArtist +" lyrics " +
                            mJsonTitle ;
                            //mScreenMsg.getText().toString();
                    launchWebBrowser(
                            URL_CONST_GOOGLE_PREFIX +
                            URLEncoder.encode(mUrl, "UTF-8") +
                            URL_CONST_GOOGLE_SUFFIX);

                } catch (Exception e) {e.printStackTrace();}
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_PICKMUSIC && resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
                // For JellyBean and above
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();

                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            Uri uri = clip.getItemAt(i).getUri();
                            // Do something with the URI
                            //msg = clip.getItemAt(i).toString();
                        }
                    }
                    // For Ice Cream Sandwich
                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra(FilePickerActivity.EXTRA_PATHS);

                    if (paths != null) {
                        for (String path : paths) {
                            Uri uri = Uri.parse(path);
                            // Do something with the URI
                            //msg = path;
                        }
                    }
                }

            } else {
                Uri uri = data.getData();
                // Do something with the URI
                mButtonType = BUTTON_TYPE_SERVER_CONNECT;
                mScreenBtnPicker.setText("CONNECT");
                //test_fpcalc(uri.getPath());
                boolean ok = false;
                for ( int i = 0; i < allowed.length; i ++) {
                    if (uri.getPath().toLowerCase().endsWith("." + allowed[i].toLowerCase())) ok = true;
                }
                if (ok) {

                    new DoFingerprint().execute(uri.getPath());
                }
                else {
                    Toast.makeText(mContext,"bad file name",Toast.LENGTH_LONG).show();
                    mButtonType = BUTTON_TYPE_PICKER;
                    mScreenBtnPicker.setText("PICKER");
                }
            }
        }
    }

    public void launchWebBrowser(String url) {
        Intent launch = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(launch);
        Toast.makeText(this, url, Toast.LENGTH_LONG).show();
    }

    public void sendHttpPost(String in, int dur) {

        try {

            String params = URLEncoder.encode(CONST_PARAM_CLIENT, "UTF-8")
                    + "=" + URLEncoder.encode(CONST_API_KEY, "UTF-8");

            params += "&" + URLEncoder.encode(CONST_PARAM_FINDGERPRINT, "UTF-8")
                    + "=" + URLEncoder.encode(in, "UTF-8");

            params += "&" + URLEncoder.encode(CONST_PARAM_DURATION, "UTF-8")
                    + "=" + URLEncoder.encode(String.valueOf(dur), "UTF-8");

            params += "&" + URLEncoder.encode(CONST_PARAM_META, "UTF-8")
                    + "=" + URLEncoder.encode(CONST_PARAM_META_VALUE, "UTF-8");

            /////////////included for development //////////////////
            /*
            String simple = (CONST_PARAM_CLIENT)
                    + "=" + (CONST_API_KEY);

            simple += "&" + (CONST_PARAM_FINDGERPRINT)
                    + "=" + (in);

            simple += "&" + (CONST_PARAM_DURATION)
                    + "=" + (String.valueOf(dur));

            simple += "&" + (CONST_PARAM_META)
                    + "=" + (CONST_PARAM_META_VALUE);
            */
            ////////////////////////////////////////////////////////

            byte [] bytes = compress(params);

            String hostname = "api.acoustid.org";
            int port = 80; //80

            InetAddress addr = InetAddress.getByName(hostname);
            Socket socket = new Socket(addr, port);
            String path = "/v2/lookup";

            // Send headers
            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os); // true???

            pw.write("POST "+path+" HTTP/1.1\r\n"); //POST 1.0
            pw.write("Host: " + hostname + "\r\n");
            pw.write("Content-Length: "+bytes.length+"\r\n");

            pw.write("Content-Type: application/x-www-form-urlencoded\r\n");

            pw.write("Content-Encoding: gzip\r\n");
            pw.write("Connection: close\r\n");
            pw.write("\r\n");


            //System.out.println("len " + params.length() + " " + bytes.length );
            // Send parameters

            pw.flush();

            os.write(bytes);

            os.flush();

            // Get response
            BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line;

            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null) {
                System.out.println(line);
                response.append(line);
                response.append("\r");
                if (line.startsWith("{")) mJsonIn = line;
            }

            pw.close();
            rd.close();

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }





    public static byte[] compress(String s) throws IOException {
        ByteArrayOutputStream byteOut=new ByteArrayOutputStream();
        GZIPOutputStream gZipOut=new GZIPOutputStream(byteOut);
        gZipOut.write(s.getBytes());
        gZipOut.finish();
        byteOut.close();
        return byteOut.toByteArray();
    }


    public void parseJSON(String in) {

        try {

            //System.out.println("input " + in);

            if (in.length() == 0) return;

            JSONObject reader = new JSONObject(in);

            JSONArray results = reader.getJSONArray("results");

            //System.out.println(results.length());

            if (results.length() == 0) {
                System.out.println("no results");
                return;
            }

            JSONObject list = results.getJSONObject(0);

            JSONArray recordings = list.getJSONArray("recordings");

            int ii = (recordings.length() > 0 ? 1: 0);
            for (int i = 0; i < ii; i++) {
                JSONObject sub = recordings.getJSONObject(i);
                JSONArray names = sub.names();
                //System.out.println(i + " -- list " + names.toString());
                //System.out.println(i + " -- " + sub.toString());

                int k = 0;
                for (int j = 0; j < names.length(); j++) {
                    if (names.getString(j).contentEquals("releasegroups")) k++;
                    if (names.getString(j).contentEquals("artists")) k++;
                    if (names.getString(j).contentEquals("title")) k++;
                }
                if (k < 3) return;

                JSONArray releasegroups = sub.getJSONArray("releasegroups");
                JSONObject subrelease = releasegroups.getJSONObject(0);
                String mAlbumName = subrelease.getString("title");
                //System.out.println("album " + mAlbumName);


                JSONArray artists = sub.getJSONArray("artists");
                String mArtists = artists.getJSONObject(0).getString("name");
                //System.out.println(mArtists);

                String mSong = sub.getString("title");
                //System.out.println(mSong);

                String mId = sub.getString("id");

                mJsonTitle = mSong;
                mJsonArtist = mArtists;
                mJsonAlbum = mAlbumName;
                mJsonId = mId;

                // the last listing will be displayed.

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void test_fpcalc(String in) {

        System.out.println("my url: " + in);

        try {
            System.loadLibrary("fpcalc");
        } catch (UnsatisfiedLinkError e) {
            System.out.print("Could not load library libfpcalc.so : " + e);
            return;
        }

        String[] args = {in};// {"-version"};// { in};
        String result = fpCalc(args);
        System.out.print("result = " + result + "\n");

        String[] parts = result.split("\n");
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].startsWith(CONST_FINGERPRINT))
                result = parts[i].substring(CONST_FINGERPRINT.length());
            if (parts[i].startsWith(CONST_DURATION))
                duration = Integer.parseInt(parts[i].substring(CONST_DURATION.length()));
        }

        // parse the result string, for example, for “FINGERPRINT=XXXXX\n”
        msg = result;
        //mScreenMsg.setText(msg + "  --  " + duration);


    }

    public native String fpCalc(String[] args);


    class DoConnect extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {

            //sendHttpGetPost(params[0], Integer.parseInt(params[1]));
            sendHttpPost(params[0], Integer.parseInt(params[1]));

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mScreenMsg.setText(mJsonIn);
            parseJSON(mJsonIn);

            if (mJsonTitle.contentEquals("")) {
                msg = "No match now.";
                mScreenBtnRetry.setVisibility(View.VISIBLE);

            } else {
                msg = "album: " + mJsonAlbum + "\n" + "artist: " + mJsonArtist + "\n" + "song: " + mJsonTitle;
                mScreenBtnRetry.setVisibility(View.GONE);

                mScreenBtnBrainz.setVisibility(View.VISIBLE);
                mScreenBtnGoogle.setVisibility(View.VISIBLE);

            }
            mScreenMsg.setText(msg);

            mScreenBtnPicker.setEnabled(true);
            mScreenBtnRetry.setEnabled(true);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mScreenBtnPicker.setEnabled(false);
            mScreenBtnRetry.setEnabled(false);
        }
    }

    class DoFingerprint extends AsyncTask<String , String ,String > {

        @Override
        protected String doInBackground(String... params) {

            test_fpcalc(params[0]);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mScreenBtnPicker.setEnabled(false);
            mScreenBtnRetry.setEnabled(false);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mScreenMsg.setText(msg.substring(0,100) + "  --  " + duration);

            mScreenBtnPicker.setEnabled(true);
            mScreenBtnRetry.setEnabled(true);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }
}
