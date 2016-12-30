package ru.komissarovea.pubtram.data;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class UrlTask extends AsyncTask<String, Void, ArrayList<Transport>> {

    private String title;
    private ProgressBar mProgressBar;
    private OnRequestComplete mRequestComplete;

    public interface OnRequestComplete {
        void onRequestComplete(ArrayList<Transport> list);
    }

    public UrlTask(ProgressBar progressBar, OnRequestComplete complete) {
        mProgressBar = progressBar;
        mRequestComplete = complete;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (mProgressBar != null && mProgressBar.getVisibility() != View.VISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected ArrayList<Transport> doInBackground(String... params) {
        ArrayList<Transport> list = new ArrayList<Transport>();
        try {
            Document doc = null;//Здесь хранится будет разобранный html документ
            try {
                doc = Jsoup.connect(params[0]).timeout(60000).get();
                Elements tdElements = doc.select("td");
                if (tdElements != null && tdElements.size() > 0) {
                    Transport trans = null;
                    for (int i = 0; i < tdElements.size(); i++) {
                        int rest = i % 3;
                        if (rest == 0) {
                            trans = new Transport();
                            trans.setType(tdElements.get(i).text());
                        } else if (rest == 1) {
                            Elements linkElements = tdElements.get(i)
                                    .select("a");
                            if (linkElements.size() > 0)
                                trans.setNumber(linkElements.get(0).text());
                        } else if (rest == 2) {
                            trans.setTime(tdElements.get(i).text());

//                            if (!TextUtils.isEmpty(trans.getNumber())
//                                    && !TextUtils.isEmpty(trans.getNumber())
//                                    && !TextUtils.isEmpty(trans.getNumber()))
                                list.add(trans);
                        }
                    }
                }
                //doc = Jsoup.connect("http://harrix.org").get();
            } catch (IOException e) {
                //Если не получилось считать
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    protected void onPostExecute(ArrayList<Transport> s) {
        super.onPostExecute(s);
        if (mProgressBar != null)
            mProgressBar.setVisibility(View.GONE);
        if (!s.isEmpty() && mRequestComplete != null) {
            mRequestComplete.onRequestComplete(s);
        }
    }
}
