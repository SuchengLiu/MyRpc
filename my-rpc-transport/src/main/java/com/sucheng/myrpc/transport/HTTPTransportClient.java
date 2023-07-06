package com.sucheng.myrpc.transport;

import com.sucheng.myrpc.Peer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 基于http的客户端实现类
 */
public class HTTPTransportClient implements TransportClient{
    private String url;

    @Override
    public void connect(Peer peer) {
        this.url = "http://" + peer.getHost() + ":" + peer.getPort();
    }

    @Override
    public InputStream write(InputStream data) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setUseCaches(false);
            urlConnection.setRequestMethod("POST");

            urlConnection.connect();
            IOUtils.copy(data, urlConnection.getOutputStream());

            int resCode = urlConnection.getResponseCode();
            if(resCode == HttpURLConnection.HTTP_OK) {
                return urlConnection.getInputStream();
            } else {
                return urlConnection.getErrorStream();
            }

        } catch (IOException e) {
            throw new IllegalStateException();
        }
    }

    @Override
    public void close() {

    }
}
