package com.example.sakura_flashcard.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages network connectivity state and provides offline/online status
 */
@Singleton
class NetworkConnectivityManager @Inject constructor(
    private val context: Context
) {
    
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    
    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()
    
    private val _connectionType = MutableStateFlow(ConnectionType.NONE)
    val connectionType: StateFlow<ConnectionType> = _connectionType.asStateFlow()
    
    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    
    init {
        setupNetworkMonitoring()
        checkInitialConnectivity()
    }
    
    /**
     * Sets up network connectivity monitoring
     */
    private fun setupNetworkMonitoring() {
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            .build()
        
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                updateConnectivityState(network)
            }
            
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                updateConnectivityState(network, networkCapabilities)
            }
            
            override fun onLost(network: Network) {
                _isOnline.value = false
                _connectionType.value = ConnectionType.NONE
            }
        }
        
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback!!)
    }
    
    /**
     * Checks initial connectivity state
     */
    private fun checkInitialConnectivity() {
        val activeNetwork = connectivityManager.activeNetwork
        if (activeNetwork != null) {
            val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            updateConnectivityState(activeNetwork, networkCapabilities)
        } else {
            _isOnline.value = false
            _connectionType.value = ConnectionType.NONE
        }
    }
    
    /**
     * Updates connectivity state based on network information
     */
    private fun updateConnectivityState(network: Network, networkCapabilities: NetworkCapabilities? = null) {
        val capabilities = networkCapabilities ?: connectivityManager.getNetworkCapabilities(network)
        
        if (capabilities != null) {
            val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            val isValidated = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            
            _isOnline.value = hasInternet && isValidated
            
            _connectionType.value = when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionType.WIFI
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectionType.CELLULAR
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ConnectionType.ETHERNET
                else -> ConnectionType.OTHER
            }
        } else {
            _isOnline.value = false
            _connectionType.value = ConnectionType.NONE
        }
    }
    
    /**
     * Checks if device is currently online
     */
    fun isCurrentlyOnline(): Boolean {
        return _isOnline.value
    }
    
    /**
     * Gets current connection type
     */
    fun getCurrentConnectionType(): ConnectionType {
        return _connectionType.value
    }
    
    /**
     * Checks if connection is metered (cellular data)
     */
    fun isConnectionMetered(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED) == false
    }
    
    /**
     * Checks if connection is suitable for large data transfers
     */
    fun isSuitableForLargeTransfers(): Boolean {
        return isCurrentlyOnline() && 
               getCurrentConnectionType() == ConnectionType.WIFI &&
               !isConnectionMetered()
    }
    
    /**
     * Gets network quality score (0-100)
     */
    fun getNetworkQualityScore(): Int {
        if (!isCurrentlyOnline()) return 0
        
        return when (getCurrentConnectionType()) {
            ConnectionType.WIFI -> if (isConnectionMetered()) 80 else 100
            ConnectionType.ETHERNET -> 100
            ConnectionType.CELLULAR -> if (isConnectionMetered()) 40 else 60
            ConnectionType.OTHER -> 50
            ConnectionType.NONE -> 0
        }
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        networkCallback?.let { 
            connectivityManager.unregisterNetworkCallback(it) 
        }
    }
}

/**
 * Types of network connections
 */
enum class ConnectionType {
    NONE,
    WIFI,
    CELLULAR,
    ETHERNET,
    OTHER
}

/**
 * Network status information
 */
data class NetworkStatus(
    val isOnline: Boolean,
    val connectionType: ConnectionType,
    val isMetered: Boolean,
    val qualityScore: Int,
    val isSuitableForLargeTransfers: Boolean
) {
    companion object {
        fun from(manager: NetworkConnectivityManager): NetworkStatus {
            return NetworkStatus(
                isOnline = manager.isCurrentlyOnline(),
                connectionType = manager.getCurrentConnectionType(),
                isMetered = manager.isConnectionMetered(),
                qualityScore = manager.getNetworkQualityScore(),
                isSuitableForLargeTransfers = manager.isSuitableForLargeTransfers()
            )
        }
    }
}