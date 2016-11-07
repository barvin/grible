/*******************************************************************************
 * Copyright (c) 2013 - 2014 Maksym Barvinskyi.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     Maksym Barvinskyi - initial API and implementation
 ******************************************************************************/
package org.grible.tests.integration;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.plus.webapp.EnvConfiguration;
import org.eclipse.jetty.plus.webapp.PlusConfiguration;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.FragmentConfiguration;
import org.eclipse.jetty.webapp.MetaInfConfiguration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.eclipse.jetty.webapp.WebXmlConfiguration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import io.github.bonigarcia.wdm.MarionetteDriverManager;

public class BaseTest {
    private Thread webAppThread;
    protected WebDriver browser;

    @BeforeSuite
    public void beforeSuite() {
        MarionetteDriverManager.getInstance().setup();
        Runnable task = () -> {
            Server server = new Server(8123);
            WebAppContext webapp = new WebAppContext();
            webapp.setTempDirectory(new File("target/tmp"));
            webapp.setContextPath("/");
            webapp.setWar("target/grible.war");
            webapp.setConfigurations(new Configuration[] { new AnnotationConfiguration(), new WebXmlConfiguration(),
                    new WebInfConfiguration(), new PlusConfiguration(), new MetaInfConfiguration(),
                    new FragmentConfiguration(), new EnvConfiguration() });
            server.setHandler(webapp);
            try {
                server.start();
                server.join();
            } catch (InterruptedException e) {
                System.out.println("Shutting down the application.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        webAppThread = new Thread(task);
        webAppThread.start();
        try {
            TimeUnit.SECONDS.sleep(7);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @BeforeMethod
    public void beforeAnyTest() {
        browser = new FirefoxDriver();
    }

    @AfterMethod
    public void afterAnyTest() {
        if (browser != null) {
            browser.quit();
        }
    }

    @AfterSuite
    public void afterSuite() {
        if (webAppThread != null && webAppThread.isAlive()) {
            webAppThread.interrupt();
        }
    }
}
