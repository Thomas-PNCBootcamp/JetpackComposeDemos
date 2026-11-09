package com.pnc.jetpackcomposedemos.core

import com.pnc.jetpackcomposedemos.BuildConfig
import com.pnc.jetpackcomposedemos.features.auth.domain.SessionManager
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AuthInterceptor @Inject constructor(
    private val session: SessionManager

): Interceptor{
    //get hostname of AW api
    private val apiHost = BuildConfig.AW_BASE_URL.toHttpUrl().host
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        //get request from chain
        val request = chain.request()
        val token = session.currentUser?.token

        //if there is no token or if the request is to a different host
        //then we do not need or want to send the token
        if(token ==null || request.url.host != apiHost){
            return chain.proceed(request)
        }

        //we need to add our token to the request
        return chain.proceed(
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        )

    }

}