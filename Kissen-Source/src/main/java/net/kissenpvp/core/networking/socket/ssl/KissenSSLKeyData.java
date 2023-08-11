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

package net.kissenpvp.core.networking.socket.ssl;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public record KissenSSLKeyData(@NotNull String password, @NotNull File file)
{
    public @NotNull KeyStore getStore() throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException
    {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        InputStream inputStream = file.toURI().toURL().openStream();
        keyStore.load(inputStream, password.toCharArray());
        return keyStore;
    }
}
