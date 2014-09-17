package org.concordion.ext.driver.web;

import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.events.EventFiringWebDriver;

/**
 * Manages the browser session.
 */
public class Browser {
	private WebDriver driver;

	public Browser() {
		DesiredCapabilities capabilities = DesiredCapabilities.firefox();

		final Proxy proxy = new Proxy();
		proxy.setProxyType(Proxy.ProxyType.MANUAL);
		proxy.setHttpProxy("www-proxy:80");
		proxy.setFtpProxy("www-proxy:80");
		proxy.setSslProxy("www-proxy:80");
		proxy.setNoProxy("");

		capabilities.setCapability(CapabilityType.PROXY, proxy);

		driver = new FirefoxDriver(capabilities);
	}

	public void quit() {
		driver.quit();
	}

	public void addLogger() {
		EventFiringWebDriver efwd = new EventFiringWebDriver(driver);
		efwd.register(new SeleniumEventLogger());
		driver = efwd;
	}

	public WebDriver getDriver() {
		return driver;
	}

}
