package com.inuker.library.encoder;

import android.content.Context;
import android.graphics.ImageFormat;

import com.inuker.library.utils.LogUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by liwentian on 2017/10/31.
 */

public class MovieEncoder1 extends BaseMovieEncoder {

    private YUVProgram mYUVProgram;
    private ByteBuffer mYUVBuffer;

    public MovieEncoder1(Context context, int width, int height) {
        super(context, width, height);
    }

    @Override
    public void onPrepareEncoder() {
        mYUVProgram = new YUVProgram(mContext, mWidth, mHeight);
        mYUVBuffer = ByteBuffer.allocateDirect(mWidth * mHeight * ImageFormat.getBitsPerPixel(ImageFormat.NV21) / 8)
                .order(ByteOrder.nativeOrder());
    }

    @Override
    public void onFrameAvailable(Object object, long timestamp) {
        byte[] data = (byte[]) object;

        synchronized (mYUVBuffer) {
            mYUVBuffer.position(0);
            int cap = mYUVBuffer.capacity();
            int len = data.length;
            LogUtils.v(String.format("%d- %d", mYUVBuffer.capacity(), data.length));
            mYUVBuffer.put(data);
        }

        mHandler.sendMessage(mHandler.obtainMessage(MSG_FRAME_AVAILABLE,
                (int) (timestamp >> 32), (int) timestamp));
    }

    @Override
    public void onFrameAvailable() {
        mYUVProgram.useProgram();

        synchronized (mYUVBuffer) {
            mYUVProgram.setUniforms(mYUVBuffer.array());
        }

        mYUVProgram.draw();
    }
}
