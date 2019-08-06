package com.example.webviewdemo.net.response;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

public class ProgressResponseBody extends ResponseBody {
    
    public interface ProgressListener {
        
        void update(long bytesRead, long contentLength, boolean done);
        
    }
    
    private final ResponseBody mResponseBody;
    private final ProgressListener mProgressListener;
    private BufferedSource mBufferedSource;
    
    public ProgressResponseBody(ResponseBody responseBody, ProgressListener listener) {
        mResponseBody = responseBody;
        mProgressListener = listener;
    }
    
    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }
    
    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }
    
    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return mBufferedSource;
    }
    
    private Source source(Source source) {
        return new ForwardingSource(source) {
            
            long totalBytesRead = 0L;
            
            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                mProgressListener.update(totalBytesRead, mResponseBody.contentLength(), bytesRead == -1);
                return bytesRead;
            }
        };
    }
}
