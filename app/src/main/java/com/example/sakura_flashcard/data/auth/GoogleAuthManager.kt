package com.example.sakura_flashcard.data.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.sakura_flashcard.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

class GoogleAuthManager(private val context: Context) {
    private val credentialManager = CredentialManager.create(context)
    private val TAG = "GoogleLogin"

    /**
     * Get Google ID token using Activity context (required for UI)
     */
    suspend fun getGoogleIdToken(activity: Activity): String? {
        Log.d(TAG, "getGoogleIdToken() called with Activity context")
        
        val serverClientId = context.getString(R.string.google_client_id)
        Log.d(TAG, "Server Client ID: $serverClientId")
        
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(serverClientId)
            .setAutoSelectEnabled(false) // Force showing account picker
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            Log.d(TAG, "Calling credentialManager.getCredential() with Activity...")
            val result = credentialManager.getCredential(activity, request)
            Log.d(TAG, "Got credential response!")
            handleSignIn(result)
        } catch (e: NoCredentialException) {
            Log.e(TAG, "NoCredentialException: No Google accounts found", e)
            null
        } catch (e: GetCredentialCancellationException) {
            Log.e(TAG, "User cancelled Google sign-in", e)
            null
        } catch (e: GetCredentialException) {
            Log.e(TAG, "GetCredentialException: ${e.type} - ${e.message}", e)
            null
        } catch (e: Exception) {
            Log.e(TAG, "Exception: ${e.message}", e)
            null
        }
    }

    private fun handleSignIn(result: GetCredentialResponse): String? {
        val credential = result.credential
        Log.d(TAG, "Credential type: ${credential.type}")
        Log.d(TAG, "Credential class: ${credential::class.java.name}")
        
        return when {
            credential is CustomCredential && 
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL -> {
                try {
                    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                    Log.d(TAG, "Got GoogleIdTokenCredential!")
                    Log.d(TAG, "Display name: ${googleIdTokenCredential.displayName}")
                    Log.d(TAG, "ID Token length: ${googleIdTokenCredential.idToken.length}")
                    googleIdTokenCredential.idToken
                } catch (e: GoogleIdTokenParsingException) {
                    Log.e(TAG, "GoogleIdTokenParsingException: ${e.message}", e)
                    null
                }
            }
            credential is GoogleIdTokenCredential -> {
                Log.d(TAG, "Direct GoogleIdTokenCredential, idToken length: ${credential.idToken.length}")
                credential.idToken
            }
            else -> {
                Log.e(TAG, "Unexpected credential type: ${credential.type}")
                Log.e(TAG, "Credential data: ${credential}")
                null
            }
        }
    }
}
