package com.lecso2.bamcompany.rwsectionindexer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class FastScrollView extends LinearLayout {

    private RecyclerView recyclerView;
    private List<String> items = new ArrayList<>();
    private FastScrollEventHandler eventHandler;
    private int lastPosition = -1;

    public FastScrollView(Context context) {
        super(context);
    }

    public FastScrollView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public FastScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setFastScrollEventHandler(FastScrollEventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    /***
     * @param recyclerView must have it's adapter implement SectionIndexer interface
     */
    public void setRecyclerView(RecyclerView recyclerView) {
        //make sure SectionIndexer is implemented. Sadly it can't be enforced by contract
        if (recyclerView.getAdapter() == null || !(recyclerView.getAdapter() instanceof SectionIndexer)) {
            throw new IllegalArgumentException("RecyclerView must have it's adapter implement SectionIndexer interface");
        }

        this.recyclerView = recyclerView;

        //update the items
        updateItemIndicator();

        //subscribe to the adapter changes, so when the data is changed, the updateItemIndicator() will be called
        //and new indexes will be created
        final RecyclerView.Adapter adapter = recyclerView.getAdapter();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                updateItemIndicator();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
                onChanged();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                onChanged();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                onChanged();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                onChanged();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                onChanged();
            }
        });
    }

    //get the indexes from the Adapter
    private void updateItemIndicator() {
        items.clear();
        //the use of the SectionIndexer interface
        SectionIndexer sectionIndexer = (SectionIndexer) recyclerView.getAdapter();
        Object[] sections = sectionIndexer.getSections();
        for (Object section : sections) {
            items.add(section.toString());
        }
        bindItemIndicatorViews();
    }

    private void bindItemIndicatorViews() {
        removeAllViews();
        for (String s : items) {
            TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.fast_scroller_indicator_text, this, false);
            tv.setText(s);
            addView(tv);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getY();

        //if the touching is over, set the position to -1 and call the end event and return with _false_
        //if the touching has started, call the start event
        if (event.getAction() == MotionEvent.ACTION_UP) {
            eventHandler.onFastScrollEventEnd();
            lastPosition = -1;
            return false;
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            eventHandler.onFastScrollEventStart();
            lastPosition = -1;
        }

        //iterate thorough the elements and check whether y is between their top and bottom -> find our item
        //if the item is the same as the last one, do nothing
        //else save the position, call the event with the y (where to show the bubble) and scroll
        for (int i = 0; i < getChildCount(); i++) {
            TextView view = (TextView) getChildAt(i);
            if (view.getTop() <= y && y <= view.getBottom()) {
                if (i == lastPosition) {
                    return true;
                } else {
                    lastPosition = i;
                    if (eventHandler != null) {
                        eventHandler.onFastScrollEvent((view.getBottom() + view.getTop()) / 2, view.getText().toString());
                        scrollToPosition(i);
                        break;
                    }
                }
            }
        }

        return true;
    }

    private void scrollToPosition(int index) {
        int position = ((SectionIndexer) recyclerView.getAdapter()).getPositionForSection(index);

        //in general, you can't be sure that the recyclerView has LinearLayoutManager, so I recommend the next line
        //recyclerView.getLayoutManager().scrollToPosition(position);

        //however, now I know that I'm using LinearLayoutManager, and this scrolling is nicer than the one above
        ((LinearLayoutManager) recyclerView.getLayoutManager()).scrollToPositionWithOffset(position, 0);

        //if you want to, you can enforce the LLM in the constructor or try to find a solution to have this by contract - it is up to you
    }

    //this is implemented by the bubble (FastScrollThumbView)
    interface FastScrollEventHandler {
        void onFastScrollEvent(int y, String s);

        void onFastScrollEventEnd();

        void onFastScrollEventStart();
    }
}
