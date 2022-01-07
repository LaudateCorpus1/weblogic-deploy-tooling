/*
 * Copyright (c) 2017, 2022, Oracle Corporation and/or its affiliates.  All rights reserved.
 * Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl.
 */
package oracle.weblogic.deploy.yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Map;

import oracle.weblogic.deploy.logging.PlatformLogger;
import oracle.weblogic.deploy.logging.WLSDeployLogFactory;

import org.python.core.PyDictionary;

/**
 * An implementation of the YAML parser/translator that reads the YAML input from an input stream.
 */
public class YamlStreamTranslator extends AbstractYamlTranslator {
    private static final String CLASS = YamlStreamTranslator.class.getName();
    private static final PlatformLogger LOGGER = WLSDeployLogFactory.getLogger("wlsdeploy.yaml");

    private String streamFileName;
    private InputStream yamlStream;
    private Writer yamlOutputWriter;

    /**
     * The constructor.
     *
     * @param streamFileName the name of the file used to create the InputStream (used only for logging purposes)
     * @param yamlStream the input stream
     */
    public YamlStreamTranslator(String streamFileName, InputStream yamlStream) {
        this(streamFileName, yamlStream, false);
    }

    /**
     * The constructor that allows control of ordering.
     *
     * @param streamFileName the name of the file used to create the InputStream (used only for logging purposes)
     * @param yamlStream the input stream
     * @param useOrderedDict whether or not to use an ordered dictionary to maintain the order
     */
    public YamlStreamTranslator(String streamFileName, InputStream yamlStream, boolean useOrderedDict) {
        super(streamFileName, useOrderedDict);
        this.streamFileName = streamFileName;
        this.yamlStream = yamlStream;
        this.yamlOutputWriter = null;
    }

    /**
     * The constructor for writing YAML output.
     *
     * @param streamFileName   the name of the file used to create the OutputStream (used only for logging purposes)
     * @param yamlOutputWriter the Writer to use for writing the YAML output
     */
    public YamlStreamTranslator(String streamFileName, Writer yamlOutputWriter) {
        super(streamFileName, true);
        this.streamFileName = streamFileName;
        this.yamlStream = null;
        this.yamlOutputWriter = yamlOutputWriter;
    }

    /**
     * This method triggers parsing of the YAML and conversion into the Python dictionary.  Note that is closes
     * the input stream when it is finished, making the instance no longer viable.
     *
     * @return the python dictionary corresponding to the YAML input
     * @throws YamlException if an error occurs while reading the input
     */
    @Override
    public PyDictionary parse() throws YamlException {
        final String METHOD = "parse";

        LOGGER.entering(CLASS, METHOD);
        PyDictionary result = null;
        if (yamlStream != null) {
            try {
                result = parseInternal(yamlStream);
            } finally {
                try {
                    yamlStream.close();
                } catch (IOException ioe) {
                    LOGGER.warning("WLSDPLY-18023", ioe, streamFileName, ioe.getLocalizedMessage());
                }
                yamlStream = null;
            }
        }
        LOGGER.exiting(CLASS, METHOD);
        return result;
    }

    public void dump(Map<String, Object> data) throws YamlException {
        final String METHOD = "dump";

        LOGGER.entering(CLASS, METHOD);
        if (yamlOutputWriter != null) {
            try {
                dumpInternal(data, yamlOutputWriter);
            } finally {
                try {
                    yamlOutputWriter.close();
                } catch (IOException ioe) {
                    LOGGER.warning("WLSDPLY-18110", ioe, streamFileName, ioe.getLocalizedMessage());
                }
                yamlOutputWriter = null;
            }
        }
        LOGGER.exiting(CLASS, METHOD);
    }

    @Override
    protected String getClassName() {
        return CLASS;
    }

    @Override
    protected PlatformLogger getLogger() {
        return LOGGER;
    }
}
