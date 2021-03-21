package ru.zch.gasstation.log;

import org.jboss.logging.Logger;

public final class Log {
	private static final String LOGGER_NAME = "gasstation";
	
	private static Logger getLogger(){
		return Logger.getLogger(LOGGER_NAME);
	}
	
	public static void i(String msg){
		getLogger().info(msg);
	}
	public static void iF(String msg, Object... params){
		getLogger().infof(msg, params);
	}
	
	public static void w(String msg){
		getLogger().warn(msg);
	}
	public static void wF(String msg, Object... params){
		getLogger().warnf(msg, params);
	}
	
	public static void e(String msg){
		getLogger().error(msg);
	}
	public static void eF(String msg, Object... params){
		getLogger().errorf(msg, params);
	}
	
	public static void d(String msg){
		getLogger().debug(msg);
	}
	public static void dF(String msg, Object... params){
		getLogger().debugf(msg, params);
	}
}
