package com.example.freshkeeper.media;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MediaService {

    private static final String TAG = "MediaService";
    private MediaExtractor extractor;
    private MediaCodec codec;

    public MediaService() {
        extractor = new MediaExtractor();
    }

    public void playMedia(String videoFilePath, Context context) {
        try {
            extractor.setDataSource(videoFilePath);  // 비디오 파일 경로 설정
            MediaFormat format = extractor.getTrackFormat(0);  // 첫 번째 트랙 포맷 가져오기
            String mimeType = format.getString(MediaFormat.KEY_MIME);
            codec = MediaCodec.createDecoderByType(mimeType);
            codec.configure(format, null, null, 0);  // 코덱 구성
            codec.start();

            ByteBuffer[] inputBuffers = codec.getInputBuffers();
            ByteBuffer[] outputBuffers = codec.getOutputBuffers();
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

            int inputIndex = codec.dequeueInputBuffer(10000);
            if (inputIndex >= 0) {
                ByteBuffer inputBuffer = inputBuffers[inputIndex];
                int sampleSize = extractor.readSampleData(inputBuffer, 0);
                if (sampleSize < 0) {
                    Log.d(TAG, "End of stream");
                } else {
                    codec.queueInputBuffer(inputIndex, 0, sampleSize, extractor.getSampleTime(), 0);
                    extractor.advance();
                }
            }

            int outputIndex = codec.dequeueOutputBuffer(bufferInfo, 10000);
            if (outputIndex >= 0) {
                ByteBuffer outputBuffer = outputBuffers[outputIndex];
                codec.releaseOutputBuffer(outputIndex, true);  // 출력 버퍼 해제
            }

        } catch (IOException e) {
            Log.e(TAG, "Failed to play media: " + e.getMessage());
        }
    }

    public void stopMedia() {
        if (codec != null) {
            codec.stop();
            codec.release();
            codec = null;
        }
    }

    public void releaseExtractor() {
        if (extractor != null) {
            extractor.release();
        }
    }
}
