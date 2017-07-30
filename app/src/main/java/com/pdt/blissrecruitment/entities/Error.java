package com.pdt.blissrecruitment.entities;

import android.support.annotation.IntDef;

import com.pdt.blissrecruitment.Util.Constants;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import okhttp3.ResponseBody;

import static com.pdt.blissrecruitment.entities.Error.Type.HTTP;
import static com.pdt.blissrecruitment.entities.Error.Type.NO_NETWORK;
import static com.pdt.blissrecruitment.entities.Error.Type.SERVER_ERROR;


public class Error {
    /**
     * Constructor
     *
     * @param builder Error builder
     */
    private Error(ErrorBuilder builder) {
        this(builder.errorType, builder.code, builder.message);
    }

    /**
     * Constructor
     *
     * @param builder Http error builder
     */
    private Error(HttpErrorBuilder builder) {
        this(builder.errorType, builder.code, builder.message);
    }

    /**
     * Constructor
     *
     * @param builder no network error builder
     */
    private Error(NoNetworkErrorBuilder builder) {
        this(builder.errorType, builder.code, builder.message);
    }

    /**
     * Constructor
     *
     * @param errorType The error type
     * @param code      the error code
     * @param message   the error message
     */
    private Error(int errorType, String code, String message) {
        this.errorType = errorType;
        this.code = code;
        this.message = message;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({NO_NETWORK, HTTP, SERVER_ERROR})
    public @interface Type {
        int NO_NETWORK = 1;
        int HTTP = 2;
        int SERVER_ERROR = 3;
    }

    private @Type final int errorType;
    private final String code;
    private final String message;

    /**
     * getter for error message
     *
     * @return error message
     */
    public String getErrorMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        switch (errorType) {
            case HTTP:
                stringBuilder.append("http");
                stringBuilder.append(" ");
                stringBuilder.append(code);
                stringBuilder.append(" ");
                stringBuilder.append(message);
                break;

            case NO_NETWORK:
            case SERVER_ERROR:
            default:
                stringBuilder.append(message);
                break;
        }

        return stringBuilder.toString();
    }

    /**
     * getter for error type
     *
     * @return error type
     */
    public
    @Type
    int getErrorType() {
        return errorType;
    }

    public static class ErrorBuilder extends BaseErrorBuilder {

        public ErrorBuilder(int errorType) {
            this.errorType = errorType;
        }

        public ErrorBuilder code(String code) {
            this.code = code;
            return this;
        }

        public ErrorBuilder message(String message) {
            this.message = message;
            return this;
        }

        public Error build() {
            return new Error(this);
        }
    }

    /**
     * Builder for http error
     */
    public static class HttpErrorBuilder extends BaseErrorBuilder {

        public HttpErrorBuilder() {
            this.errorType = HTTP;
        }

        public HttpErrorBuilder code(String code) {
            this.code = code;
            return this;
        }

        public HttpErrorBuilder message(ResponseBody responseBody) {
            String message = null;
            try {
                message = responseBody.string();
            } catch (IOException e) {
                // silently ignore
            }
            this.message = message;
            return this;
        }

        public Error build() {
            return new Error(this);
        }
    }

    /**
     * builder for no network error
     */
    public static class NoNetworkErrorBuilder extends BaseErrorBuilder {
        public NoNetworkErrorBuilder() {
            this.errorType = NO_NETWORK;
            this.code = Constants.Error.NO_NETWORK_CODE;
            this.message = Constants.Error.NO_NETWORK_MESSAGE;
        }

        public Error build() {
            return new Error(this);
        }
    }

    public static abstract class BaseErrorBuilder {
        @Error.Type
        int errorType;
        String code;
        String message;

    }

    @Override
    public String toString() {
        return "Error{" +
                "errorType=" + errorType +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
