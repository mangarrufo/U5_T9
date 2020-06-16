package dam.android.raul.u5_t9;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public final static String URL_GEONAMES = "http://api.geonames.org/wikipediaSearchJSON";
    public final static String URL_WHEATHER = "http://api.openweathermap.org/data/2.5/weather";
    private final static String USER_ID = " ";
    private final static String USER_NAME = "mangarrufo";
    private final static int ROWS = 10;
    private final static String LANGUAGE = "ES";
    private geonamesPlace geonamesPlace;

    private EditText etPlaceName;
    private Button btSearch;
    private ListView lvSearchResult;
    private ArrayList<geonamesPlace> listaSearchResult;
    private GetHttpDataTask getHttpDataTask;
    private GetHttpWheaterTask getHttpWheaterTask;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUI();
    }

    private void setUI() {
        etPlaceName = findViewById(R.id.etPlaceName);
        btSearch = findViewById(R.id.btSearch);
        btSearch.setOnClickListener(this);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        listaSearchResult = new ArrayList<>();
        lvSearchResult = findViewById(R.id.lvSearchResult);
        lvSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                geonamesPlace = listaSearchResult.get(i);

                URL url;
                try {
                    url = new URL(URL_WHEATHER + "?lat=" + geonamesPlace.getLat() + "&lon=" + geonamesPlace.getLng() + "&appid=" + USER_ID + "&units=metric");
                    getHttpWheaterTask = new GetHttpWheaterTask(lvSearchResult, progressBar);
                    getHttpWheaterTask.execute(url);
                } catch (MalformedURLException e) {
                    Log.i("URL", e.getMessage());
                }
            }
        });

        lvSearchResult.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaSearchResult));
    }

    @Override
    public void onClick(View v) {
        if (isNetworkAvailable()) {
            String place = etPlaceName.getText().toString();

            if (!place.isEmpty()) {
                URL url;
                try {
                    url = new URL(URL_GEONAMES + "?q=" + place + "&maxRows=" + ROWS + "&userName=" + USER_NAME + "&lang=" + LANGUAGE);
                    getHttpDataTask = new GetHttpDataTask(lvSearchResult, progressBar);
                    getHttpDataTask.execute(url);
                    hideKeyboard(this);
                } catch (MalformedURLException e) {
                    Log.i("URL", e.getMessage());
                }
            } else
                Toast.makeText(this, "Write a place to search", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(this, "Sorry,network is not availabel", Toast.LENGTH_LONG).show();
    }

    public boolean isNetworkAvailable() {
        boolean newtworkAvailable = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                newtworkAvailable = true;
            }

        }
        return newtworkAvailable;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void selected() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (getHttpDataTask != null) {
            getHttpDataTask.cancel(true);
            Log.e("onDestroy()", "ASYNCTASK was Canceled");
        } else
            Log.e("onDestroy()", "ASYNCTASK = NULL,was not canceled");
    }
}
