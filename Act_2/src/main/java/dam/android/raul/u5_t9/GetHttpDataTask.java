package dam.android.raul.u5_t9;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static dam.android.raul.u5_t9.MainActivity.URL_GEONAMES;

public class GetHttpDataTask extends AsyncTask<URL, Integer, ArrayList<geonamesPlace>> {

    private final int CONNECTION_TIMEOUT = 15000;
    private final int READ_TIMEOUT = 10000;

    private final WeakReference<ListView> listViewWeakReference;
    private ProgressBar progressBar;

    public GetHttpDataTask(ListView listView, ProgressBar progressBar) {
        this.listViewWeakReference = new WeakReference<>(listView);
        this.progressBar = progressBar;
    }

    @Override
    protected ArrayList<geonamesPlace> doInBackground(URL... urls) {
        HttpURLConnection urlConnection = null;
        ArrayList<geonamesPlace> serchResult = new ArrayList<>();

        try {
            urlConnection = (HttpURLConnection) urls[0].openConnection();
            urlConnection.setRequestProperty("Connection", "close");
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(READ_TIMEOUT);

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String resultStream = readStream(urlConnection.getInputStream());

                JSONObject json = new JSONObject(resultStream);
                JSONArray jArray = json.getJSONArray("geonames");

                if (jArray.length() > 0) {
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject item = jArray.getJSONObject(i);
                        serchResult.add(new geonamesPlace(item.getString("lng"), item.getString("lat"), item.getString("summary")));

                        if (isCancelled())
                            break;
                    }
                } else
                    serchResult.add(new geonamesPlace("", "", "not information found"));
            } else
                Log.i("URL", "ErrorCode:" + urlConnection.getResponseCode());
        } catch (IOException e) {
            Log.i("IOExcpetion", e.getMessage());
        } catch (JSONException e) {
            Log.i("JSONException", e.getMessage());
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }

        return serchResult;
    }

    @Override
    protected void onPostExecute(ArrayList<geonamesPlace> searchResult) {
        progressBar.setVisibility(View.GONE);
        ListView listView = listViewWeakReference.get();
        if (listView != null)
            if (searchResult != null && searchResult.size() > 0) {
                ArrayAdapter<geonamesPlace> adapter = (ArrayAdapter<geonamesPlace>) listView.getAdapter();
                adapter.clear();
                adapter.addAll(searchResult);
                adapter.notifyDataSetChanged();
            } else
                Toast.makeText(listView.getContext().getApplicationContext(), "Not Possible to Contact " + URL_GEONAMES, Toast.LENGTH_LONG).show();
        progressBar.setVisibility(View.INVISIBLE);
    }


    private String readStream(InputStream in) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String nextLine = "";

            while ((nextLine = reader.readLine()) != null) {
                sb.append(nextLine);
            }

        } catch (IOException e) {
            Log.i("IOException-ReadStream", e.getMessage());
        }

        return sb.toString();
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        progressBar.setProgress(progress[0]);
    }

    protected void onCancelled() {
        super.onCancelled();
        Log.e("onCancelled", "ASYSNTASK: I´VE BEEN CANCELED AND READY TO GV CLEAN");
    }
}
