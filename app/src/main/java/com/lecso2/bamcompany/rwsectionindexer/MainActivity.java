package com.lecso2.bamcompany.rwsectionindexer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setting up Recyclerview
        final RecyclerView recyclerView = findViewById(R.id.recyclerView);
        final LinearLayoutManager lm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(lm);

        //creating data and insert into the adapter
        final List<Model> data = createData();
        final MyAdapter adapter = new MyAdapter(this, customModelComparator);
        adapter.add(data);
        recyclerView.setAdapter(adapter);

        //this is the index column
        final FastScrollView fastScrollView = findViewById(R.id.fastScrollView);
        fastScrollView.setRecyclerView(recyclerView);

        //this is the bubble with the text when you click on the index column
        final FastScrollThumbView thumbView = findViewById(R.id.fastScrollView_thumb);
        thumbView.setupWithFastScroller(fastScrollView);

        //search bar
        final SearchView searchView = findViewById(R.id.searchview);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                final List<Model> filteredModelList = filter(data, query);
                adapter.replaceAll(filteredModelList);
                recyclerView.scrollToPosition(0);
                return true;
            }
        });
    }

    //Creating custom data to show in from of a0, a1, a2, a3, a4 until z5
    private List<Model> createData() {
        List<Model> data = new ArrayList<>();

        String upper = "abcdefghijklmnopqestuvwxyz";
        for (int i = 0; i < upper.length() * 5; i++) {
            String text = "" + upper.charAt(i % upper.length()) + (i / upper.length());
            data.add(new Model(i, text));
        }
        return data;
    }

    //Model can implement Comparable, or this way different comparators can be made
    //the best way depends on your needs
    private Comparator<Model> customModelComparator = new Comparator<Model>() {
        @Override
        public int compare(Model m1, Model m2) {
            return m1.getText().compareTo(m2.getText());
        }
    };

    //return the Models which's text contains the given lowercase query
    private static List<Model> filter(List<Model> models, String query) {
        final String lowerCaseQuery = query.toLowerCase();
        final List<Model> filteredModelList = new ArrayList<>();

        for (Model model : models) {
            if (model.getText().toLowerCase().contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }

        return filteredModelList;
    }

}
