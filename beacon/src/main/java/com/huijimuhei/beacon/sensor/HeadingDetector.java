package com.huijimuhei.beacon.sensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.huijimuhei.beacon.utils.ToastUtils;
import com.orhanobut.logger.Logger;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class HeadingDetector implements SensorEventListener {

    SensorManager Sm;
    Context context;
    Sensor Accelerometer, Magnetometer;
    int maLength, stepState, stepCount;
    long lastTimeAcc, curTimeAcc, lastTimeMag, curTimeMag;
    float[] accValues = new float[3];
    float[] magValues = new float[3];
    float[] values = new float[3];
    float[] R = new float[9];
    float[] I = new float[9];
    float accModule = 0, maResult = 0;
    float maxVal = 0f, minVal = 0f, stepLength = 0f;
    static float accThreshold = 0.55f, co_k_wein = 45f, alpha = 0.25f;
    int degreeDisplay;
    float degree;


    public HeadingDetector(Context context) {
        this.context = context;
        loadSystemService();
    }

    public void loadSystemService() {
        Sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        Accelerometer = Sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Magnetometer = Sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void startObtain() {
        maLength = 5;
        stepState = 0;
        stepCount = 0;
        lastTimeAcc = System.currentTimeMillis();
        lastTimeMag = System.currentTimeMillis();
        Sm.registerListener(this, Accelerometer, SensorManager.SENSOR_DELAY_GAME);
        Sm.registerListener(this, Magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            curTimeAcc = System.currentTimeMillis();
            if (curTimeAcc - lastTimeAcc > 40) {
                getStepAccInfo(event.values.clone());
                lastTimeAcc = curTimeAcc;
            }
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            curTimeMag = System.currentTimeMillis();
            if (curTimeMag - lastTimeMag > 40) {
                getAzimuthDegree(event.values.clone());
                lastTimeMag = curTimeMag;
            }
        }
    }

    private void getStepAccInfo(float[] accClone) {
        accValues = accClone;
        accModule = (float) (Math.sqrt(Math.pow(accValues[0], 2) + Math.pow(accValues[1], 2) + Math.pow(accValues[2], 2)) - 9.794);
        maResult = MovingAverage.movingAverage(accModule, maLength);
        if (stepState == 0 && maResult > accThreshold) {
            stepState = 1;
        }
        if (stepState == 1 && maResult > maxVal) { //find peak
            maxVal = maResult;
        }
        if (stepState == 1 && maResult <= 0) {
            stepState = 2;
        }
        if (stepState == 2 && maResult < minVal) { //find bottom
            minVal = maResult;
        }
        if (stepState == 2 && maResult >= 0) {
            stepCount++;
            getStepLengthAndCoordinate();
            maxVal = minVal = stepState = 0;
        }

//        Log.d("step", "Step Count : " + stepCount);
//        Log.d("step", "Step Length : " + decimalF.format(stepLength) + " cm");
    }

    private void getStepLengthAndCoordinate() {
        stepLength = (float) (co_k_wein * Math.pow(maxVal - minVal, 1.0 / 4));
    }

    private void getAzimuthDegree(float[] MagClone) {
        /*
         * get the azimuth degree of the pedestrian.
		 */
        magValues = lowPassFilter(MagClone, magValues);
        if (accValues == null || magValues == null) return;
        boolean sucess = SensorManager.getRotationMatrix(R, I, accValues, magValues);
        if (sucess) {
            SensorManager.getOrientation(R, values);
            degree = (int) (Math.toDegrees(values[0]) + 360) % 360; // translate into (0, 360).
            degree = ((int) (degree + 2)) / 5 * 5; // the value of degree is multiples of 5.

            degreeDisplay = (int) degree;

        }
//        Log.d("step", "Angle azimuth: " + String.valueOf(degreeDisplay) + " degree");
    }


    protected float[] lowPassFilter(float[] input, float[] output) {
        /*
         * low pass filter algorithm implement.
		 */
        if (output == null) return input;
        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + alpha * (input[i] - output[i]);
        }
        return output;
    }

    public void stopObtain() {
        Sm.unregisterListener(this);
    }


    // 计算方向
    private String calculateOrientation(float azimuth) {
        if (azimuth >= -5 && azimuth < 5) {
            return "正北";
        } else if (azimuth >= 5 && azimuth < 85) {
            // Log.i(TAG, "东北");
            return "东北";
        } else if (azimuth >= 85 && azimuth <= 95) {
            // Log.i(TAG, "正东");
            return "正东";
        } else if (azimuth >= 95 && azimuth < 175) {
            // Log.i(TAG, "东南");
            return "东南";
        } else if ((azimuth >= 175 && azimuth <= 180)
                || (azimuth) >= -180 && azimuth < -175) {
            // Log.i(TAG, "正南");
            return "正南";
        } else if (azimuth >= -175 && azimuth < -95) {
            // Log.i(TAG, "西南");
            return "西南";
        } else if (azimuth >= -95 && azimuth < -85) {
            // Log.i(TAG, "正西");
            return "正西";
        } else if (azimuth >= -85 && azimuth < -5) {
            // Log.i(TAG, "西北");
            return "西北";
        }
        return "北";
    }

    public int getAngle() {
        return degreeDisplay;
    }

    public int getStep() {
        int res = stepCount;
        return res;
    }

    public interface IAccelerometerListener {
        public void onNewAccelerometer(float[] values);
    }
}