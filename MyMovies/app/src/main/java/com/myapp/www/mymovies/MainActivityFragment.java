package com.myapp.www.mymovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    static GridView gridview;
    static ArrayList<String> posters;
    static boolean sortByPop=true;
    public static final String API_KEY = "YOUR_API_KEY";
    static PreferenceChangeListener listener;
    static SharedPreferences prefs;

    static ArrayList<String> overviews;
    static ArrayList<String> title;
    static ArrayList<String> date;
    static ArrayList<String> ratings;
    static ArrayList<String> id;


    public MainActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<String> array = new ArrayList<String>();
        ImageAdapter adapter = new ImageAdapter(getActivity(), array);
        gridview = (GridView) rootView.findViewById(R.id.gridView);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent= new Intent(getActivity(),DetailedActivity.class)
                        .putExtra("overviews",overviews.get(position))
                        .putExtra("title",title.get(position))
                        .putExtra("date",date.get(position))
                        .putExtra("ratings",ratings.get(position))
                        .putExtra("poster",posters.get(position));
                startActivity(intent);

            }
        });
         return rootView;
    }


    private class PreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener{

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                gridview.setAdapter(null);
                onStart();
        }
    }
    public void onStart() {

        super.onStart();
        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        listener = new PreferenceChangeListener();
        prefs.registerOnSharedPreferenceChangeListener(listener);

        if(prefs.getString("sortby","popularity").equals("popularity"))
        {
            getActivity().setTitle("Most Popular Movies");
            sortByPop = true;
        }
        else if(prefs.getString("sortby","popularity").equals("rating"))
        {
            getActivity().setTitle("Highest Rated Movies");
            sortByPop = false;
        }

                    new ImageLoadTask().execute();

    }


    public class ImageLoadTask extends AsyncTask<Void, Void, ArrayList<String>>{


        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            while (true) {
                try {
                    posters = new ArrayList(Arrays.asList(getPathFromAPI(sortByPop)));

                    return posters;
                } catch (Exception e) {
                    continue;
                }
            }

        }

        @Override
        protected void onPostExecute(ArrayList<String> result) {
            if (result != null && getActivity() != null) {
                ImageAdapter adapter = new ImageAdapter(getActivity(), result);
                gridview.setAdapter(adapter);
            }
        }

        public String[] getPathFromAPI(boolean sortbypop) {
            while (true) {

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                try {
                    String urlString = null;
                    String JSONResult;
                    if (sortbypop) {
                        urlString =
                                "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=" + API_KEY;
                    } else {
                        urlString =
                                "http://api.themoviedb.org/3/discover/movie?sort_by=vote_average.desc&vote_count.gte=500&api_key=" + API_KEY;
                    }
                    URL url = new URL(urlString);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }
                    if (buffer.length() == 0) {
                        return null;
                    }
                    JSONResult = buffer.toString();
                    try {
                        overviews=new ArrayList<String >(Arrays.asList(getJSONStrings(JSONResult,"overview")));
                        title=new ArrayList<String >(Arrays.asList(getJSONStrings(JSONResult,"original_title")));
                        date=new ArrayList<String >(Arrays.asList(getJSONStrings(JSONResult,"release_date")));
                        ratings=new ArrayList<String >(Arrays.asList(getJSONStrings(JSONResult,"vote_average")));

                        return getPathFromJSON(JSONResult);
                    } catch (JSONException e) {
                        return null;
                    }

                } catch (Exception e) {
                    continue;

                }finally {
                    if(urlConnection!=null){
                        urlConnection.disconnect();
                    }
                    if(reader!= null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {

                            continue;
                    }


            }

        }
    }
}

        public String[] getJSONStrings(String JSONStringParams, String param)throws JSONException {
            JSONObject JSONString = new JSONObject(JSONStringParams);

            JSONArray movieArray = JSONString.getJSONArray("results");
            String[] result = new String[movieArray.length()];

            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);

                if (param.equals("vote_average")) {
                    Double number = movie.getDouble("vote_average");
                    String ratings = Double.toString(number) + "/10";
                    result[i] = ratings;
                } else {
                    String data = movie.getString(param);
                    result[i] = data;
                }
            }
            return result;
        }

        public String[] getPathFromJSON(String JSONStringParams)throws JSONException{

            JSONObject JSONString= new JSONObject(JSONStringParams);

            JSONArray movieArray= JSONString.getJSONArray("results");
            String[] result=new String[movieArray.length()];

            for(int i=0; i<movieArray.length(); i++)
            {
                JSONObject movie =movieArray.getJSONObject(i);
                String moviePath = movie.getString("poster_path");
                result[i]=moviePath;
            }
            return result;
        }

}
}