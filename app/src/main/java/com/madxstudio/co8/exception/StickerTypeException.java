package com.madxstudio.co8.exception;

/**
 * Created by Jackie on 8/6/15.
 * @see java.lang.Exception
 * 表情异常类
 * @author Jackie
 * @version 1.0
 */
public class StickerTypeException extends Exception {
    /**
     * Constructs a new {@code Exception} that includes the current stack trace.
     */
    public StickerTypeException() {
        super();
    }

    /**
     * Constructs a new {@code Exception} with the current stack trace and the
     * specified detail message.
     *
     * @param detailMessage the detail message for this exception.
     */
    public StickerTypeException(String detailMessage) {
        super(detailMessage);
    }
}
