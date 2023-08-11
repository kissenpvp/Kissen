/*
 * Copyright 2023 KissenPvP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.kissenpvp.core.networking;

import net.kissenpvp.core.api.networking.NetworkImplementation;
import net.kissenpvp.core.api.networking.socket.DataPackage;
import net.kissenpvp.core.api.networking.socket.Execution;
import net.kissenpvp.core.networking.socket.KissenDataPackage;
import net.kissenpvp.core.networking.socket.SocketEntity;
import net.kissenpvp.core.networking.socket.ssl.KissenSSLKeyData;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.Serializable;
import java.security.*;
import java.security.cert.CertificateException;


public abstract class KissenNetworkImplementation implements NetworkImplementation
{

    @Override public DataPackage createPackage(String id, Serializable... data)
    {
        return new KissenDataPackage(id, data);
    }

    public abstract SocketEntity getSocketEntity();

    @Override public abstract void addExecution(String id, Execution execution);

    @Override public boolean active()
    {
        return getSocketEntity() != null && getSocketEntity().isRunning();
    }

    public @NotNull SSLContext generateContext(@NotNull KissenSSLKeyData keyStore, @NotNull KissenSSLKeyData trustStore) throws NoSuchAlgorithmException, NoSuchProviderException, KeyStoreException, UnrecoverableKeyException, KeyManagementException, CertificateException, IOException
    {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("PKIX", "SunJSSE");
        trustManagerFactory.init(trustStore.getStore());
        X509TrustManager x509TrustManager = null;
        for (TrustManager trustManager : trustManagerFactory.getTrustManagers()) {
            if (trustManager instanceof X509TrustManager) {
                x509TrustManager = (X509TrustManager) trustManager;
                break;
            }
        }

        if (x509TrustManager == null) {
            throw new NullPointerException();
        }

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
        keyManagerFactory.init(keyStore.getStore(), keyStore.password().toCharArray());
        X509KeyManager x509KeyManager = null;
        for (KeyManager keyManager : keyManagerFactory.getKeyManagers()) {
            if (keyManager instanceof X509KeyManager) {
                x509KeyManager = (X509KeyManager) keyManager;
                break;
            }
        }
        if (x509KeyManager == null) throw new NullPointerException();

        // set up the SSL Context
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(new KeyManager[]{x509KeyManager}, new TrustManager[]{x509TrustManager}, null);
        return sslContext;
    }
}
