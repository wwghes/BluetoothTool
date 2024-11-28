import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat

class BTManager(private val context: Context) {
    private val bluetoothManager: BluetoothManager? =
        context.getSystemService(BluetoothManager::class.java)
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

    fun isBluetoothSupported(): Boolean {
        return bluetoothAdapter != null
    }

    fun openBluetooth(activity: Activity): Boolean {
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "设备不支持蓝牙", Toast.LENGTH_SHORT).show()
            return false
        }

        // 检查权限（仅适用于 Android 12 或更高版本）
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                    REQUEST_PERMISSION_BLUETOOTH_CONNECT
                )
                return false
            }
        }

        try {
            if (!bluetoothAdapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
                Toast.makeText(context, "蓝牙已开启", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            Toast.makeText(context, "权限不足，无法打开蓝牙", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    companion object {
        const val REQUEST_ENABLE_BT = 1
        const val REQUEST_PERMISSION_BLUETOOTH_CONNECT = 2
    }
}
