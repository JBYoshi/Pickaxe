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
