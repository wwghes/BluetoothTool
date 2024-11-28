package com.example.bluetoothtool.ui.home

import BTManager
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bluetoothtool.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var btManage: BTManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        btManage = BTManager(requireContext())
        binding.buttonOpenBt.setOnClickListener {
            if (!btManage.isBluetoothSupported()) {
                Toast.makeText(context, "当前设备不支持蓝牙", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                // Android 12 及以上动态权限处理
                if (!hasBluetoothPermission()) {
                    requestBluetoothPermission()
                    return@setOnClickListener
                }
            }

            val success = btManage.openBluetooth(requireActivity())
            if (!success) {
                Toast.makeText(context, "无法开启蓝牙，请检查设备支持情况", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 检查蓝牙权限
    private fun hasBluetoothPermission(): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val permission = android.Manifest.permission.BLUETOOTH_CONNECT
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Android 12 以下无需此权限
        }
    }

    // 请求蓝牙权限
    private fun requestBluetoothPermission() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            val permission = android.Manifest.permission.BLUETOOTH_CONNECT
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(permission),
                BTManager.REQUEST_PERMISSION_BLUETOOTH_CONNECT
            )
        }
    }

    // 处理权限请求结果
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == BTManager.REQUEST_PERMISSION_BLUETOOTH_CONNECT) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "蓝牙权限已授予", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "蓝牙权限被拒绝", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
