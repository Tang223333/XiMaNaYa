package com.example.ximanaya.View;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ximanaya.R;

public class ConfirmDialog extends Dialog {

    private View mCancelSub;
    private View mGiveUp;
    private OnDialogActionClickListener mOnDialogActionClickListener=null;

    public ConfirmDialog(@NonNull Context context) {
        this(context,0);
    }

    public ConfirmDialog(@NonNull Context context, int themeResId) {
        this(context, true,null);
    }

    protected ConfirmDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_confirm);
        initView();
        initListener();
    }

    private void initListener() {
        mGiveUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDialogActionClickListener != null) {
                    mOnDialogActionClickListener.onGiveUpClick();
                    dismiss();
                }
            }
        });

        mCancelSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnDialogActionClickListener != null) {
                    mOnDialogActionClickListener.onCancelSubClick();
                    dismiss();
                }
            }
        });
    }

    private void initView() {
        mCancelSub = this.findViewById(R.id.dialog_check_box_cancel);
        mGiveUp = this.findViewById(R.id.dialog_check_box_confirm);
    }

    public void setOnDialogActionClickListener(OnDialogActionClickListener onDialogActionClickListener){
        mOnDialogActionClickListener =onDialogActionClickListener;
    }

    public interface OnDialogActionClickListener{
        void onCancelSubClick();

        void onGiveUpClick();
    }
}
