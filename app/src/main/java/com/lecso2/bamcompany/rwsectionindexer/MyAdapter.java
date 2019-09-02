package com.lecso2.bamcompany.rwsectionindexer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SectionIndexer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter implements SectionIndexer {

    private final LayoutInflater layoutInflater;
    private final Comparator<Model> comparator;
    //the list is not sorted itself, but I sort it after every add
    //create a custom implementation if you wish, or see an other option at the bottom
    private final List<Model> mSortedList = new ArrayList<>();

    //---needed for SectionIndexer IF--
    //this will help get the item position for the given index
    private final HashMap<Character, Integer> alphaIndexer = new HashMap<>();
    //this will represent the indexes
    private Character[] sections;

    public MyAdapter(Context context, Comparator<Model> comparator) {
        this.layoutInflater = LayoutInflater.from(context);
        this.comparator = comparator;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.rv_row_model, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.textView.setText(mSortedList.get(position).getText());
    }

    @Override
    public int getItemCount() {
        return mSortedList.size();
    }

    //custom helper methods for adding items----------------

    public void replaceAll(List<Model> models) {
        mSortedList.clear();
        add(models);
    }

    public void add(List<Model> models) {
        mSortedList.addAll(models);
        Collections.sort(mSortedList, comparator);
        createNewIndexes();
        notifyDataSetChanged();
    }

    private void createNewIndexes() {
        alphaIndexer.clear();

        //sectionList will be converted to sections
        List<Character> sectionList = new ArrayList<>();

        //iterate thorough the data we have and have the index
        for (int index = 0; index < mSortedList.size(); index++) {
            //get the first letter of the data
            char c = mSortedList.get(index).getText().charAt(0);
            //if we don't have this letter, put the index and letter to alphaIndexer
            //since the list is sorted, the position will be valid
            //also add to the sections the letter
            if (!alphaIndexer.containsKey(c)) {
                alphaIndexer.put(c, index);
                sectionList.add(c);
            }
        }

        //conversion from List to Array
        sections = new Character[sectionList.size()];
        sections = sectionList.toArray(sections);
    }

    //SectionIndexer IF------------------------

    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return alphaIndexer.get((sections[sectionIndex]));
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }


    //ViewHolder--------------------------------------------------

    private class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        MyViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.rw_row_model_text);
        }
    }

    //SortedList creation------------------------------------------

    /*
    With this approach, notify methods are called automatically and the items are always sorted.
    Now I don't use this, because the onRemove() is called after every deletion in mSortedList.clear() and it is used
    a lot so doesn't seem effective
    Also, if you use this, you should be aware of the call order: this will trigger the notify method, so if you
    create the sections after adding to this list, they will be not updated in FastScrollView
     */
    private final SortedList<Model> mSortedList2 = new SortedList<>(Model.class, new SortedList.Callback<Model>() {

        @Override
        public void onInserted(int position, int count) {
            notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
            notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
            notifyItemMoved(fromPosition, toPosition);
        }

        @Override
        public void onChanged(int position, int count) {
            notifyItemRangeChanged(position, count);
        }

        @Override
        public int compare(Model a, Model b) {
            return comparator.compare(a, b);
        }

        @Override
        public boolean areContentsTheSame(Model oldItem, Model newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(Model item1, Model item2) {
            return item1.getId() == item2.getId();
        }

    });
}
