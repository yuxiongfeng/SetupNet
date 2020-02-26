package com.proton.setupnet;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.proton.setupnet.databinding.ActivityConfigIpBinding;
import com.proton.setupnet.utils.Utils;
import com.proton.setupnet.utils.ble.AdvertiseManager;

import java.util.Arrays;


public class ConfigIpActivity extends AppCompatActivity {

    ActivityConfigIpBinding binding;
    /**
     * 是否开始配置ip
     */
    private boolean isStart = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_config_ip);

        binding.idBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnConfigIp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                if (isStart) {
                    AdvertiseManager.getInstance().startAdvertising(ConfigIpActivity.this, false, 0x04cb, getAdvertiseBytes());
                    binding.btnConfigIp.setText("停止配置静态ip");
                }else {
                    AdvertiseManager.getInstance().stopAdvertising();
                    binding.btnConfigIp.setText("配置静态ip");
                }
                isStart = !isStart;
            }
        });
    }

    private byte[] getAdvertiseBytes() {

        String ipOne = binding.idIpOne.getText().toString();
        String ipTwo = binding.idIpTwo.getText().toString();
        String ipThree = binding.idIpThree.getText().toString();
        String ipFour = binding.idIpFour.getText().toString();

        if (TextUtils.isEmpty(ipOne)
                || TextUtils.isEmpty(ipTwo)
                || TextUtils.isEmpty(ipThree)
                || TextUtils.isEmpty(ipFour)) {
            Toast.makeText(this, "请输入正确的Ip地址", Toast.LENGTH_SHORT).show();
            return null;
        }

        if (!isAvaliableRange(ipOne)
                || !isAvaliableRange(ipTwo)
                || !isAvaliableRange(ipThree)
                || !isAvaliableRange(ipFour)) {
            Toast.makeText(this, "Ip地址，每位不能必须在0~255之间", Toast.LENGTH_SHORT).show();
            return null;
        }

        byte[] ipOneBytes = Utils.hexStringToBytes(Integer.toHexString(Integer.valueOf(ipOne)));
        byte[] ipTwoBytes = Utils.hexStringToBytes(Integer.toHexString(Integer.valueOf(ipTwo)));
        byte[] ipThreeBytes = Utils.hexStringToBytes(Integer.toHexString(Integer.valueOf(ipThree)));
        byte[] ipFourBytes = Utils.hexStringToBytes(Integer.toHexString(Integer.valueOf(ipFour)));

        String subnetMaskOne = binding.idSubnetMaskOne.getText().toString();
        String subnetMaskTwo = binding.idSubnetMaskTwo.getText().toString();
        String subnetMaskThree = binding.idSubnetMaskThree.getText().toString();
        String subnetMaskFour = binding.idSubnetMaskFour.getText().toString();

        if (TextUtils.isEmpty(subnetMaskOne)
                || TextUtils.isEmpty(subnetMaskTwo)
                || TextUtils.isEmpty(subnetMaskThree)
                || TextUtils.isEmpty(subnetMaskFour)) {
            Toast.makeText(this, "请输入正确的子网掩码地址", Toast.LENGTH_SHORT).show();
            return null;
        }

        if (!isAvaliableRange(subnetMaskOne)
                || !isAvaliableRange(subnetMaskTwo)
                || !isAvaliableRange(subnetMaskThree)
                || !isAvaliableRange(subnetMaskFour)) {
            Toast.makeText(this, "子网掩码地址，每位不能必须在0~255之间", Toast.LENGTH_SHORT).show();
        }

        byte[] subnetMaskOneBytes = Utils.hexStringToBytes(Integer.toHexString(Integer.valueOf(subnetMaskOne)));
        byte[] subnetMaskTwoBytes = Utils.hexStringToBytes(Integer.toHexString(Integer.valueOf(subnetMaskTwo)));
        byte[] subnetMaskThreeBytes = Utils.hexStringToBytes(Integer.toHexString(Integer.valueOf(subnetMaskThree)));
        byte[] subnetMaskFourBytes = Utils.hexStringToBytes(Integer.toHexString(Integer.valueOf(subnetMaskFour)));

        String gatewayOne = binding.idGatewayOne.getText().toString();
        String gatewayTwo = binding.idGatewayTwo.getText().toString();
        String gatewayThree = binding.idGatewayThree.getText().toString();
        String gatewayFour = binding.idGatewayFour.getText().toString();

        if (TextUtils.isEmpty(gatewayOne)
                || TextUtils.isEmpty(gatewayTwo)
                || TextUtils.isEmpty(gatewayThree)
                || TextUtils.isEmpty(gatewayFour)) {
            Toast.makeText(this, "请输入正确的网关地址", Toast.LENGTH_SHORT).show();
            return null;
        }

        if (!isAvaliableRange(gatewayOne)
                || !isAvaliableRange(gatewayTwo)
                || !isAvaliableRange(gatewayThree)
                || !isAvaliableRange(gatewayFour)) {
            Toast.makeText(this, "网关地址，每位不能必须在0~255之间", Toast.LENGTH_SHORT).show();
            return null;
        }

        byte[] gatewayOneBytes = Utils.hexStringToBytes(Integer.toHexString(Integer.valueOf(gatewayOne)));
        byte[] gatewayTwoBytes = Utils.hexStringToBytes(Integer.toHexString(Integer.valueOf(gatewayTwo)));
        byte[] gatewayThreeBytes = Utils.hexStringToBytes(Integer.toHexString(Integer.valueOf(gatewayThree)));
        byte[] gatewayFourBytes = Utils.hexStringToBytes(Integer.toHexString(Integer.valueOf(gatewayFour)));

        return concatAll(ipOneBytes, ipTwoBytes, ipThreeBytes, ipFourBytes
                , subnetMaskOneBytes, subnetMaskTwoBytes, subnetMaskThreeBytes, subnetMaskFourBytes
                , gatewayOneBytes, gatewayTwoBytes, gatewayThreeBytes, gatewayFourBytes);
    }

    /**
     * 判断数是否在0~255之间
     *
     * @return
     */
    private boolean isAvaliableRange(String num) {
        int temp = Integer.valueOf(num);
        if (temp >= 0 && temp <= 255) {
            return true;
        }
        return false;
    }

    /**
     * 合并多个数组
     *
     * @return
     */
    private byte[] concatAll(byte[] first, byte[]... reset) {
        int totalLength = first.length;
        for (byte[] array : reset) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;

        for (byte[] array : reset) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AdvertiseManager.getInstance().stopAdvertising();
    }
}
