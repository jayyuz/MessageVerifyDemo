package com.jaesoon.messageverifydemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.jaesoon.messageverifydemo.fragment.VerifyMessageDialog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onSaveTheWorldClick(View view) {
        final VerifyMessageDialog verifyMessageDialog = new VerifyMessageDialog();
//        verifyMessageDialog.setTitle("请输入短信验证码");
        verifyMessageDialog.setMessagePrompt("已发送验证码至133 **** 3333");
        verifyMessageDialog.setVerifyMessageDialogListener(new VerifyMessageDialog.VerifyMessageDialogListener() {
            @Override
            public void onConfirmClick(String msgCode) {
                verifyMessageDialog.dismiss();
                Toast.makeText(MainActivity.this, "确认提交，内容：" + msgCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                verifyMessageDialog.dismiss();
                Toast.makeText(MainActivity.this, "取消", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResendMessageClick() {
                verifyMessageDialog.dismiss();
                Toast.makeText(MainActivity.this, "请求重新发送短信验证码", Toast.LENGTH_SHORT).show();
            }
        });
        verifyMessageDialog.show(getSupportFragmentManager(), "");
    }
}
