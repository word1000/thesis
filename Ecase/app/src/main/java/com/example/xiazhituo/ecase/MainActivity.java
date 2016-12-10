package com.example.xiazhituo.ecase;

import com.dd.CircularProgressButton;
import com.example.xiazhituo.ecase.CircleProgressView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.animation.ValueAnimator;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.bluetooth.le.ScanCallback;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //CircularProgressButton getWeightBut;
    CircleProgressView mUnlockCircleView;
    CircleProgressView mWeightCircleView;
    FloatingActionButton mBleFab;

    /* For Bluetooth */
    public BluetoothDevice mDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    private static final int REQUEST_ENABLE_BT = 1;
    // stop scan after 10 second
    private static final long SCAN_PERIOD = 100000;
    private String TAG = MainActivity.class.getSimpleName();

    /* For ble service and char */
    private String mDeviceName;
    private String mDeviceAddress;
    private ExpandableListView mGattServicesList;
    public static BluetoothGatt mBluetoothGatt;
    private BluetoothLeService mBluetoothLeService;
    private BluetoothGattService mGattService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private BluetoothGattCharacteristic mNftCharacteristic;
    private BluetoothGattCharacteristic mWriteCharacteristic;


    private String ECASE_SERVICE_UUID = "08590F7A-DB05-467E-8757-72F6FAEB13D4";
    private String ECASE_NTF_CHARACTERISTIC_UUID = "08590F7E-DB05-467E-8757-72F6FAEB13D4";
    private String ECASE_WRITE_CHARACTERISTIC_UUID = "08590F7F-DB05-467E-8757-72F6FAEB13D4";
    private boolean mConnected = false;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LocateMapActivity.class);
                startActivity(intent);
            }
        });

        mBleFab = (FloatingActionButton) findViewById(R.id.ble_fab);
        mBleFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanLeDevice(true);
            }
        });


        mUnlockCircleView = (CircleProgressView) findViewById(R.id.unlockBtn);
        mUnlockCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				sendBleUnlock();
            }
        });

        mWeightCircleView = (CircleProgressView) findViewById(R.id.weightCircleProgress);
        mWeightCircleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int progress = 20;
                String weightHint = progress + " Kg";
                mWeightCircleView.setmTxtHint1(weightHint);
                mWeightCircleView.setProgress(progress);
            }
        });

        /** For Bluetooth **/

        mHandler = new Handler();

        // 检查当前手机是否支持ble 蓝牙,如果不支持退出程序
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "ble_not_supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // 检查设备上是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "error_bluetooth_not_supported", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_getbattery) {
            sendBleGetBattery();
            return true;
        }

        if (id ==  R.id.action_regfinger) {
            sendBleRegFingerPrint();
        }

        if (id ==  R.id.action_delfinger) {
            sendBleDelFingerPrint();
        }

        if (id ==  R.id.action_setphonenum) {
            sendBleSetPhoneNum();
        }

        return super.onOptionsItemSelected(item);
    }

    private void simulateSuccessProgress(final CircularProgressButton button, int progress) {
        ValueAnimator widthAnimation = ValueAnimator.ofInt(1, progress);
        widthAnimation.setDuration(1500);
        widthAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        widthAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                button.setProgress(value);
            }
        });
        widthAnimation.start();
    }

    /* For Ble */
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;


                    mBluetoothAdapter.getBluetoothLeScanner().stopScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.getBluetoothLeScanner().startScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(mLeScanCallback);
        }
    }

    public ScanCallback mLeScanCallback = new ScanCallback() {

        @Override

        public void onScanResult(int callbackType, final ScanResult scanResult) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String devName = scanResult.getDevice().getName();
                    Log.d(TAG, "run:devName " + devName);

                    if (devName != null) {
                        if ((devName.contains("Luggage"))) {
                            mDevice = scanResult.getDevice();
                            mDeviceAddress = scanResult.getDevice().getAddress();
                            //Toast.makeText(getBaseContext(), devName, Toast.LENGTH_SHORT).show();

                            Intent gattServiceIntent = new Intent(getBaseContext(), BluetoothLeService.class);

                            boolean bll = MainActivity.this.getApplicationContext().bindService(gattServiceIntent,
                                    mServiceConnection, BIND_AUTO_CREATE);

                            if (bll) {
                                System.out.println("Bond connect OK");
                            } else {
                                System.out.println("Bond connect FAIL");
                            }
                        }
                    }
                }
            });
        }
    };


    /**********
     * For Ble service
     */
    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            mBluetoothLeService.connect(mDeviceAddress);
            System.out.println("on Service connected 0000000");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                Toast.makeText(getBaseContext(), "E-CASE connectted"+ mDeviceName, Toast.LENGTH_SHORT).show();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                System.out.println("Get Data from device");
                StringBuffer sb = new StringBuffer();

                sb.append(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                Log.d(TAG, "Gooooot Data: "+sb.toString());
                Toast.makeText(getBaseContext(), sb.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.EXTRA_DATA);
        return intentFilter;
    }

    // Demonstrates how to iterate through the supported GATT
    // Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the
    // ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null)
            return;
        String serviceUuid = null;
        String characteristicUuid = null;
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            serviceUuid = gattService.getUuid().toString();
            if (serviceUuid.equalsIgnoreCase(ECASE_SERVICE_UUID))
            {
                Log.d(TAG, "displayGattServices: " + serviceUuid);

                List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                    characteristicUuid = gattCharacteristic.getUuid().toString();
                    if (characteristicUuid.equalsIgnoreCase(ECASE_NTF_CHARACTERISTIC_UUID)) {
                        Log.e("console", "NTF Characteristic: " + characteristicUuid);

                        mBluetoothLeService.setCharacteristicNotification(gattCharacteristic, true);
                        mNftCharacteristic = gattCharacteristic;
                        //mBluetoothLeService.readCharacteristic(gattCharacteristic);

                    }

                    if (characteristicUuid.equalsIgnoreCase(ECASE_WRITE_CHARACTERISTIC_UUID)) {
                        mWriteCharacteristic = gattCharacteristic;
                        System.out.println("Write Characteristic: " + characteristicUuid);
                    }
                }
            }
        }
    }

    /****
     * common
     ****/
    private static byte[] reverseBytes(byte[] a) {
        int len = a.length;
        byte[] b = new byte[len];
        for (int k = 0; k < len; k++) {
            b[k] = a[a.length - 1 - k];
        }
        return b;
    }

    // byte转十六进制字符串
    public static String bytes2HexString(byte[] bytes) {
        String ret = "";
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            ret += hex.toUpperCase();
        }
        return ret;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.xiazhituo.ecase/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.example.xiazhituo.ecase/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unbindService(mServiceConnection);
        //mBluetoothLeService = null;
    }

    public void sendCmd(String strcmd){
        byte[] byteCmd = strcmd.getBytes(Charset.forName("US-ASCII"));
        if (mConnected) {
            if (mWriteCharacteristic != null) {
                mBluetoothLeService.writeCharacteristic(mWriteCharacteristic, byteCmd);
                System.out.println("Ble send cmd success: " + strcmd );
            } else {
                System.out.println("Ble send cmd failed: " + strcmd );
            }
        }
    }

	public void sendBleUnlock()
	{
		String unlockCmd = "AT+LOCKOFF\r";
		sendCmd(unlockCmd);
	}

    public void sendBleGetBattery()
    {
        String getBatteryCmd = "AT+GTBAT\r";
        sendCmd(getBatteryCmd);
    }

    public void sendBleRegFingerPrint()
    {
        String getBatteryCmd = "AT+FINGERREG\r";
        sendCmd(getBatteryCmd);
    }

    public void sendBleDelFingerPrint()
    {
        String getBatteryCmd = "AT+FINGERDEL\r";
        sendCmd(getBatteryCmd);
    }

    public void sendBleSetPhoneNum()
    {
        String getBatteryCmd = "AT+STSIM=1234\r";
        sendCmd(getBatteryCmd);
    }
}
