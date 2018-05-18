package com.idc9000.smartlock.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.idc9000.smartlock.R;

/**
 * 右侧带清除图标的输入框
 */

public class ClearEditText extends AppCompatEditText implements TextWatcher {

    public static final String TAG = "ClearEditText";

    boolean isShow = false;//是否已经显示右侧删除图标

    public ClearEditText(Context context) {
        this(context, null);
    }

    public ClearEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClearEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Drawable drawableRight = this.getCompoundDrawables()[2];
        if (drawableRight != null) {//有右边图标才去相应点击事件
            int action = event.getAction();
            if (action == MotionEvent.ACTION_UP) {
                boolean xFlag1 = event.getX() > getWidth() - drawableRight.getIntrinsicWidth() - getPaddingRight();
                boolean xFlag2 = event.getX() < getWidth() - getPaddingRight();
                if (xFlag1 && xFlag2) {
                    setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(s)) {
            if (isShow) {
                isShow = !isShow;
                this.setCompoundDrawables(null, null, null, null);
            }
        } else {
            if (!isShow) {
                isShow = !isShow;
                Drawable drawable = this.getResources().getDrawable(R.mipmap.cleanup);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                this.setCompoundDrawables(null, null, drawable, null);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
