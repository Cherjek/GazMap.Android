package Logging;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.gasstation.common.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.os.Handler;
import android.os.Process;

public class Logger {
	
	private static File logFile;
	private static File logFileSync;
	private static Context applicationContext;
	private static Handler handler;
	
	private final static DateFormat fileFormatter = new SimpleDateFormat("dd-MM-yy");
	private static boolean errorOccurred;			
	
	public enum TagEnum {
		ERROR,
		WARNING;
	}
	
	public static void init(File root, Context applicationContext, Handler handler) {
		
		String stacktraceDir = String.format("/Android/data/%s/files/", applicationContext.getPackageName());
		final Date dumpDate = new Date(System.currentTimeMillis());
		logFile = new File(root.getPath() + stacktraceDir,
                String.format(
                        "stacktrace-%s.txt",
                        fileFormatter.format(dumpDate)));		
		
		stacktraceDir = String.format("/Android/data/%s/files/logsync/", applicationContext.getPackageName());
		logFileSync = new File(root.getPath() + stacktraceDir,
                String.format(
                        "stacktrace-%s.txt",
                        fileFormatter.format(dumpDate)));
		
		Logger.applicationContext = applicationContext;
		Logger.handler = handler;
	}
	
	private static void appendErrorToLogFile(String message) {    
		
		BufferedWriter buffer = null;
		try {
			if (logFile != null) {
				File dumpdir = logFile.getParentFile();
				boolean dirReady = dumpdir.isDirectory() || dumpdir.mkdirs();
				if (dirReady) {
	               
					buffer = new BufferedWriter(new FileWriter(logFile, true));
					buffer.append(Utils.getTime() + "	");
					buffer.append(message);
					buffer.append("\n----------------------------------------------------------------------------------------------\n\n");
	            }
			}
		}
		catch (IOException e) {
	      
		  Log.println(Log.ASSERT, "ERROR", "Error on write to log file. Error: " + e);
		  Log.println(Log.ASSERT, "ERROR", "\n----------------------------------------------------------------------------------------------\n");
		  Log.e("ERROR", "Error on write to log file. Error: " + e);
	      Log.e("ERROR", "\n----------------------------------------------------------------------------------------------\n");	      
	      
	      e.printStackTrace();
	   }
	   finally {
		   
		   if (buffer != null) {
			   try {
				   buffer.close();
			   }
			   catch (IOException e) {
			   }
		   }
	   }
	}	
	
	public static String getStackTrace(Throwable t) {
	
		String stackTrace = null;
		StackTraceElement[] elements = t.getStackTrace();
		
		for (int i = 0; i < elements.length; i++) {
			stackTrace += elements[i].toString() + "\n";
		}
		
		return stackTrace;
	}
	
	public static void writeErrorToLog(String message) { 
		writeToLog(TagEnum.ERROR, message);
	}
	
	public static void writeErrorToLog(String message, Throwable e) {
		writeToLog(TagEnum.ERROR, message, e);
	}	
	
	public static void writeWarningToLog(String message) {
		writeToLog(TagEnum.WARNING, message);
	}
	
	public static void writeWarningToLog(String message, Throwable e) {
		writeToLog(TagEnum.WARNING, message, e);
	}
	
	private static void writeToLog(TagEnum tag, String message, Throwable e) {
		writeToLog(tag, message + "\n(Message: " + e.getMessage() + "\nFull information: " + e + "\nStack trace: " + Logger.getStackTrace(e) + ")");
	}

	public static void writeToLog(TagEnum tag, String message) {
				
		if (errorOccurred) {
			return;
		}
		
		Log.println(Log.ASSERT, tag.toString(), message);
		Log.println(Log.ASSERT, tag.toString(), "\n----------------------------------------------------------------------------------------------\n");
	
		if (BuildOptions.SEND_BUGS && !(tag == TagEnum.WARNING && BuildOptions.SEND_ONLY_ERRORS)) {
			sendBug(tag, message);		
		}
		
		if (tag.equals(TagEnum.ERROR)) {
			
			errorOccurred = true;
			
			Log.e(TagEnum.ERROR.toString(), message);
			Log.e(TagEnum.ERROR.toString(), "\n----------------------------------------------------------------------------------------------\n");
			
			if (BuildOptions.WRITE_LOG_FILE) {
				appendErrorToLogFile(message);
			}
			
			if (BuildOptions.SHOW_ERROR_DIALOG) {
				showErrorDialog(message);
			}
				
			//throw new UncaughtException();
		}
		else {
			Log.i(tag.toString(), message);
			Log.i(tag.toString(), "\n----------------------------------------------------------------------------------------------\n");
		}
	}
	
	public static void sendBug(TagEnum tag, String message) {
		
		
	}	
	
	private static void showErrorDialog(final String message) {
		
		handler.post(new Runnable() {
    		
    		public void run() {
		
    			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(applicationContext);
				dialogBuilder.setMessage(message)
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
		
					public void onClick(DialogInterface dialog, int which) {
						Process.killProcess(Process.myPid());
					}
				}).setCancelable(false).show();
    		}
		});
    }
	
	public static String readSyncLog() {
		StringBuilder sb = new StringBuilder();
		BufferedReader buffer = null;
		try {
			File dumpdir = logFileSync.getParentFile();
			boolean dirReady = dumpdir.isDirectory() || dumpdir.mkdirs();
			if (dirReady) {
               
				buffer = new BufferedReader(new FileReader(logFileSync));	
				String str;
				while ((str = buffer.readLine()) != null) {		            
					sb.append(str);
					sb.append("\n"); 
		        }
            }
		}
		catch (IOException e) {
	      
			Log.println(Log.ASSERT, "ERROR", "Error on write to log file. Error: " + e);
			Log.println(Log.ASSERT, "ERROR", "\n----------------------------------------------------------------------------------------------\n");
			Log.e("ERROR", "Error on write to log file. Error: " + e);
			Log.e("ERROR", "\n----------------------------------------------------------------------------------------------\n");	      
	      
	      	e.printStackTrace();
		}
		finally {
		   
		   	if (buffer != null) {
			   	try {
				   	buffer.close();
			   	}
			   	catch (IOException e) {
			   	}
		   	}
	   	}
		return sb.toString();
	}
	
	public static void writeSyncLog(List<String> messages) {
		
		BufferedWriter buffer = null;
		try {
			File dumpdir = logFileSync.getParentFile();
			boolean dirReady = dumpdir.isDirectory() || dumpdir.mkdirs();
			if (dirReady) {
               
				buffer = new BufferedWriter(new FileWriter(logFileSync, true));		
				for(String message: messages)
					buffer.append(message + "\n");
            }
		}
		catch (IOException e) {
	      
		  Log.println(Log.ASSERT, "ERROR", "Error on write to log file. Error: " + e);
		  Log.println(Log.ASSERT, "ERROR", "\n----------------------------------------------------------------------------------------------\n");
		  Log.e("ERROR", "Error on write to log file. Error: " + e);
	      Log.e("ERROR", "\n----------------------------------------------------------------------------------------------\n");	      
	      
	      e.printStackTrace();
	   }
	   finally {
		   
		   if (buffer != null) {
			   try {
				   buffer.close();
			   }
			   catch (IOException e) {
			   }
		   }
	   }
	}
	
	public static class UncaughtException extends RuntimeException {
	}
}
