package com.legstar.xsd.def;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.legstar.xsd.InvalidParameterException;
import com.legstar.xsd.InvalidXsdException;

/**
 * ANT Task for XML schema to COBOL translation.
 * 
 */
public class Xsd2CobTask extends Task {

    /** Holds all options. */
    private Xsd2CobModel _model;

    /** Logger. */
    private final Log _log = LogFactory.getLog(getClass());

    /**
     * The ant execute method. Generates a new annotated schema.
     */
    public void execute() {
        if (_log.isDebugEnabled()) {
            _log.debug("XML Schema to COBOL translator started");
        }
        try {
            Xsd2CobIO xsd2cob = new Xsd2CobIO(getModel());
            xsd2cob.execute();

        } catch (InvalidXsdException e) {
            throw new BuildException(e);
        } catch (InvalidParameterException e) {
            throw new BuildException(e);
        } catch (IOException e) {
            throw new BuildException(e);
        }
    }

    /**
     * @return a new options model
     */
    public Xsd2CobModel createOptions() {
        return new Xsd2CobModel();
    }

    /**
     * @param model an empty options model
     */
    public void addOptions(final Xsd2CobModel model) {
        _model = model;
    }

    /**
     * Receive a fully configured options model object.
     * 
     * @param model the options model
     */
    public void addConfiguredOptions(final Xsd2CobModel model) {
        _model = model;
    }

    /**
     * @return the options model
     */
    public Xsd2CobModel getModel() {
        return _model;
    }

}