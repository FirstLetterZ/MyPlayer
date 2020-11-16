package com.zpf.common.network

import com.zpf.api.IEvent
import com.zpf.api.IFunction1
import com.zpf.api.IKVPair
import com.zpf.common.app.AppCacheKey
import com.zpf.common.app.AppConstant
import com.zpf.common.util.CheckUtil
import com.zpf.common.util.CommonUtil
import com.zpf.support.network.header.HeaderCarrier
import com.zpf.support.network.interceptor.NetLogInterceptor
import com.zpf.support.network.model.ClientBuilder
import com.zpf.support.network.util.OkHttpNetUtil
import com.zpf.support.util.LogUtil
import com.zpf.tool.PublicUtil
import com.zpf.tool.config.AppContext
import com.zpf.tool.config.GlobalConfigImpl
import com.zpf.tool.expand.util.CacheMap
import com.zpf.tool.expand.util.EventManagerImpl
import com.zpf.tool.gson.GsonUtil
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetSocketAddress
import java.net.Proxy

object NetCall : IFunction1<IEvent<*>> {

    private var poxyIp: String? = null
    private var poxyPort: Int? = null
    private var usePoxy: Boolean = false
    private val apiMap = HashMap<Class<*>, Any>()

    init {
        EventManagerImpl.get().register(javaClass.name, this)
    }

    private fun initNetUtil() {
        CacheMap.put(AppCacheKey.USER_AGENT, CommonUtil.getUserAgent(AppContext.get()))
        val clientBuilder = ClientBuilder.createOkHttpClientBuilder(createHeaderCarrier())
        addNetLogInterceptor(clientBuilder)
        setProxyInfo(clientBuilder)
        OkHttpNetUtil.setDefClient(clientBuilder.build())
    }

    fun openPoxy(open: Boolean) {
        this.usePoxy = open
    }

    fun setPoxyInfo(poxyIp: String?, poxyPort: Int?) {
        this.poxyIp = poxyIp
        this.poxyPort = poxyPort
    }

    fun reset() {
        apiMap.clear()
        initNetUtil()
    }

    override fun func(a: IEvent<*>?) {
        if (a?.eventCode == AppConstant.EVENT_RESET_HOST) {
            reset()
        }
    }

    @Synchronized
    fun <T> getApi(apiClass: Class<T>): T {
        var result = apiMap[apiClass]
        if (result == null) {
            val clientBuilder = ClientBuilder.createDefBuilder(createHeaderCarrier())
            addNetLogInterceptor(clientBuilder.clientBuilder())
            setProxyInfo(clientBuilder.clientBuilder())
            clientBuilder.retrofitBuilder().addConverterFactory(Instance.converterFactory)
            result = clientBuilder.build(AppUrl.BASE_HOST, apiClass)
            apiMap[apiClass] = result!!
        }
        return result as T
    }


    private fun addNetLogInterceptor(builder: OkHttpClient.Builder) {
        if (GlobalConfigImpl.get().isDebug) {
            val netLog = NetLogInterceptor()
            netLog.logListener = object : NetLogInterceptor.OnNetLogListener {
                override fun onSuccess(message: String?) {
                    LogUtil.i(message)
                }

                override fun onError(message: String?) {
                    LogUtil.w(message)
                }
            }
            builder.addInterceptor(netLog)
        }
    }

    private fun setProxyInfo(builder: OkHttpClient.Builder) {
        if (GlobalConfigImpl.get().isDebug && usePoxy) {
            if (CheckUtil.checkIpLegal(poxyIp, poxyPort ?: -1)) {
                builder.proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress(poxyIp!!, poxyPort!!)))
            } else {
                usePoxy = false
            }
        }
    }

    private fun createHeaderCarrier(): HeaderCarrier {
        val headerCarrier = HeaderCarrier()
        val versionName = PublicUtil.getVersionName(AppContext.get())
        headerCarrier
            .addHeader(object : IKVPair<String, String> {
                override fun getKey(): String {
                    return "token"
                }

                override fun getValue(): String {
                    return CacheMap.getString(AppCacheKey.ACCESS_TOKEN)
                }

            })
            .addHeader(object : IKVPair<String, String> {
                override fun getKey(): String {
                    return "User-Agent"
                }

                override fun getValue(): String {
                    return CacheMap.getString(AppCacheKey.USER_AGENT)
                }

            })
            .addHeader(object : IKVPair<String, String> {
                override fun getKey(): String {
                    return "version"
                }

                override fun getValue(): String {
                    return versionName
                }

            }).addHeader(object : IKVPair<String, String> {
                override fun getKey(): String {
                    return "webkit"
                }

                override fun getValue(): String {
                    return CacheMap.getString(AppCacheKey.WEB_KIT)
                }

            })
        return headerCarrier
    }

    private object Instance {
        val converterFactory: GsonConverterFactory =
            GsonConverterFactory.create(GsonUtil.get().gson)
    }

}