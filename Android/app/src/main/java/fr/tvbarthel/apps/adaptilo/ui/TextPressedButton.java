package fr.tvbarthel.apps.adaptilo.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.Button;

import fr.tvbarthel.apps.adaptilo.R;

/**
 * A simple {@link android.widget.Button} that changes it's text size on pressed.
 */
public class TextPressedButton extends Button {

    protected float mTextSizeInPx;
    protected float mTextSizePressedInPx;

    public TextPressedButton(Context context) {
        super(context);
        mTextSizeInPx = getTextSize();
        mTextSizePressedInPx = getTextSize();
    }

    public TextPressedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTextSizeInPx = getTextSize();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextPressedButton, 0, 0);
        mTextSizePressedInPx = a.getDimension(R.styleable.TextPressedButton_textSizePressed, mTextSizeInPx);
        a.recycle();
    }

    public TextPressedButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTextSizeInPx = getTextSize();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextPressedButton, defStyle, 0);
        mTextSizePressedInPx = a.getDimension(R.styleable.TextPressedButton_textSizePressed, mTextSizeInPx);
        a.recycle();
    }


    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        if(pressed) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSizePressedInPx);
        } else {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSizeInPx);
        }
    }

}
