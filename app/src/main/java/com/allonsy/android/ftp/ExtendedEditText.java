package com.allonsy.android.ftp;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class ExtendedEditText extends android.support.design.widget.TextInputEditText
{
    public ExtendedEditText(Context context, AttributeSet attrs, int defStyle) {super(context, attrs, defStyle);}
    public ExtendedEditText(Context context, AttributeSet attrs) {super(context, attrs);}
    public ExtendedEditText(Context context) {super(context);}

    private KeyImeChange keyImeChangeListener;

    public void setKeyImeChangeListener(KeyImeChange listener)
    {
        keyImeChangeListener = listener;
    }

    public interface KeyImeChange
    {
        public boolean onKeyIme(int keyCode, KeyEvent event);
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event)
    {
        if (keyImeChangeListener != null)
            return keyImeChangeListener.onKeyIme(keyCode, event);
        else
            return super.onKeyPreIme(keyCode, event);
    }
}