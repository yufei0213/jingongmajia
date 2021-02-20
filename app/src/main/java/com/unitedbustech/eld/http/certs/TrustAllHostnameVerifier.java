package com.unitedbustech.eld.http.certs;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

/**
 * @author yufei0213
 * @date 2018/3/16
 * @description TrustAllHostnameVerifier
 */
public class TrustAllHostnameVerifier implements HostnameVerifier {

    @Override
    public boolean verify(String hostname, SSLSession session) {
        return true;
    }
}
