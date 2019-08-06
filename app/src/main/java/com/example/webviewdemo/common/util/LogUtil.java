package com.example.webviewdemo.common.util;

import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.webviewdemo.BuildConfig;

import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public class LogUtil {
    
    static {
        if (BuildConfig.DEBUG) {
            Timber.plant(new InternalDebugTree());
        }
    }
    
    private LogUtil() {
        throw new RuntimeException("Can not invoke constructor!");
    }
    
    /**
     * Log a verbose message with optional format args.
     */
    public static void v(String message, Object... args) {
        Timber.v(message, args);
    }
    
    /**
     * Log a verbose exception and a message with optional format args.
     */
    public static void v(Throwable t, String message, Object... args) {
        Timber.v(t, message, args);
    }
    
    /**
     * Log a verbose exception.
     */
    public static void v(Throwable t) {
        Timber.v(t);
    }
    
    /**
     * Log a debug message with optional format args.
     */
    public static void d(String message, Object... args) {
        Timber.d(message, args);
    }
    
    /**
     * Log a debug exception and a message with optional format args.
     */
    public static void d(Throwable t, String message, Object... args) {
        Timber.d(t, message, args);
    }
    
    /**
     * Log a debug exception.
     */
    public static void d(Throwable t) {
        Timber.d(t);
    }
    
    /**
     * Log an info message with optional format args.
     */
    public static void i(String message, Object... args) {
        Timber.i(message, args);
    }
    
    /**
     * Log an info exception and a message with optional format args.
     */
    public static void i(Throwable t, String message, Object... args) {
        Timber.i(t, message, args);
    }
    
    /**
     * Log an info exception.
     */
    public static void i(Throwable t) {
        Timber.i(t);
    }
    
    /**
     * Log a warning message with optional format args.
     */
    public static void w(String message, Object... args) {
        Timber.w(message, args);
    }
    
    /**
     * Log a warning exception and a message with optional format args.
     */
    public static void w(Throwable t, String message, Object... args) {
        Timber.w(t, message, args);
    }
    
    /**
     * Log a warning exception.
     */
    public static void w(Throwable t) {
        Timber.w(t);
    }
    
    /**
     * Log an error message with optional format args.
     */
    public static void e(String message, Object... args) {
        Timber.e(message, args);
    }
    
    /**
     * Log an error exception and a message with optional format args.
     */
    public static void e(Throwable t, String message, Object... args) {
        Timber.e(t, message, args);
    }
    
    /**
     * Log an error exception.
     */
    public static void e(Throwable t) {
        Timber.e(t);
    }
    
    /**
     * Log an assert message with optional format args.
     */
    public static void wtf(String message, Object... args) {
        Timber.wtf(message, args);
    }
    
    /**
     * Log an assert exception and a message with optional format args.
     */
    public static void wtf(Throwable t, String message, Object... args) {
        Timber.wtf(t, message, args);
    }
    
    /**
     * Log an assert exception.
     */
    public static void wtf(Throwable t) {
        Timber.wtf(t);
    }
    
    /**
     * Log at {@code priority} a message with optional format args.
     */
    public static void log(int priority, String message, Object... args) {
        Timber.log(priority, message, args);
    }
    
    /**
     * Log at {@code priority} an exception and a message with optional format args.
     */
    public static void log(int priority, Throwable t, String message, Object... args) {
        Timber.log(priority, t, message, args);
    }
    
    /**
     * Log at {@code priority} an exception.
     */
    public static void log(int priority, Throwable t) {
        Timber.log(priority, t);
    }
    
    /**
     * Set a one-time tag for use on the next logging call.
     */
    public static void tag(String tag) {
        Timber.tag(tag);
    }
    
    /**
     * 具体实现使用Timber.DebugTree，主要为了修改CALL_STACK_INDEX的值
     */
    public static class InternalDebugTree extends Timber.Tree {
        
        private static final int MAX_LOG_LENGTH = 4000;
        private static final int MAX_TAG_LENGTH = 23;
        private static final int CALL_STACK_INDEX = 7;
        private static final Pattern ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$");
        
        protected String createStackElementTag() {
            StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            if (stackTrace.length <= CALL_STACK_INDEX) {
                throw new IllegalStateException(
                        "Synthetic stacktrace didn't have enough elements: are you using proguard?");
            }
            StackTraceElement element = stackTrace[CALL_STACK_INDEX];
            
            String tag = element.getClassName();
            Matcher m = ANONYMOUS_CLASS.matcher(tag);
            if (m.find()) {
                tag = m.replaceAll("");
            }
            tag = tag.substring(tag.lastIndexOf('.') + 1);
            // Tag length limit was removed in API 24.
            if (tag.length() <= MAX_TAG_LENGTH || Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                return tag;
            }
            return tag.substring(0, MAX_TAG_LENGTH);
        }
        
        @Override
        protected void log(int priority, @Nullable String tag, @NotNull String message, @Nullable Throwable t) {
            if (tag == null) {
                tag = createStackElementTag();
            }
            
            if (message.length() < MAX_LOG_LENGTH) {
                if (priority == Log.ASSERT) {
                    Timber.wtf(tag, message);
                } else {
                    Log.println(priority, tag, message);
                }
                return;
            }
            
            // Split by line, then ensure each line can fit into Log's maximum length.
            String part;
            int end;
            for (int i = 0, length = message.length(); i < length; i++) {
                int newline = message.indexOf('\n', i);
                newline = newline != -1 ? newline : length;
                do {
                    end = Math.min(newline, i + MAX_LOG_LENGTH);
                    part = message.substring(i, end);
                    if (priority == Log.ASSERT) {
                        Timber.wtf(tag, part);
                    } else {
                        Log.println(priority, tag, part);
                    }
                    i = end;
                } while (i < newline);
            }
        }
    }
    
}
