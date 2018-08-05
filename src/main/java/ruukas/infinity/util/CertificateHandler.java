package ruukas.infinity.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManagerFactory;

import ruukas.infinity.Infinity;

public class CertificateHandler
{
    public static void addLetsEncryptCertificate()
    {
        try
        {
            KeyStore keyStore = KeyStore.getInstance( KeyStore.getDefaultType() );
            Path ksPath = Paths.get( System.getProperty( "java.home" ), "lib", "security", "cacerts" );
            keyStore.load( Files.newInputStream( ksPath ), "changeit".toCharArray() );
            
            CertificateFactory cf = CertificateFactory.getInstance( "X.509" );
            
            InputStream caInput = new BufferedInputStream( CertificateHandler.class.getResourceAsStream( "/assets/infinity/cert/lets-encrypt-x3-cross-signed.der" ) );
            Certificate crt = cf.generateCertificate( caInput );
            Infinity.logger.info( "Added Certificate for " + ((X509Certificate) crt).getSubjectDN() );
            
            keyStore.setCertificateEntry( "lets-encrypt-x3-cross-signed", crt );
            caInput.close();
            
            TrustManagerFactory tmf = TrustManagerFactory.getInstance( TrustManagerFactory.getDefaultAlgorithm() );
            tmf.init( keyStore );
            SSLContext sslContext = SSLContext.getInstance( "TLS" );
            sslContext.init( null, tmf.getTrustManagers(), null );
            SSLContext.setDefault( sslContext );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }
    
    public static void testIfNeedsCert() throws IOException
    {
        URLConnection connection = new URL( "https://minecraft-heads.com/scripts/api.php" ).openConnection();
        try
        {
            connection.connect();
            Infinity.logger.info( "Head API connection test was successfully passed." );
        }
        catch ( SSLHandshakeException e )
        {
            Infinity.logger.warn( "Failed connection to Head API. You might be running an outdated java version. Attempting to add Let's Encrypt certificate..." );
            addLetsEncryptCertificate();
        }
    }
}
