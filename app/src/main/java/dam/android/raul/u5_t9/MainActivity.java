package dam.android.raul.u5_t9;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public final static String URL_GEONAMES = "http://api.geonames.org/wikipediaSearchJSON";
    private final static String USER_NAME = "mangarrufo";
    private final static int ROWS = 10;

    private EditText etPlaceName;
    private Button btSearch;
    private ListView lvSearchResult;
    private ArrayList<String> listaSearchResult;
    private GetHttpDataTask getHttpDataTask;

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

        listaSearchResult = new ArrayList<>();
        lvSearchResult = findViewById(R.id.lvSearchResult);

        lvSearchResult.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaSearchResult));
    }

    @Override
    public void onClick(View v) {
        if (isNetworkAvailable()) {
            String place = etPlaceName.getText().toString();

            if (!place.isEmpty()) {
                URL url;
                try {
                    url = new URL(URL_GEONAMES + "?q=" + place + "&maxRows=" + ROWS + "&userName=" + USER_NAME);
                    getHttpDataTask = new GetHttpDataTask(lvSearchResult);
                    getHttpDataTask.execute(url);
                } catch (MalformedURLException e) {
                    Log.i("URL", e.getMessage());
                }
            } else
                Toast.makeText(this, "Write a place to search", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(this, "Sorry,network is not available", Toast.LENGTH_LONG).show();
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

