package feifu.com.testview.gridpswview;

import android.view.View;

/**
 * Created by Administrator on 2017/5/8.
 */

public interface OnGridPswViewListener {
    void onTextChanged(CharSequence s, int start, int before, int count);
    void onClick(View v);
    void onComplete(String s);

}
