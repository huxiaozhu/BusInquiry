package com.hch.businquiry.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

/**
 * Created by chenhui on 2017/3/28.
 * All Rights Reserved by YiZu
 */

public class PermisitionUtils {
    private Context mContext;
    private Activity mActivity;
//    权限是否设置成功，主要是用来处理用户点击dialog取消设置权限时的一个反馈
    private boolean isSuccess = false;
    int mPermisitionType;
    public PermisitionUtils(Activity activity, Context context) {
        mContext = context;
        mActivity = activity;
    }

    /**
     *设置单个权限
     * @param permistionType
     * @return
     */
    public boolean setSinglePermisition(int permistionType) {
        mPermisitionType = permistionType;
        boolean result=false;
        //        如果当前系统的sdk>=23(android6.0以上)设置权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (mPermisitionType) {
                case IPermisitionType.LOCATION:
                    /**
                     * 位置权限
                     */
                    if (setPermisition(Manifest.permission.ACCESS_COARSE_LOCATION, mPermisitionType)) {
                        result = true;
                    }
                    break;
                case IPermisitionType.PHONE:
                    /**
                     * 拨打电话权限
                     */
                    if (setPermisition(Manifest.permission.CALL_PHONE, mPermisitionType)) {
                        result = true;
                    }
                    break;
                case IPermisitionType.CAMERA:
                    /**
                     * 设置相机权限
                     */
                    if (setPermisition(Manifest.permission.CAMERA, mPermisitionType)) {
                        result = true;
                    }
                    break;
                case IPermisitionType.SDCARDREAD:
                    /**
                     * sd卡读权限
                     */
                    if (setPermisition(Manifest.permission.READ_EXTERNAL_STORAGE, mPermisitionType)) {
                        result = true;
                    }
                    break;
                case IPermisitionType.SDCARDWRITE:
                    /**
                     * sd卡写权限
                     */
                    if (setPermisition(Manifest.permission.WRITE_EXTERNAL_STORAGE, mPermisitionType)) {
                        result = true;
                    }
                    break;
                default:
                    Toast.makeText(mContext, "请选择权限类型", Toast.LENGTH_SHORT).show();
                    break;
            }
            if (isSuccess) isSuccess = false;
        } else {
            result = true;
        }
        return result;
    }

    private boolean setPermisition(String permisition,int permisitionType) {

        /**
         * ****注意，调取权限后用户可能会拒绝，所以要在avtivity或者fragment中重写onRequestPermissionsResult方法。。。。。。。。
         */
        int mPermisition = ContextCompat.checkSelfPermission(mContext, permisition);
//            如果没有位置权限
        if (mPermisition != PackageManager.PERMISSION_GRANTED) {
            //设置权限
            /**
             * 这里一般会弹出一个dialog让用户选择
             * 给权限将isSuccess设置为true
             */
            ActivityCompat.requestPermissions(mActivity, new String[]{permisition}, permisitionType);
            isSuccess = true;
        } else {
            isSuccess = true;
        }
        return isSuccess;
    }

    /**
     * 设置所有权限
     */
    public void setAllPermisition() {
        String[] permisition = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int read = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
            int write = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (read != PackageManager.PERMISSION_GRANTED && write != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mActivity, permisition,IPermisitionType.All);
            }
        }

    }


    public interface IPermisitionType {
        int LOCATION = 0;
        int PHONE = 1;
        int CAMERA = 2;
        int SDCARDREAD = 3;
        int SDCARDWRITE = 4;
        int All = 5;
    }
}
