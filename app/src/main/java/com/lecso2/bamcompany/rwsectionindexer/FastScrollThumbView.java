package com.lecso2.bamcompany.rwsectionindexer;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

public class FastScrollThumbView extends ConstraintLayout implements FastScrollView.FastScrollEventHandler {

    private TextView textView;
    private View thumbView;
    private SpringAnimation animation;

    public FastScrollThumbView(Context context) {
        super(context);
        init();
    }

    public FastScrollThumbView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FastScrollThumbView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.fast_scroller_thumb_view, this, true);
        thumbView = v.findViewById(R.id.fast_scroller_thumb);

        textView = v.findViewById(R.id.fast_scroller_thumb_text);
        textView.setTextColor(Color.BLUE);

        animation = new SpringAnimation(thumbView, DynamicAnimation.TRANSLATION_Y);
        animation.setSpring(new SpringForce(SpringForce.DAMPING_RATIO_NO_BOUNCY));
    }

    public void setupWithFastScroller(FastScrollView fastScrollView) {
        fastScrollView.setFastScrollEventHandler(this);
    }

    @Override
    public void onFastScrollEvent(int y, String s) {
        textView.setText(s);

        float f = y - thumbView.getMeasuredHeight() / 2F;
        animation.animateToFinalPosition(f);
    }

    @Override
    public void onFastScrollEventEnd() {
        thumbView.setVisibility(GONE);
    }

    @Override
    public void onFastScrollEventStart() {
        thumbView.setVisibility(VISIBLE);
    }
}
