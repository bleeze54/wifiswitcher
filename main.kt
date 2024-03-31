import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class WifiSwitcherActivity : AppCompatActivity() {

    private lateinit var wifiManager: WifiManager
    private lateinit var wifiListView: ListView
    private lateinit var scanResults: List<ScanResult>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiListView = findViewById(R.id.wifiListView)

        // Vérifiez si le WiFi est activé, sinon demandez à l'utilisateur de l'activer
        if (!wifiManager.isWifiEnabled) {
            wifiManager.isWifiEnabled = true
        }

        // Commencez le balayage des réseaux WiFi
        registerReceiver(wifiReceiver, IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
        wifiManager.startScan()
    }

    private val wifiReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            scanResults = wifiManager.scanResults

            val wifiList = mutableListOf<String>()
            for (result in scanResults) {
                wifiList.add(result.SSID)
            }

            val adapter = ArrayAdapter<String>(this@WifiSwitcherActivity, android.R.layout.simple_list_item_1, wifiList)
            wifiListView.adapter = adapter

            // Gérez la logique de commutation ici en fonction de la force du signal, etc.
            wifiListView.setOnItemClickListener { _, _, position, _ ->
                connectToWifi(scanResults[position])
            }
        }
    }

    private fun connectToWifi(scanResult: ScanResult) {
        val wifiConfig = WifiConfiguration()
        wifiConfig.SSID = "\"" + scanResult.SSID + "\""
        wifiConfig.preSharedKey = "\"" + "YourWifiPassword" + "\""

        val netId = wifiManager.addNetwork(wifiConfig)
        wifiManager.disconnect()
        wifiManager.enableNetwork(netId, true)
        wifiManager.reconnect()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(wifiReceiver)
    }
}
