package com.tools.payhelper.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tools.payhelper.R;

public class LoadingDialog extends Dialog {

    LoadingDialog(Context context) {
        super(context);
    }

    private LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {

        private View layout;

        private LoadingDialog dialog;

        public Builder(Context context) {
            //这里传入自定义的style，直接影响此Dialog的显示效果。style具体实现见style.xml
            dialog = new LoadingDialog(context, R.style.Dialog);
            layout = LayoutInflater.from(context).inflate(R.layout.pop_loading, null);
            dialog.addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ImageView loadingImg = (ImageView) layout.findViewById(R.id.pop_loading_img);
            loadingImg.setImageResource(R.drawable.loading);
        }

        /**
         * 单按钮对话框和双按钮对话框的公共部分在这里设置
         */
        private void create() {
            dialog.setContentView(layout);
            //用户可以点击手机Back键取消对话框显示
            dialog.setCancelable(false);
            //用户不能通过点击对话框之外的地方取消对话框显示
            dialog.setCanceledOnTouchOutside(false);
        }

        /**
         * 点击弹窗外是否可以取消加载按钮 false:不可以
         */
        public LoadingDialog craeteDialog() {
            create();
            return dialog;
        }
    }
}
