/*
 * Copyright (c) 2016 JBYoshi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package jbyoshi.sponge.pickaxe.ore.ssl;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.*;
import java.security.cert.*;
import java.util.HashSet;
import java.util.Set;

public final class PickaxeSSLSocketFactory {
    private PickaxeSSLSocketFactory() {
    }

    public static SSLSocketFactory wrap(SSLSocketFactory old, URL url) {
        try (InputStream in = PickaxeSSLSocketFactory.class.getResourceAsStream(url.getHost() + ".crt")) {
            if (in == null) {
                return old;
            }
            KeyManagerFactory keyManager = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            keyManager.init(keyStore, null);

            TrustManagerFactory trustManager = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            Set<TrustAnchor> anchors = new HashSet<>();
            anchors.add(new TrustAnchor((X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(in), null));
            trustManager.init(new CertPathTrustManagerParameters(new PKIXBuilderParameters(anchors, new X509CertSelector())));

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(keyManager.getKeyManagers(), trustManager.getTrustManagers(), new SecureRandom());
            return ctx.getSocketFactory();
        } catch (GeneralSecurityException | IOException e) {
            throw new AssertionError(e);
        }
    }

    public static void wrap(HttpURLConnection conn) {
        if (conn instanceof HttpsURLConnection) {
            HttpsURLConnection https = (HttpsURLConnection) conn;
            https.setSSLSocketFactory(wrap(https.getSSLSocketFactory(), https.getURL()));
        }
    }
}
