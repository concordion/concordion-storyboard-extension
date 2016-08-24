package org.concordion.ext;

import org.concordion.logback.LoggingListener;

import ch.qos.logback.classic.spi.ILoggingEvent;

public class Listen extends LoggingListener {

	@Override
	public String[] getFilterMarkers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void append(ILoggingEvent eventObject) {
		// TODO Auto-generated method stub
		
	}

}
