package com.jaesoon.messageverifydemo.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jaesoon.messageverifydemo.R;
import com.jaesoon.messageverifydemo.widget.NumberInputView;

import java.util.Timer;
import java.util.TimerTask;

public class VerifyMessageDialog extends DialogFragment {
    private String TAG = "VerifyMessageDialog";
    private TextView tv_title;
    private TextView tv_message_prompt;
    private TextView tv_get_message_code;
    private TextView tv_confirm;
    private NumberInputView numberInputView;
    private ImageView imageClose;

    private View rootView;

    private VerifyMessageDialogListener mVerifyMessageDialogListener;

    private String mTitle;
    private String mMessagePrompt;

    private CountDownTimer mCountDownTimer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        rootView = inflater.inflate(R.layout.fragment_verify_message_dialog, null);
        setCancelable(false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tv_title = view.findViewById(R.id.tv_title);
        tv_message_prompt = view.findViewById(R.id.tv_message_prompt);
        tv_get_message_code = view.findViewById(R.id.tv_get_message_code);
        tv_confirm = view.findViewById(R.id.tv_confirm);
        numberInputView = view.findViewById(R.id.numberInputView);
        imageClose = view.findViewById(R.id.imageClose);

        if (!TextUtils.isEmpty(mTitle)) {
            tv_title.setText(mTitle);
        }
        tv_message_prompt.setText(mMessagePrompt);

        tv_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = numberInputView.getText();
                Log.e(TAG, msg);
                if (TextUtils.isEmpty(msg)) {
                    Toast.makeText(getActivity(), "请输入短信验证码", Toast.LENGTH_SHORT);
                    return;
                }
                if (mVerifyMessageDialogListener != null && !TextUtils.isEmpty(msg)) {
                    Log.e(TAG, "onConfirmClick " + msg);
                    mVerifyMessageDialogListener.onConfirmClick(msg);
                }
            }
        });
        tv_get_message_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVerifyMessageDialogListener != null) {
                    mVerifyMessageDialogListener.onResendMessageClick();
                    tv_get_message_code.setTextColor(Color.parseColor("#b2b2b2"));
                    mCountDownTimer.cancel();
                    mCountDownTimer.start();
                    tv_get_message_code.setEnabled(false);
                }
            }
        });
        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVerifyMessageDialogListener != null) {
                    mVerifyMessageDialogListener.onCancel();
                }
            }
        });

        mCountDownTimer = new CountDownTimer(30 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_get_message_code.setText(String.format("%ds后重新发送", millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                tv_get_message_code.setTextColor(Color.parseColor("#000000"));
                tv_get_message_code.setText("重新获取");
                tv_get_message_code.setEnabled(true);
            }
        };
        tv_get_message_code.setTextColor(Color.parseColor("#b2b2b2"));
        tv_get_message_code.setEnabled(false);
        mCountDownTimer.start();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {

            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 500); // 秒后自动弹出
    }

    @Override
    public void onResume() {
        super.onResume();
        int w = getScreenWidth(getContext());
        getDialog().getWindow().setLayout(w / 4 * 3, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void setVerifyMessageDialogListener(VerifyMessageDialogListener mVerifyMessageDialogListener) {
        this.mVerifyMessageDialogListener = mVerifyMessageDialogListener;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
        if (tv_title != null) {
            tv_title.setText(mTitle);
        }
    }

    public void setMessagePrompt(String mMessagePrompt) {
        this.mMessagePrompt = mMessagePrompt;
        if (tv_message_prompt != null) {
            tv_message_prompt.setText(mMessagePrompt);
        }
    }

    public interface VerifyMessageDialogListener {
        void onConfirmClick(String msgCode);

        void onCancel();

        void onResendMessageClick();
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }
}
