package ru.komissarovea.pubtram;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import java.util.ArrayList;

import ru.komissarovea.pubtram.data.Stop;
import ru.komissarovea.pubtram.data.StopsHelper;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new PrefetchData().execute();
    }

    /*
	 * Async Task
	 */
    private class PrefetchData extends AsyncTask<Void, Void, Void> {

        //private ArrayList<Stop> stops = null;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                //Thread.sleep(3000);
                //DatabaseHelper dbHelper = new DatabaseHelper(SplashActivity.this);
                //ArrayList<Stop> stops = dbHelper.getAllStops();
                ArrayList<Stop> stops = StopsHelper.getDefaultStops(SplashActivity.this);
                StopsHelper.setActualStops(stops);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            // i.putExtra("stops", stops);
            startActivity(i);

            // close this activity
            finish();
        }
    }
}
