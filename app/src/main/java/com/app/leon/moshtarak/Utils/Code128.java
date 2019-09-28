package com.app.leon.moshtarak.Utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Code128 {
    private static final String TAG = Code128.class.getSimpleName();
    private static final int CODE_START_B = 104;
    private static final int CODE_STOP = 106;
    private static final int DIVISOR = 103;
    private String data;
    private Context context;
    private int weight = 0;
    private int weight_sum = 0;
    private int check_sum = 0;

    public Code128(Context context) {
        this.context = context;
    }

    public Code128(String data) {
        this.data = data;
    }

    public static boolean checkNumber(String data) {
        int len = data.length();
        for (int i = 0; i < len; i++) {
            char ch = data.charAt(i);
            if (ch < '0' || ch > '9') {
                return false;
            }
        }
        return true;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    private void init() {
        weight = 0;
        weight_sum = 0;
        check_sum = 0;
    }

    private byte[] initBuffer(int dataLen) {
        int sum = 0;
        sum = sum + 11;
        sum = sum + (dataLen * 11);
        sum = sum + 11;
        sum = sum + 13;
        return new byte[sum];
    }

    private byte[] encode() {
        if (data == null) {
            return null;
        }
        int len = data.length();
        int index = -1;
        int count = 0;
        init();
        byte[] buffer = initBuffer(len);
        int pos = 0;
        count = appendData(Code128Constant.CODE_WEIGHT[CODE_START_B], buffer, pos, "StartCode");
        pos += count;
        weight_sum = CODE_START_B;
        for (int i = 0; i < len; i++) {
            weight++;
            char ch = data.charAt(i);
            index = ch - ' ';
            byte[] ch_weight = Code128Constant.CODE_WEIGHT[index];
            count = appendData(ch_weight, buffer, pos, ch + "");
            pos += count;
            int weightByValue = weight * index;
            weight_sum += weightByValue;
        }

        check_sum = weight_sum % DIVISOR;

        count = appendData(Code128Constant.CODE_WEIGHT[check_sum], buffer, pos, "CheckSum");
        pos += count;
        count = appendData(Code128Constant.CODE_WEIGHT[CODE_STOP], buffer, pos, "CODE_STOP");
        pos += count;
        return buffer;
    }

    public Bitmap getBitmap(int width, int height) {
        byte[] code = encode();
        if (code == null) {
            return null;
        }
        int TOP_GAP = 30;
        int BOTTOM_GAP = 30;
        int inputWidth = code.length;
        int fullWidth = inputWidth + (6);
        int outputWidth = Math.max(width, fullWidth);
        int outputHeight = Math.max(1, height) - BOTTOM_GAP;
        int multiple = outputWidth / fullWidth;
        int leftPadding = (outputWidth - (inputWidth * multiple)) / 2;
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(Color.rgb(255, 255, 255));
        Rect bounds = new Rect(0, 0, width, height);
        canvas.drawRect(bounds, bgPaint);
        Paint barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setColor(Color.rgb(0, 0, 0));
        barPaint.setStrokeWidth(0);
        for (int inputX = 0, outputX = leftPadding; inputX < inputWidth; inputX++, outputX += multiple) {
            if (code[inputX] == 1) {
                canvas.drawRect(outputX, TOP_GAP, (outputX + multiple), outputHeight, barPaint);
            }
        }
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;
        bgPaint.setColor(Color.WHITE);
//        int size = (int) (26 * scale);
        int size = (int) (1 * scale);
        bgPaint.setTextSize(size);
        String str = insertSpace(data);
        bgPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(str, width / 2, (height - 10), bgPaint);
        return bitmap;
    }

    private String insertSpace(String data) {
        StringBuilder sb = new StringBuilder();
        int cnt = 1;
        int len = data.length();
        for (int i = 0; i < len; i++, cnt++) {
            sb.append(data.charAt(i));
            if (cnt % 4 == 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private int getSum() {
        return getSum();
    }

    private int appendData(byte[] weights, byte[] dst, int pos, String debugdata) {
        byte color = 1;
        int count = 0;
        int index = pos;
        for (byte weight : weights) {
            for (int i = 0; i < weight; i++) {
                dst[index] = color;
                index++;
                count++;
            }
            color ^= 1;
        }
        return count;
    }

    public void printByteArr(String msg, byte[] buff) {
        if (buff == null) {
            return;
        }
        int color = 1;
        StringBuilder sb = new StringBuilder();
        for (byte by : buff) {
            for (int i = 0; i < by; i++) {
                sb.append(color);
            }
            color ^= 1;
        }
        android.util.Log.e(TAG, "char: " + msg + " barcode weight: " + sb.toString());
    }

    public void printCode128MetaInfo() {
        android.util.Log.e(TAG, "sum: " + weight_sum);
        android.util.Log.e(TAG, "divisor: " + DIVISOR);
        android.util.Log.e(TAG, "sum/divisor: " + (weight_sum / DIVISOR));
        android.util.Log.e(TAG, "check sum value: " + check_sum);
    }
}