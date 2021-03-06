package de.micromata.merlin.server;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Configuration {
    private static Logger log = LoggerFactory.getLogger(Configuration.class);
    private final static String[] SUPPORTED_LANGUAGES = {"en", "de"};
    private static String applicationHome;

    private int port;
    private boolean showTestData = true;
    private boolean webDevelopmentMode = false;
    private List<ConfigurationTemplatesDir> templatesDirs;
    private boolean templatesDirModified = false;

    public static Configuration getDefault() {
        return ConfigurationHandler.getDefaultConfiguration();
    }

    public static String[] getSupportedLanguages() {
        return SUPPORTED_LANGUAGES;
    }

    public static String getApplicationHome() {
        if (applicationHome == null) {
            applicationHome = System.getProperty("applicationHome");
            if (StringUtils.isBlank(applicationHome)) {
                applicationHome = System.getProperty("user.dir");
                log.info("applicationHome is not given as JVM   parameter. Using current working dir (OK for start in IDE): " + applicationHome);
            }
        }
        return applicationHome;
    }

    public void resetModifiedFlag() {
        templatesDirModified = false;
    }

    @Transient
    public boolean isTemplatesDirModified() {
        return templatesDirModified;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return true, if test templates will be shown and usable
     */
    public boolean isShowTestData() {
        return showTestData;
    }

    public void setShowTestData(boolean showTestData) {
        this.showTestData = showTestData;
    }

    /**
     * If true, CrossOriginFilter will be set.
     */
    public boolean isWebDevelopmentMode() {
        return webDevelopmentMode;
    }

    public void setWebDevelopmentMode(boolean webDevelopmentMode) {
        this.webDevelopmentMode = webDevelopmentMode;
    }

    public List<ConfigurationTemplatesDir> getTemplatesDirs() {
        return templatesDirs;
    }

    public void setTemplatesDirs(List<ConfigurationTemplatesDir> templatesDirs) {
        if (!Objects.equals(this.templatesDirs, templatesDirs)) {
            templatesDirModified = true;
        }
        this.templatesDirs = templatesDirs;
    }

    public void addTemplatesDir(String templateDir) {
        templatesDirModified = true;
        addTemplatesDir(templateDir, false);
    }

    public void addTemplatesDir(String templateDir, boolean recursive) {
        if (StringUtils.isBlank(templateDir)) {
            return;
        }
        if (templatesDirs == null) {
            templatesDirs = new ArrayList<>();
        }
        templatesDirModified = true;
        templatesDirs.add(new ConfigurationTemplatesDir().setDirectory(templateDir).setRecursive(recursive));
    }

    public void copyFrom(Configuration other) {
        this.port = other.port;
        this.showTestData = other.showTestData;
        this.webDevelopmentMode = other.webDevelopmentMode;
        if (!Objects.equals(this.templatesDirs, other.templatesDirs)) {
            templatesDirModified = true;
        }
        this.templatesDirs = other.templatesDirs;
    }
}
