package fr.tvbarthel.apps.adaptilo.ui;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Don't used hardcoded width and height, this view will always be rendered as a square based
 * on the min between width and height.
 * <p/>
 * Can also be used with an oval background in order to obtain a perfect circle.
 */
public class SquareTextPressedButton extends TextPressedButton {
    public SquareTextPressedButton(Context context) {
        super(context);
    }

    public SquareTextPressedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareTextPressedButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int spec = Math.min(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(spec, spec);
    }
}
