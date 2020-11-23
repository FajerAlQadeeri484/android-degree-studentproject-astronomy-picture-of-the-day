package com.barmej.apod;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.ortiz.touchview.TouchImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private FragmentManager mFragmentManager;
    private TextView title;
    private TextView explanation;
    private TouchImageView image;
    private WebView video;
    private ProgressBar progressBar;
    private LinearLayout bottomSheet;
    private SimpleDateFormat simpleDateFormat;
    private final static String url = "https://api.nasa.gov/planetary/apod?api_key=vYS7IxXAwb0eEcRllj0ay7VqdraUdDMAUFMvsj0z";
    private String downloadUrl;
    private MenuItem downloadMenuItem;
    AstronomyData astronomyData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.titlee);
        explanation = findViewById(R.id.explanationn);
        image = findViewById(R.id.img_picture_view);
        video = findViewById(R.id.wv_video_player);
        video.getSettings().setJavaScriptEnabled(true);
        video.setWebViewClient(new WebViewClient());
        video.setWebChromeClient(new WebChromeClient());
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        bottomSheet = findViewById(R.id.bottom_sheet);

        /*if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            bottomSheet.setVisibility(View.INVISIBLE);
            getSupportActionBar().hide();
        }*/

        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateString = simpleDateFormat.format(new Date());

        invalidateOptionsMenu();

        if (savedInstanceState != null) {
            astronomyData = savedInstanceState.getParcelable("apod");
            showData();
        } else {
            requestAPOD(dateString);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        downloadMenuItem = menu.findItem(R.id.action_download_hd);
        if (astronomyData != null) {
            if (astronomyData.getMediaType().equalsIgnoreCase("image")) {
                downloadMenuItem.setVisible(true);
            } else {
                downloadMenuItem.setVisible(false);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_pick_day:
                showDatePickerDialog();
                return true;
            case R.id.action_download_hd:
                download();
                return true;
            case R.id.action_share:
                BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                String imagePath = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "image.jpg", null);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                if (astronomyData.getMediaType().equalsIgnoreCase("image")) {
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(imagePath));
                    intent.putExtra(Intent.EXTRA_TEXT, astronomyData.getTitle());
                } else {
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, astronomyData.getTitle() + "" + astronomyData.getUrl());
                }
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
                return true;
            case R.id.action_about:
                AboutFragment aboutFragment = new AboutFragment();
                mFragmentManager = getSupportFragmentManager();
                aboutFragment.show(mFragmentManager, aboutFragment.getTag());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void download() {
        // Create request for android download manager
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

        // set title and description
        request.setTitle("Data Download");
        request.setDescription("Android Data download using DownloadManager.");

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        //set the local destination for download file to a path within the application's external files directory
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "downloadfileName");
        request.setMimeType("image/*");
        downloadManager.enqueue(request);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.YEAR, year);
        String dateString = simpleDateFormat.format(calendar.getTime());
        requestAPOD(dateString);
    }

    private void requestAPOD(String date) {
        String finalUrl = url + "&date=" + date;
        RequestQueue mQueue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, finalUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            astronomyData = new AstronomyData(response);
                            showData();
                            if (astronomyData.getMediaType().equalsIgnoreCase("image")) {
                                downloadMenuItem.setVisible(true);
                            } else {
                                downloadMenuItem.setVisible(false);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //error.printStackTrace();
                VolleyLog.d("Error", error.getMessage());
            }
        });
        mQueue.add(request);
    }

    private void showData() {
        title.setText(astronomyData.getTitle());
        explanation.setText(astronomyData.getExplanation());
        if (astronomyData.getMediaType().equalsIgnoreCase("image")) {
            video.setVisibility(View.GONE);
            image.setVisibility(View.VISIBLE);
            downloadUrl = astronomyData.getUrl();
            Glide.with(MainActivity.this).load(astronomyData.getUrl()).into(image);
        } else {
            video.setVisibility(View.VISIBLE);
            image.setVisibility(View.GONE);
            video.loadUrl(astronomyData.getUrl());
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable("apod", astronomyData);
        super.onSaveInstanceState(outState);

    }

    ///// To Enable fullscreen mode /////

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (hasFocus) {
                bottomSheet.setVisibility(View.INVISIBLE);
                hideSystemUI();
            }
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
// except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

}
