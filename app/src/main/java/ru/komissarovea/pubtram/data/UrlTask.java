package ru.komissarovea.pubtram.data;

import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class UrlTask extends AsyncTask<String, Void, ArrayList<Transport>> {

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
        return getListByHtmlCleaner(params[0]);
    }

    private ArrayList<Transport> getListByJsoup(String url) {
        ArrayList<Transport> list = new ArrayList<>();
        try {
            Document doc = Jsoup.connect(url).timeout(60000).get();
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

                        list.add(trans);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private ArrayList<Transport> getListByHtmlCleaner(String url) {
        ArrayList<Transport> list = new ArrayList<>();
        try {
            HtmlCleaner cleaner = new HtmlCleaner();
            TagNode rootNode = cleaner.clean(new URL(url));
            TagNode tdElements[] = rootNode.getElementsByName("td", true);
            if (tdElements != null && tdElements.length > 0) {
                Transport trans = null;
                for (int i = 0; i < tdElements.length; i++) {
                    int rest = i % 3;
                    if (rest == 0) {
                        trans = new Transport();
                        trans.setType(tdElements[i].getText().toString());
                    } else if (rest == 1) {
                        TagNode linkElements[] = tdElements[i]
                                .getElementsByName("a", true);
                        if (linkElements.length > 0)
                            trans.setNumber(linkElements[0].getText()
                                    .toString());
                    } else if (rest == 2) {
                        trans.setTime(tdElements[i].getText().toString());

//                        if (!TextUtils.isEmpty(trans.getNumber())
//                                && !TextUtils.isEmpty(trans.getNumber())
//                                && !TextUtils.isEmpty(trans.getNumber()))
                            list.add(trans);
                    }
                }
            }
            // RestServiceProxy serviceProxy = new RestServiceProxy();
            // HttpResponse response = serviceProxy.webGet(params[0]);
            // HttpEntity entity = response.getEntity();
            // String charset = "utf-8";//
            // EntityUtils.getContentCharSet(entity);
            // res = EntityUtils.toString(entity, charset);

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
