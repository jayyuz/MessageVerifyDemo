package com.jaesoon.messageverifydemo.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

import com.jaesoon.messageverifydemo.R;

import java.util.ArrayList;

public class NumberInputView extends LinearLayout {
    private String TAG = "NumberInputView";
    private InputMethodManager input;//输入法管理
    private ArrayList<Integer> result;//输入结果保存
    private int count = 6;//密码位数
    private int mActiveColor = Color.parseColor("#6ae1ff");
    private int mInactiveColor = Color.parseColor("#47b4db");
    private int mTextColor = Color.parseColor("#000000");
    private int mTextSize = (int) (Resources.getSystem().getDisplayMetrics().density * 25);
    private int mSpacing = (int) (Resources.getSystem().getDisplayMetrics().density * 4);
    private int mBottomLineWidth = (int) (Resources.getSystem().getDisplayMetrics().density * 1.5);

    public NumberInputView(Context context) {
        super(context);
        init(context, null);
    }

    public NumberInputView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        clearFocus();
        input = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        result = new ArrayList<>();

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.NumberInputView);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.NumberInputView_activeColor:
                    mActiveColor = a.getColor(attr, mActiveColor);
                    break;
                case R.styleable.NumberInputView_inactiveColor:
                    mInactiveColor = a.getColor(attr, mInactiveColor);
                    break;
                case R.styleable.NumberInputView_numberColor:
                    mTextColor = a.getColor(attr, mTextColor);
                    break;
                case R.styleable.NumberInputView_numberTextSize:
                    mTextSize = a.getDimensionPixelSize(attr, mTextSize);
                    break;
                case R.styleable.NumberInputView_spacing:
                    mSpacing = a.getDimensionPixelSize(attr, mSpacing);
                    break;
                case R.styleable.NumberInputView_bottomLineWidth:
                    mBottomLineWidth = a.getDimensionPixelSize(attr, mBottomLineWidth);
                    break;
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getChildCount() <= 0) {
            for (int i = 0; i < 6; i++) {
                SingleNumberView singleNumberView = new SingleNumberView(getContext(), null);
                singleNumberView.setPadding(mSpacing / 2, mSpacing / 2, mSpacing / 2, mSpacing);
                singleNumberView.setTextColor(mTextColor);
                singleNumberView.setTextSize(mTextSize);
                singleNumberView.setActiveColor(mActiveColor);
                singleNumberView.setInactiveColor(mInactiveColor);
                singleNumberView.setBottomLineWidth(mBottomLineWidth);
                LinearLayout.LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                singleNumberView.setLayoutParams(layoutParams);
                addView(singleNumberView);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {//点击控件弹出输入键盘
            requestFocus();
            input.showSoftInput(this, InputMethodManager.SHOW_FORCED);
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (gainFocus) {
            input.showSoftInput(this, InputMethodManager.SHOW_FORCED);
        } else {
            input.hideSoftInputFromInputMethod(this.getWindowToken(), 0);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        if (!hasWindowFocus) {
            input.hideSoftInputFromWindow(this.getWindowToken(), 0);
        }
    }

    public String getText() {
        StringBuffer sb = new StringBuffer();
        for (int i : result) {
            sb.append(i);
        }
        return sb.toString();
    }

    private InputCallBack inputCallBack;//输入完成的回调

    public interface InputCallBack {
        void onInputFinish(String result);
    }

    public void setInputCallBack(InputCallBack inputCallBack) {
        this.inputCallBack = inputCallBack;
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.inputType = InputType.TYPE_CLASS_NUMBER;//输入类型为数字
        outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE;
        return new JInputConnection(this, false);
    }

    class JInputConnection extends BaseInputConnection {

        public JInputConnection(View targetView, boolean fullEditor) {
            super(targetView, fullEditor);
        }

        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            //这里是接受输入法的文本的，我们只处理数字，所以什么操作都不做
            return super.commitText(text, newCursorPosition);
        }

        @Override
        public boolean sendKeyEvent(KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                Log.e(TAG, event.getKeyCode() + "");
                if (event.isShiftPressed()) {//处理*#等键
                    return false;
                }
                int keyCode = event.getKeyCode();
                if (keyCode >= KeyEvent.KEYCODE_0 && keyCode <= KeyEvent.KEYCODE_9) {//只处理数字
                    if (result.size() < count) {
                        result.add(keyCode - KeyEvent.KEYCODE_0);
                        if (getChildAt(result.size() - 1) instanceof SingleNumberView) {
                            Log.e(TAG, keyCode + ";");
                            ((SingleNumberView) getChildAt(result.size() - 1)).setNumber(result.get(result.size() - 1) + "");
                        }
                        ensureFinishInput();
                    }
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    if (!result.isEmpty()) {//不为空，删除最后一个
                        result.remove(result.size() - 1);
                        if (getChildAt(result.size()) instanceof SingleNumberView) {
                            ((SingleNumberView) getChildAt(result.size())).setNumber("");
                        }
                    }
                    return true;
                }
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    ensureFinishInput();
                    return true;
                }
            }
            return super.sendKeyEvent(event);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            //软键盘的删除键 DEL 无法直接监听，自己发送del事件
            if (beforeLength == 1 && afterLength == 0) {
                return super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                        && super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            }
            return super.deleteSurroundingText(beforeLength, afterLength);
        }
    }

    /**
     * 判断是否输入完成，输入完成后调用callback
     */
    void ensureFinishInput() {
        if (result.size() == count) {//输入完成
            if (inputCallBack != null) {
                StringBuffer sb = new StringBuffer();
                for (int i : result) {
                    sb.append(i);
                }
                inputCallBack.onInputFinish(sb.toString());
            }
        }
    }

}
