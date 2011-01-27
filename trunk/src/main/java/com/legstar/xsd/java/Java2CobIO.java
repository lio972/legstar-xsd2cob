package com.legstar.xsd.java;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.legstar.coxb.util.NameUtil;
import com.legstar.xsd.InvalidParameterException;
import com.legstar.xsd.InvalidXsdException;
import com.legstar.xsd.XsdToCobolStringResult;
import com.legstar.xsd.XsdWriter;

/**
 * Java to COBOL translation using file system.
 * <p/>
 * An extension of the core API that deals with reading/ writing to the file
 * system.
 * 
 */
public class Java2CobIO extends Java2Cob {

    /** Logger. */
    private final Log _log = LogFactory.getLog(getClass());

    public Java2CobIO(final Java2CobModel model) {
        super(model);
    }

    /**
     * Generates a new annotated schema.
     * 
     * @throws IOException if basic read/write operation fails
     * @throws InvalidXsdException if XML schema read is invalid
     * @throws InvalidParameterException if one of the parameters is invalid
     */
    public void execute() throws IOException, InvalidXsdException,
            InvalidParameterException {

        if (_log.isDebugEnabled()) {
            _log.debug("Java to COBOL translator started");
        }

        checkParameters();

        XsdToCobolStringResult results = translate();

        XsdWriter.writeResults(getDefaultName(), getModel().getTargetXsdFile(),
                getModel().getTargetCobolFile(), getModel()
                        .getTargetCobolEncoding(), results, _log);
    }

    /**
     * Sanity checks this model.
     * 
     * @throws InvalidParameterException if a parameter is invalid
     */
    public void checkParameters() throws InvalidParameterException {

        if (getModel().getClassNames() == null
                || getModel().getClassNames().size() == 0) {
            throw new InvalidParameterException(
                    "No java classes were specified");
        }

        XsdWriter.check(getModel().getTargetXsdFile(), getModel()
                .getTargetCobolFile());

    }

    /**
     * The user did not specify an explicit output file name so we need to
     * determine a default base name.
     * <p/>
     * If there is a single input class name, we use it as a hint.
     * <p/>
     * If there are multiple class names, we attempt to use the last segment of
     * the package name as a hint.
     * <p/>
     * Finally we return the hint in lower case.
     * 
     * @return a default name
     */
    protected String getDefaultName() {
        if (getModel().getClassNames() == null) {
            return null;
        }

        String hint = getModel().getClassNames().get(0);
        List < String > words = NameUtil.toWordList(hint);
        if (getModel().getClassNames().size() == 1) {
            hint = words.get((words.size() - 1));
        } else {
            if (words.size() > 1) {
                hint = words.get((words.size() - 2));
                /* We might have an embedded class */
                if (hint.equals("$")) {
                    hint = words.get((words.size() - 3));
                }
            } else {
                hint = words.get((words.size() - 1));
            }
        }
        return hint.toLowerCase();
    }

}