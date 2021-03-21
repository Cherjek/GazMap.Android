package com.gasstation;

import java.lang.Thread.UncaughtExceptionHandler;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import Logging.Logger;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class MMTException implements UncaughtExceptionHandler {
	
	private final DateFormat formatter = new SimpleDateFormat("dd.MM.yy HH:mm");
    private String versionName = "0";
    private int versionCode = 0;
    private final Thread.UncaughtExceptionHandler previousHandler;
    
    private MMTException(Context context, boolean chained) {

        PackageManager mPackManager = context.getPackageManager();
        PackageInfo mPackInfo;
        try {
            mPackInfo = mPackManager.getPackageInfo(context.getPackageName(), 0);
            versionName = mPackInfo.versionName;
            versionCode = mPackInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // ignore
        }
        if(chained)
            previousHandler = Thread.getDefaultUncaughtExceptionHandler();
        else
            previousHandler = null;
    }

    public static MMTException inContext(Context context) {
        return new MMTException(context, true);
    }
    
    public static MMTException reportOnlyHandler(Context context) {
        return new MMTException(context, false);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable exception) {
    	        
        final Date dumpDate = new Date(System.currentTimeMillis());
        
        StringBuilder reportBuilder = new StringBuilder();
        reportBuilder
                .append("\n\n\n")
                .append(formatter.format(dumpDate)).append("\n")
                .append(String.format("Version: %s (%d)\n", versionName, versionCode))
                .append(thread.toString()).append("\n");
        processThrowable(exception, reportBuilder);

        Logger.writeErrorToLog(reportBuilder.toString(), exception);
    	
        if(previousHandler != null)
            previousHandler.uncaughtException(thread, exception);
    }

    private void processThrowable(Throwable exception, StringBuilder builder) {
        if(exception == null)
            return;
        StackTraceElement[] stackTraceElements = exception.getStackTrace();
        builder
                .append("Exception: ").append(exception.getClass().getName()).append("\n")
                .append("Message: ").append(exception.getMessage()).append("\nStacktrace:\n");
        for(StackTraceElement element : stackTraceElements) {
            builder.append("\t").append(element.toString()).append("\n");
        }
        processThrowable(exception.getCause(), builder);
    }
}
