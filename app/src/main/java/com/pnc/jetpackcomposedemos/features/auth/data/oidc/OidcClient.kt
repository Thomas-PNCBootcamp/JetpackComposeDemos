package com.pnc.jetpackcomposedemos.features.auth.data.oidc

import com.pnc.jetpackcomposedemos.BuildConfig
import com.pnc.jetpackcomposedemos.features.auth.domain.User
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.HttpUrl
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class OidcClient @Inject constructor(
    private val authApi:AuthApi
)
{
    private var pending:PendingSignIn? = null


    //this will create fresh secrets, remember them and return the url that should be opened in browser
    fun beginSignIn(): HttpUrl {
        val request = PendingSignIn(
            verifier = Pkce.newCodeVerifier(),
            state = Pkce.newRandomValue(),
            nonce = Pkce.newRandomValue()
        )
        pending = request

        return AuthorizeUrl.build(
            authBaseUrl = BuildConfig.AUTH_BASE_URL,
            clientId = BuildConfig.OIDC_CLIENT_ID,
            redirectUri = BuildConfig.OIDC_REDIRECT_URI,
            scopes = BuildConfig.OIDC_SCOPES,
            state = request.state,
            nonce = request.nonce,
            codeChallenge = Pkce.codeChallenge(request.verifier)
        )
    }

    //check whether the browsers answer corresponds to our request and returh auth codes
    fun codeFrom(redirect:String):Result<String>{
        val request = pending ?: return Result.failure(IllegalStateException("No sign in is in progress"))

        val parsed = RedirectParser.parse(redirect)

        if(parsed.error != null){
            val detail = parsed.errorDescription?.let{": $it"} ?: ""
            return Result.failure(IllegalStateException("${parsed.error}$detail"))
        }
        if(parsed.state != request.state){
            return Result.failure(
                IllegalStateException("the response did not match the sign in request (state mismatch)")
            )
        }

        val code = parsed.code ?: return Result.failure(IllegalStateException("the response contained no code"))

        return Result.success(code)

    }


    suspend fun completeSignIn(redirect:String):Result<User>{
        val request = pending ?: return Result.failure(IllegalStateException("No sign in is in progress"))
        val code = codeFrom(redirect).getOrElse {  return Result.failure(it)}
        pending = null //code only works once so pending is done
        return try{
            val tokens = authApi.exchangeCode(
                grantType = "authorization_code",
                clientId = BuildConfig.OIDC_CLIENT_ID,
                code = code,
                redirectUri = BuildConfig.OIDC_REDIRECT_URI,
                codeVerifier = request.verifier
            )
            Result.success(userFrom(tokens,request.nonce))
        }catch(e: CancellationException){
            throw e
        } catch(e: HttpException){
            Result.failure(IllegalStateException(describe(e),e))
        } catch (e: IOException){
            Result.failure(IllegalStateException("Couldn't reach the server check your connection",e))
        } catch(e: Exception){
            Result.failure(e)
        }
    }


    suspend fun refresh(user:User):Result<User>{
        val refreshToken = user.refreshToken ?: return Result.failure(RefreshRejectedException("there is no refresh token"))
        return try{
            val tokens = authApi.refresh(
                grantType = "refresh_token",
                clientId = BuildConfig.OIDC_CLIENT_ID,
                refreshToken = refreshToken
            )
            Result.success(user.copy(
                token = tokens.accessToken,
                refreshToken = tokens.refreshToken ?: refreshToken,
                expiresAtMillies = System.currentTimeMillis() + tokens.expiresIn * 1000))

        }catch (e: CancellationException){
            throw e

        }catch(e: HttpException){
            if(e.code() in 400..499){
                Result.failure(RefreshRejectedException(describe(e)))
            }else{
                Result.failure(IllegalStateException(describe(e),e))
            }
        }catch(e: IOException) {
            Result.failure(
                IllegalStateException(
                    "Couldn't reach the server check your connection",
                    e
                )
            )
        }
    }


    private fun userFrom(tokens:TokenResponse, nonce:String):User{
        val idToken = tokens.idToken ?: throw IllegalStateException("The server did not return an ID token")
        val claims = IdToken.parse(idToken)

        //the nonce we sent must come back inside the ID token. it proves this token was issues for this sign in and isn't a replay of an older sign in
        if(claims.nonce != nonce){
            throw IllegalStateException("The ID token did not contain the expected nonce")
        }

        return User(
            id = claims.subject,
            username = claims.name ?: "customer ${claims.subject}",
            token = tokens.accessToken,
            refreshToken = tokens.refreshToken,
            expiresAtMillies = System.currentTimeMillis() + tokens.expiresIn * 1000
        )
    }

    private fun describe(e:HttpException):String{
        val body = e.response()?.errorBody()?.string().orEmpty()
        val description = runCatching {
            Json.parseToJsonElement(body).jsonObject["error_description"]?.jsonPrimitive?.content
        }.getOrNull()

        return description ?: "the server rejected the sign in (HTTP ${e.code()})."



    }


}



class RefreshRejectedException(message:String):Exception(message)