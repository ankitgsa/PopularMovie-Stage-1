package com.myapp.www.mymovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailedActivityFragment())
                    .commit();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {

            Intent intent=new Intent(this,SettingsActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public static class DetailedActivityFragment extends Fragment {

        public DetailedActivityFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            Intent intent=getActivity().getIntent();
            View rootView= inflater.inflate(R.layout.fragment_detailed, container, false);

            if(intent!=null && intent.hasExtra("overviews"))
            {
                String overview=intent.getStringExtra("overviews");
                ((TextView) rootView.findViewById(R.id.overview))
                            .setText(overview);
            }
            if(intent!=null && intent.hasExtra("title"))
            {

                String title=intent.getStringExtra("title");
                ((TextView) rootView.findViewById(R.id.title))
                        .setText(title);

            }
            if(intent!=null && intent.hasExtra("date"))
            {
                String date=intent.getStringExtra("date");
                ((TextView) rootView.findViewById(R.id.date))
                        .setText(date);

            }
            if(intent!=null && intent.hasExtra("ratings"))
            {
                String rating=intent.getStringExtra("ratings");
                ((TextView) rootView.findViewById(R.id.rating))
                        .setText(rating);

            }

            if(intent!=null && intent.hasExtra("poster"))
            {

                ImageView imageView=(ImageView) rootView.findViewById(R.id.poster);
                String poster=intent.getStringExtra("poster");
                Picasso.with(getActivity())
                        .load("http://image.tmdb.org/t/p/w92/" + poster )
                        .placeholder(R.drawable.loading)
                        .error(R.drawable.error)
                        .fit()
                        .into(imageView);

            }

                return rootView;
        }



    }
}
