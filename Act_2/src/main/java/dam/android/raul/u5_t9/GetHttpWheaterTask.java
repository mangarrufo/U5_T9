package dam.android.raul.u5_t9;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
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

import static dam.android.raul.u5_t9.MainActivity.URL_WHEATHER;

public class GetHttpWheaterTask extends AsyncTask<URL, Integer, wheaterPlace> {
    private final int CONNECTION_TIMEOUT = 15000;
    private final int READ_TIMEOUT = 10000;

    private final WeakReference<ListView> listViewWeakReference;
    private final ProgressBar progressBar;

    public GetHttpWheaterTask(ListView listView, ProgressBar progressBar) {
        this.progressBar = progressBar;
        this.listViewWeakReference = new WeakReference<>(listView);
    }

    @Override
    protected wheaterPlace doInBackground(URL... urls) {
        HttpURLConnection urlConnection = null;
        ArrayList<String> serchResult = new ArrayList<>();
        wheaterPlace wheaterPlace = null;

        try {
            urlConnection = (HttpURLConnection) urls[0].openConnection();
            urlConnection.setRequestProperty("Connection", "close");
            urlConnection.setConnectTimeout(CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(READ_TIMEOUT);

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String resultStream = readStream(urlConnection.getInputStream());

                JSONObject json = new JSONObject(resultStream);
                JSONArray jArray = json.getJSONArray("weather");

                if (jArray.length() > 0) {
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject item = jArray.getJSONObject(i);
                        serchResult.add(item.getString("description"));

                        if (isCancelled())
                            break;
                    }

                    JSONObject attributesObject = json.getJSONObject("main");
                    serchResult.add(attributesObject.getString("temp"));
                    serchResult.add(attributesObject.getString("humidity"));
                    wheaterPlace = new wheaterPlace(serchResult.get(0), serchResult.get(1), serchResult.get(2));
                } else
                    serchResult.add("not information found");

            } else
                Log.i("URL", "ErrorCode:" + urlConnection.getResponseCode());
        } catch (IOException e) {
            Log.i("IOExcpetion", e.getMessage());
        } catch (JSONException e) {
            Log.i("JSONException", e.getMessage());
        } finally {
            if (urlConnection != null) urlConnection.disconnect();
        }

        return wheaterPlace;
    }

    @Override
    protected void onPostExecute(wheaterPlace searchResult) {
        progressBar.setVisibility(View.GONE);
        ListView listView = listViewWeakReference.get();

        if (searchResult != null) {
            Toast.makeText(listView.getContext().getApplicationContext(), searchResult.toString(), Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(listView.getContext().getApplicationContext(), "Not Possible to Contact " + URL_WHEATHER, Toast.LENGTH_LONG).show();

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
