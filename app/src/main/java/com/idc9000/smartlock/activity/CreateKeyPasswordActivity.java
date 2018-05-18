package com.idc9000.smartlock.activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.blankj.utilcode.util.ToastUtils;
import com.github.jhonnyx2012.horizontalpicker.DatePickerListener;
import com.github.jhonnyx2012.horizontalpicker.HorizontalPicker;
import com.idc9000.smartlock.HttpCallBack.CommonCallBack;
import com.idc9000.smartlock.R;
import com.idc9000.smartlock.base.BaseActivity;
import com.idc9000.smartlock.bean.RResult;
import com.idc9000.smartlock.cons.NetworkConst;
import com.idc9000.smartlock.event.CurrentDeviceEvent;
import com.idc9000.smartlock.event.KeyTypeEvent;
import com.idc9000.smartlock.event.MessageEvent;
import com.idc9000.smartlock.utils.ActivityUtil;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.qmuiteam.qmui.widget.QMUITopBar;
import com.socks.library.KLog;
import com.suke.widget.SwitchButton;
import com.zhy.http.okhttp.OkHttpUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.aigestudio.datepicker.cons.DPMode;
import cn.aigestudio.datepicker.views.DatePicker;
import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import okhttp3.Call;

/**
 * Created by 赵先生 on 2017/11/1.
 * 生成租户密码
 */

public class CreateKeyPasswordActivity extends BaseActivity implements DatePickerListener {
    private static final String TAG = "CreateKeyPassword";

    Context context = this;
    @BindView(R.id.topbar)
    QMUITopBar topbar;
    @BindView(R.id.et_phone)
    EditText etPhone;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_confirm_password)
    EditText etConfirmPassword;
    @BindView(R.id.et_rent)
    EditText rent;
    @BindView(R.id.time_spinner)
    MaterialSpinner spinner;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.rb_day)
    RadioRealButton rbDay;
    @BindView(R.id.rb_hour)
    RadioRealButton rbHour;
    @BindView(R.id.rbg)
    RadioRealButtonGroup rbg;

    String HeadShortTime;//小时租 年月日
    String deviceId;//锁Id
    String LongTime;//长租时间
    String ShortTime;//短租(小时)时间
    int timeType; // 0： 长租  1：短租
    String type;//钥匙类型
    String hireType= "0";//出租类型
    String ifShare = "0";//0:非合租 1:合租
    String safeMode = "0"; // 	0:关 1:开
    String startTimeString;//开始时间
    String endTimeString;//结束时间
    @BindView(R.id.btn_confirm)
    Button btnConfirm;
    @BindView(R.id.cb_tip)
    CheckBox cbTip;
    @BindView(R.id.cb_hezu)
    CheckBox cbHezu;
    @BindView(R.id.switch_button)
    SwitchButton switchButton;

    CurrentDeviceEvent currentDevice;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createkeypassword);
        EventBus.getDefault().register(this);
        initView();
        initTime();
    }

    @Override
    protected void initView() {
        super.initView();
        initTopBar();
        initSpinner();
    }

    /**
     * 选择时间
     */
    private void initTime() {

        rbg.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                if (position == 0) {
                    ShortTime=null;
                    showTvTime(LongTime);
                } else {
                    LongTime=null;
                    showTvTime(ShortTime);
                }
                timeType = position;
            }

            private void showTvTime(String time) {
                if (time == null) {
                    tvTime.setText("请选择时间！");
                } else {
                    tvTime.setText(time);
                }
            }
        });
    }

    //租期单位
    private void initSpinner() {
        spinner.setItems("日", "月", "时");
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                switch (item) {
                    case "日":
                        hireType ="0";
                        break;
                    case "月":
                        hireType ="1";
                        break;
                    case "时":
                        hireType ="2";
                        break;
                }
            }
        });
    }

    @Override
    protected void initTopBar() {
        super.initTopBar();
        topbar.addLeftBackImageButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onShowMessageEvent(CurrentDeviceEvent messageEvent) {
        deviceId=messageEvent.getDeviceId();
        topbar.setTitle(messageEvent.getName()+"  生成租户钥匙");
    }
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true) // ThreadMode is optional here
    public void onMessage(KeyTypeEvent event) {
        type=event.getMessage();
    }


    /**
     * 弹出选择小时对话框
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void showShortTime() {

        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.show();
        dialog.setContentView(R.layout.dialog_timepicker);

        // find the picker

        final TimePicker start_time_picker = (TimePicker) dialog.findViewById(R.id.start_time_picker);
        final TimePicker end_time_picker = (TimePicker) dialog.findViewById(R.id.end_time_picker);
        //横向年月日
        HorizontalPicker picker = (HorizontalPicker) dialog.findViewById(R.id.horizontal_datePicker);
        picker.setListener(this)
                .setDateSelectedColor(ContextCompat.getColor(context, R.color.color_green))
                .setDayOfWeekTextColor(ContextCompat.getColor(context, R.color.color_green))
                .setMonthAndYearTextColor(ContextCompat.getColor(context, R.color.color_green))
                .setTodayDateBackgroundColor(Color.GRAY)
                .showTodayButton(true)
                .init();
        picker.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        picker.setDate(DateTime.now());

        //确定
        Button btn_dialog_ok = (Button) dialog.findViewById(R.id.btn_dialog_ok);
        btn_dialog_ok.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                ShortTime = HeadShortTime + "    入住：" + start_time_picker.getCurrentHour() + ":" + start_time_picker.getCurrentMinute() + "   离开：" + end_time_picker.getCurrentHour() + ":" + end_time_picker.getCurrentMinute();
                startTimeString=HeadShortTime+" "+start_time_picker.getCurrentHour() + ":" + start_time_picker.getCurrentMinute();
                endTimeString=HeadShortTime+" "+end_time_picker.getCurrentHour() + ":" + end_time_picker.getCurrentMinute();
                KLog.e(TAG, "startTimeString: "+start_time_picker.getCurrentHour() + ":" + start_time_picker.getCurrentMinute());
                KLog.e(TAG, "endTimeString: "+end_time_picker.getCurrentHour() + ":" + end_time_picker.getCurrentMinute());
                if (validation(start_time_picker, end_time_picker)) return;
                dialog.dismiss();
                tvTime.setText(ShortTime);
            }
        });

        //取消
        Button btn_dialog_cancel = (Button) dialog.findViewById(R.id.btn_dialog_cancel);
        btn_dialog_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                tvTime.setText("请选择时间！");
            }
        });
    }

    /**
     * 弹出对话框 选择长租时间
     */
    private void showChooseLongTime() {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.show();

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final DatePicker picker = new DatePicker(context);
        picker.setDate(DateTime.now().getYear(), DateTime.now().getMonthOfYear());
        picker.setMode(DPMode.MULTIPLE);
        dialog.setContentView(picker, params);

        picker.setOnClosedListener(new DatePicker.OnClosedListener() {
            @Override
            public void OnClosed(int type) {
                if (type == 1) {
                    picker.setOnDateSelectedListener(new DatePicker.OnDateSelectedListener() {
                        @Override
                        public void onDateSelected(List<String> date) {
                            try {
                                startTimeString=date.get(0);
                                endTimeString=date.get(1);
                                LongTime = "入住 " + date.get(0) + "     离开 " + date.get(1);
                                tvTime.setText(LongTime);
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                    });
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }
            }
        });
    }

    //横向日历
    @Override
    public void onDateSelected(DateTime dateSelected) {
        KLog.e(TAG, "Selected date is " + dateSelected.toString() + " day " + dateSelected.getDayOfMonth());
        HeadShortTime = String.valueOf(dateSelected.getYear()) + "/" + dateSelected.getMonthOfYear() + "/" + dateSelected.getDayOfMonth();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @OnClick({R.id.tv_time, R.id.btn_confirm})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_time:
                if (timeType == 0) {
                    showChooseLongTime();
                } else {
                    showShortTime();
                }
                break;
            case R.id.btn_confirm:
                if (!validate()) {
                    return;
                }
                upLoadData();
                break;
        }
    }

    private boolean validate() {
        boolean valid = true;
        String phone = etPhone.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPwd = etConfirmPassword.getText().toString();
        String hirePrice = etConfirmPassword.getText().toString();

        if (ifValueWasEmpty(phone) || phone.length() != 11) {
            etPhone.setError("请输入11位手机号码");
            valid = false;
        } else {
            etPhone.setError(null);
        }

        if (ifValueWasEmpty(password) || password.length() != 8) {
            etPassword.setError("请输入8位数字密码！");
            valid = false;
        } else {
            etPassword.setError(null);
        }

        if (!confirmPwd.equals(password)) {
            etConfirmPassword.setError("密码输入不一致");
            valid = false;
        } else {
            etConfirmPassword.setError(null);
        }

        if (ifValueWasEmpty(hirePrice)) {
            rent.setError("请输入租金！");
            valid = false;
        } else {
            rent.setError(null);
        }

        if (ifValueWasEmpty(startTimeString)||ifValueWasEmpty(endTimeString)) {
            ToastUtils.showShort("请选择入住和离开时间！");
            valid = false;
        }

        if (!cbTip.isChecked()) {
            ToastUtils.showShort("请选择同意锁具使用协议！");
            valid = false;
        }

        if (switchButton.isChecked()){
            safeMode="1";
        }else {
            safeMode="0";
        }

        if (cbHezu.isChecked()){
            ifShare = "1";
        }else {
            ifShare="0";
        }

        return valid;
    }

    private void upLoadData() {

        showProgressDialog(this, "上传钥匙信息...");
        OkHttpUtils
                .post()
                .url(NetworkConst.ADDLOCKKEY_URL)
                .addParams("lockId", deviceId)
                .addParams("hirerPhone", etPhone.getText().toString())
                .addParams("password", etPassword.getText().toString())
                .addParams("confirmPwd", etConfirmPassword.getText().toString())
                .addParams("hireType", hireType)
                .addParams("hirePrice", rent.getText().toString())
                .addParams("startTimeString", startTimeString)
                .addParams("endTimeString", endTimeString)
                .addParams("type", type)
                .addParams("ifShare", ifShare)
                .addParams("safeMode", safeMode)
                .build()
                .execute(new CommonCallBack() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        dismissProgressDialog();
                        ToastUtils.showShort(e.getLocalizedMessage());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        RResult rResult =JSON.parseObject(response, RResult.class);
                        if (rResult.isSuccess()) {
                            dismissProgressDialog();
                            ToastUtils.showShort("添加成功！");
                            ActivityUtil.start(context, KeyListActivity.class, true);
                        } else {
                            KLog.e(TAG, "onResponse: "+rResult.toString());
                            dismissProgressDialog();
                            ToastUtils.showShort(rResult.getErrorMsg());
                        }

                    }
                });
    }

    /**
     * 验证短租时间
     * @param start_time_picker
     * @param end_time_picker
     * @return
     */
    private boolean validation(TimePicker start_time_picker, TimePicker end_time_picker) {
        SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
        Date bt=null;
        try {
            bt = sdf.parse(start_time_picker.getCurrentHour() + ":" + start_time_picker.getCurrentMinute());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date et=null;
        try {
            et = sdf.parse(end_time_picker.getCurrentHour() + ":" + end_time_picker.getCurrentMinute());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (!bt.before(et)) {
            Toast.makeText(context, "开始时间大于结束时间,请重新选择！", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
}