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

    /**
     * The text size in px when the button is not pressed.
     */
    private float mTextSizeInPx;

    /**
     * The text size in px when the button is pressed.
     */
    private float mTextSizePressedInPx;

    /**
     * Constructor.
     *
     * @param context holding context.
     */
    public TextPressedButton(Context context) {
        super(context);
        mTextSizeInPx = getTextSize();
        mTextSizePressedInPx = getTextSize();
    }

    /**
     * Constructor.
     *
     * @param context holding context.
     * @param attrs   attribute set from xml.
     */
    public TextPressedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTextSizeInPx = getTextSize();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextPressedButton, 0, 0);
        mTextSizePressedInPx = a.getDimension(R.styleable.TextPressedButton_textSizePressed, mTextSizeInPx);
        a.recycle();
    }

    /**
     * Constructor.
     *
     * @param context  holding context.
     * @param attrs    attribute set from xml.
     * @param defStyle style res id from xml.
     */
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
        if (pressed) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSizePressedInPx);
        } else {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSizeInPx);
        }
    }

}
