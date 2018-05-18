package com.idc9000.smartlock.view;

import android.os.CountDownTimer;
import android.widget.Button;

import com.idc9000.smartlock.R;

/**
 * Created by 赵先生 on 2017/10/12.
 */

public class TimeCount extends CountDownTimer {
    private Button button;
    private String tickText;
    private String finishText;

    /** * @param millisInFuture 倒计时总时长 * @param countDownInterval 倒计时单位 毫秒. */
    public TimeCount(long millisInFuture, long countDownInterval,
                     Button button,String tickText,String finishText) {
        super(millisInFuture, countDownInterval);
        this.button = button;
        this.tickText=tickText;
        this.finishText=finishText;
    }


    @Override
    public void onTick(long millisUntilFinished) {
        button.setText(millisUntilFinished / 1000 + tickText);
        button.setEnabled(false);
        button.setBackgroundResource(R.drawable.shape_verify_btn_press);
    }

    @Override
    public void onFinish() {
        button.setEnabled(true);
        button.setText(finishText);
        button.setBackgroundResource(R.drawable.shape_verify_btn_normal);
    }
}
