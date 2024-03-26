package br.com.core.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DriverFactory {
    private static ThreadLocal<DriverFactory> driverFactory = new ThreadLocal<DriverFactory>();
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<WebDriver>();
    private Properties properties;

    private DriverFactory(){}

    public static DriverFactory getInstance() {
        if (driverFactory.get() == null) {
            DriverFactory instance = new DriverFactory();
            instance.properties = instance.loadProperties();
            driverFactory.set(instance);
        }
        return driverFactory.get();
    }

    private Properties loadProperties(){
        Properties properties = new Properties();
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("config.properties")){
            if(inputStream == null){
                throw new IOException("Falha no local de config.properties");
            }
            properties.load(inputStream);
        } catch (IOException ex){
            throw new RuntimeException("Falha no carregamento de properties para config.properties", ex);
        }
        return properties;
    }

    public void initializeDriver(BrowserType browserType){
        switch (browserType) {
            case CHROME:
                System.setProperty("webdriver.chrome.driver", properties.getProperty("webdriver.chrome.driver"));
                System.out.println("Chrome Driver Path: " + System.getProperty("webdriver.chrome.driver"));
                this.setDriver(new ChromeDriver());
                break;
            case FIREFOX:
                System.setProperty("webdriver.gecko.driver", properties.getProperty("webdriver.gecko.driver"));
                this.setDriver(new FirefoxDriver());
                break;
            case EDGE:
                System.setProperty("webdriver.edge.driver", properties.getProperty("webdriver.edge.driver"));
                this.setDriver(new EdgeDriver());
                break;
            default:
                throw new IllegalArgumentException("Invalid browser type: " + browserType);
        }
    }

    public WebDriver getDriver() {
        return driver.get();
    }

    private void setDriver(WebDriver driver) {
        DriverFactory.driver.set(driver);
    }

    public void removeDriver() {
        WebDriver webDriver = driver.get();
        if (webDriver != null) {
            webDriver.quit();
        }
        driver.remove();
    }

}




