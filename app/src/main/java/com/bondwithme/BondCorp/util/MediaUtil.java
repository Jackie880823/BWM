package com.madxstudio.co8.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by wing on 15/9/1.
 */
public class MediaUtil {

    static int bitRate = 500000;
    static int frameRate = 30;
    static int width = 640;
    static int height = 480;
    static String mimeType = MediaFormat.MIMETYPE_VIDEO_MPEG4;
    private static MediaCodec decoder;
    private static MediaCodec encoder;
    private static MediaFormat mOutputFormat; // member variable
    private static MediaMuxer mMuxer;
    private static int mTrackIndex;
    private static boolean mMuxerStarted;
    /**use in low then api 18*/
    static ByteBuffer[] encoderInputBuffers;
    /**use in low then api 18*/
    static ByteBuffer[] encoderOutputBuffers;
    /**use in low then api 18*/
    static ByteBuffer[] decoderInputBuffers = null;
    /**use in low then api 18*/
    static ByteBuffer[] decoderOutputBuffers = null;
    static final long kTimeOutUs = 5000;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void encodeFile2Mp4(Context context, String fromPath, String outPath) throws IOException {
        MediaExtractor extractor = new MediaExtractor();
        extractor.setDataSource(fromPath);
        int numTracks = extractor.getTrackCount();
        for (int i = 0; i < numTracks; ++i) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("video/")) {
                extractor.selectTrack(i);
                MediaCodecInfo mediaCodecInfo = getMediaCodecInfo(mime);
                if (mediaCodecInfo != null) {
                    initDecoder(format, mime);
                    initEncoder(mime, outPath, mediaCodecInfo);
                } else {
                    //TODO format not support
                }
                break;
            }
        }
//        ByteBuffer inputBuffer = ByteBuffer.allocate(1024 * 10);

        int sampleSize;
        int inputBufIndex = decoder.dequeueInputBuffer(kTimeOutUs);
        ByteBuffer inputBuffer;
        boolean sawInputEOS = false;
        if (inputBufIndex >= 0) {
            do {
//                inputBuffer = getDecodeInputBufferByIndex(inputBufIndex);
                inputBuffer = getEncodeInputBufferByIndex(inputBufIndex);
                sampleSize = extractor.readSampleData(inputBuffer, 0);
                long presentationTimeUs = 0;
                if (sampleSize < 0) {
                    sawInputEOS = true;
                    sampleSize = 0;
                } else {
                    presentationTimeUs = extractor.getSampleTime();
                }
//                read2MediaCodecFromFile(sampleSize, presentationTimeUs);
                //decode
                encoder.queueInputBuffer(inputBufIndex, 0, sampleSize, presentationTimeUs, sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);
//                decoder.queueInputBuffer(inputBufIndex, 0, sampleSize, presentationTimeUs, sawInputEOS ? MediaCodec.BUFFER_FLAG_END_OF_STREAM : 0);
                if (!sawInputEOS) {
                    extractor.advance();
                    inputBufIndex = encoder.dequeueInputBuffer(kTimeOutUs);
//                    inputBufIndex = decoder.dequeueInputBuffer(kTimeOutUs);
                }
            } while (inputBufIndex >= 0&&!sawInputEOS);
        }
        extractor.release();


        write2FileFromMediaCodec(outPath);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static ByteBuffer getDecodeInputBufferByIndex(int inputBufIndex) {
        ByteBuffer inputBuffer;
        if (SDKUtil.IS_JB_MR2) {
            inputBuffer = decoder.getInputBuffer(inputBufIndex);
        } else {
            inputBuffer = decoderInputBuffers[inputBufIndex];
        }
        return inputBuffer;
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static ByteBuffer getDecodeOutputBufferByIndex(int inputBufIndex) {
        ByteBuffer inputBuffer;
        if (SDKUtil.IS_JB_MR2) {
            inputBuffer = decoder.getOutputBuffer(inputBufIndex);
        } else {
            inputBuffer = decoderInputBuffers[inputBufIndex];
        }
        return inputBuffer;
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static ByteBuffer getEncodeInputBufferByIndex(int inputBufIndex) {
        ByteBuffer inputBuffer;
        if (SDKUtil.IS_JB_MR2) {
            inputBuffer = encoder.getInputBuffer(inputBufIndex);
        } else {
            inputBuffer = encoderInputBuffers[inputBufIndex];
        }
        return inputBuffer;
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static ByteBuffer getEncodeOutputBufferByIndex(int inputBufIndex) {
        ByteBuffer inputBuffer;
        if (SDKUtil.IS_JB_MR2) {
            inputBuffer = encoder.getOutputBuffer(inputBufIndex);
        } else {
            inputBuffer = encoderOutputBuffers[inputBufIndex];
        }
        return inputBuffer;
    }

    private static void read2MediaCodecFromFile(int sampleSize, long sampleTime) {
//        for (;;) {
//            int inputBufferId = decoder.dequeueInputBuffer(kTimeOutUs);
//            if (inputBufferId >= 0) {
//
//                if (SDKUtil.IS_L) {
//                    do {
//                        outputStream.write(encoder.getInputBuffer(inputBufferId).array());
//                        index = encoder.dequeueOutputBuffer(info, kTimeOutUs);
//                    } while (index >= 0);
//                } else {
//                    ByteBuffer[]  byteBuffers = encoder.getInputBuffers();
//                    for (ByteBuffer byteBuffer:byteBuffers) {
//                        outputStream.write(byteBuffer.array());
//                    }
//                }
//
////                ByteBuffer inputBuffer = decoder.getInputBuffer(inputBufferId);
//                // fill inputBuffer with valid data
//                decoder.queueInputBuffer(inputBufferId, sampleSize,0,sampleTime,0);
//            }else{
//                break;
//            }
//            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
//            int outputBufferId = decoder.dequeueOutputBuffer(info,kTimeOutUs);
//            if (outputBufferId >= 0) {
//                ByteBuffer outputBuffer = decoder.getOutputBuffer(outputBufferId);
//                MediaFormat bufferFormat = decoder.getOutputFormat(outputBufferId); // option A
//                // bufferFormat is identical to outputFormat
//                // outputBuffer is ready to be processed or rendered.
//                decoder.releaseOutputBuffer(outputBufferId, false);
//            } else if (outputBufferId == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//                // Subsequent data will conform to new format.
//                // Can ignore if using getOutputFormat(outputBufferId)
//                mOutputFormat = decoder.getOutputFormat(); // option B
//            }
//        }
//        decoder.stop();
//        decoder.release();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void write2FileFromMediaCodec(String outPath) throws IOException {


        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
//        int inputBufIndex = decoder.dequeueOutputBuffer(info,kTimeOutUs);
//        ByteBuffer inputBuffer;
//        boolean sawInputEOS = false;
//        if (inputBufIndex >= 0) {
//            do {
//                inputBuffer = getEncodeOutputBufferByIndex(inputBufIndex);
//                int inputBufferIndex = encoder.dequeueInputBuffer(0);
//                if (inputBufferIndex >= 0) {
//                    encoder.queueInputBuffer(inputBufferIndex, 0, inputBuffer.array().length, 0, 0);
//                    encoderInputBuffers[inputBufferIndex] = inputBuffer;
//                } else {
//                    return;
//                }
//
//            } while (inputBufIndex >= 0&&!sawInputEOS);
//        }


        int outputIndex = encoder.dequeueOutputBuffer(info, kTimeOutUs);
        File file = new File(outPath);
//        File file = new File(FileUtil.getSaveRootPath(context, true).toString() + System.currentTimeMillis() + ".mp4");
        FileOutputStream outputStream = new FileOutputStream(file);
        if (SDKUtil.IS_L) {
            do {
                outputStream.write(encoder.getInputBuffer(outputIndex).array());
                outputIndex = encoder.dequeueOutputBuffer(info, kTimeOutUs);
            } while (outputIndex >= 0);
        } else {
            ByteBuffer[] byteBuffers = encoder.getInputBuffers();
            for (ByteBuffer byteBuffer : byteBuffers) {
                outputStream.write(byteBuffer.array());
            }
        }
        encoder.stop();
        encoder.release();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static MediaCodecInfo getMediaCodecInfo(String mimeType) {
        MediaCodecInfo codecInfo = null;
        if (SDKUtil.IS_L) {
            MediaCodecInfo[] codecInfos = new MediaCodecList(MediaCodecList.REGULAR_CODECS).getCodecInfos();
            // Find a code that supports the mime type
            for (MediaCodecInfo info : codecInfos) {
                if (!info.isEncoder()) {
                    continue;
                }
                String[] types = info.getSupportedTypes();
                boolean found = false;
                for (int j = 0; j < types.length && !found; j++) {
                    if (types[j].equals(mimeType))
                        found = true;
                }
                if (!found)
                    continue;
                codecInfo = info;
            }
        } else {
            int numCodecs = MediaCodecList.getCodecCount();
            for (int i = 0; i < numCodecs && codecInfo == null; i++) {
                MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
                if (!info.isEncoder()) {
                    continue;
                }
                String[] types = info.getSupportedTypes();
                boolean found = false;
                for (int j = 0; j < types.length && !found; j++) {
                    if (types[j].equals(mimeType))
                        found = true;
                }
                if (!found)
                    continue;
                codecInfo = info;
            }
        }

        if (codecInfo != null)
            LogUtil.d("", "Found " + codecInfo.getName() + " supporting " + mimeType);
        return codecInfo;
    }

    private static void initDecoder(MediaFormat format, String mime) throws IOException {
        decoder = MediaCodec.createDecoderByType(mime);
        decoder.configure(format, null, null, 0);
        decoder.start();
//        decoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
//        mOutputFormat = decoder.getOutputFormat(); // option B

        decoderInputBuffers = decoder.getInputBuffers();
        decoderOutputBuffers = decoder.getInputBuffers();
        //TODO
//        // wait for processing to complete
//        decoder.stop();
//        decoder.release();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static void initEncoder(String mime, final String outputPath, MediaCodecInfo codecInfo) throws IOException {
        encoder = MediaCodec.createEncoderByType(mime);
//        encoder = MediaCodec.createByCodecName(codecInfo.getName());
        // Determine width, height and slice sizes
        if (codecInfo.getName().equals("OMX.TI.DUCATI1.VIDEO.H264E")) {
            // This codec doesn't support a width not a multiple of 16,
            // so round down.
            width &= ~15;
        }
        int stride = width;
        int sliceHeight = height;
        if (codecInfo.getName().startsWith("OMX.Nvidia.")) {
            stride = (stride + 15) / 16 * 16;
            sliceHeight = (sliceHeight + 15) / 16 * 16;
        }

        MediaFormat inputFormat = MediaFormat.createVideoFormat(mime, width, height);
        inputFormat.setInteger(MediaFormat.KEY_BIT_RATE, bitRate);
        inputFormat.setInteger(MediaFormat.KEY_FRAME_RATE, frameRate);
        inputFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, getColorFormat(codecInfo, mime));
        inputFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 10);

        inputFormat.setInteger("stride", stride);
        inputFormat.setInteger("slice-height", sliceHeight);
        encoder.configure(inputFormat, null /* surface */, null /* crypto */, MediaCodec.CONFIGURE_FLAG_ENCODE);
        encoder.start();

        // Create a MediaMuxer.  We can't add the video track and start() the muxer here,
        // because our MediaFormat doesn't have the Magic Goodies.  These can only be
        // obtained from the encoder after it has started processing data.
        //
        // We're not actually interested in multiplexing audio.  We just want to convert
        // the raw H.264 elementary stream we get from MediaCodec into a .mp4 file.
        if (SDKUtil.IS_JB_MR2) {
            try {
                mMuxer = new MediaMuxer(outputPath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            } catch (IOException ioe) {
                throw new RuntimeException("MediaMuxer creation failed", ioe);
            }

            mTrackIndex = -1;
            mMuxerStarted = false;
        }
    }

//    /**
//     * Releases encoder resources.  May be called after partial / failed initialization.
//     */
//    private void releaseEncoder() {
//        if (VERBOSE) Log.d(TAG, "releasing encoder objects");
//        if (mEncoder != null) {
//            mEncoder.stop();
//            mEncoder.release();
//            mEncoder = null;
//        }
//        if (mInputSurface != null) {
//            mInputSurface.release();
//            mInputSurface = null;
//        }
//        if (mMuxer != null) {
//            mMuxer.stop();
//            mMuxer.release();
//            mMuxer = null;
//        }
//    }

    private static int getColorFormat(MediaCodecInfo codecInfo, String mime) {
        // Find a color profile that the codec supports
        int colorFormat = 0;
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mime);
        for (int i = 0; i < capabilities.colorFormats.length && colorFormat == 0; i++) {
            int format = capabilities.colorFormats[i];
            switch (format) {
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
                case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
                case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
                    colorFormat = format;
                    break;
                default:
                    LogUtil.d("", "Skipping unsupported color format " + format);
                    break;
            }
        }

        LogUtil.d("", "Using color format " + colorFormat);
        return colorFormat;
    }


    /**
     * 音频
     *
     * @param codec
     * @param bufferId
     * @param channelIx
     * @return
     */
    short[] getSamplesForChannel(MediaCodec codec, int bufferId, int channelIx) {
//        ByteBuffer outputBuffer = codec.getOutputBuffer(bufferId);
//        MediaFormat format = codec.getOutputFormat(bufferId);
//        ShortBuffer samples = outputBuffer.order(ByteOrder.nativeOrder()).asShortBuffer();
//        int numChannels = formet.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
//        if (channelIx < 0 || channelIx >= numChannels) {
//            return null;
//        }
//        short[] res = new short[samples.remaining() / numChannels];
//        for (int i = 0; i < res.length; ++i) {
//            res[i] = samples.get(i * numChannels + channelIx);
//        }
//        return res;
        return null;
    }
}
