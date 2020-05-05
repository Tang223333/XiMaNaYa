package com.example.ximanaya.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ximanaya.R;

public class ConfirmCheckBoxDialog extends Dialog {

    private View mCancel;
    private View mConfirm;
    private OnDialogActionClickListener mOnDialogActionClickListener=null;
    private CheckBox mCheckBox;

    public ConfirmCheckBoxDialog(@NonNull Context context) {
        this(context,0);
    }

    public ConfirmCheckBoxDialog(@NonNull Context context, int themeResId) {
        this(context, true,null);
    }

    protected ConfirmCheckBoxDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_check_box_confirm);
        initView();
        initListener();
    }

    private void initListener() {
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDialogActionClickListener != null) {
                    boolean checked = mCheckBox.isChecked();
                    mOnDialogActionClickListener.onConfirm(checked);
                    dismiss();
                }
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDialogActionClickListener != null) {
                    mOnDialogActionClickListener.onCancel();
                    dismiss();
                }
            }
        });
    }

    private void initView() {
        mCancel = this.findViewById(R.id.dialog_check_box_cancel);
        mConfirm = this.findViewById(R.id.dialog_check_box_confirm);
        mCheckBox = this.findViewById(R.id.dialog_check_box);
    }

    public void setOnDialogActionClickListener(OnDialogActionClickListener onDialogActionClickListener){
        mOnDialogActionClickListener =onDialogActionClickListener;
    }

    public interface OnDialogActionClickListener{
        void onCancel();

        void onConfirm(boolean checked);
    }
}
