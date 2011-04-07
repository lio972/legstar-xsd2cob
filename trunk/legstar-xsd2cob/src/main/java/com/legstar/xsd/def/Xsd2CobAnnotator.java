package com.legstar.xsd.def;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaFractionDigitsFacet;
import org.apache.ws.commons.schema.XmlSchemaLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaPatternFacet;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeList;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeRestriction;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeUnion;
import org.apache.ws.commons.schema.XmlSchemaTotalDigitsFacet;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.w3c.dom.Element;

import com.legstar.coxb.CobolMarkup;
import com.legstar.coxb.CobolType;
import com.legstar.dom.ElementFactory;
import com.legstar.xsd.AbstractXsdAnnotator;
import com.legstar.xsd.XsdConstants;
import com.legstar.xsd.XsdFacets;
import com.legstar.xsd.XsdMappingException;

/**
 * The default annotator produces COBOL annotations by inference.
 * <p/>
 * The COBOL layout inferred maps to the XSD hierarchy starting from root
 * Elements and descending into children by following the Complex Types
 * sequences.
 * <p/>
 * 
 */
public class Xsd2CobAnnotator extends AbstractXsdAnnotator {

    /** Logging. */
    private Log _log = LogFactory.getLog(getClass());

    /** Configuration data. */
    private Xsd2CobConfig _xsdConfig;

    /**
     * Construct the annotator.
     */
    public Xsd2CobAnnotator() {
        this(new Xsd2CobConfig());
    }

    /**
     * Construct the annotator.
     * 
     * @param xsdConfig the configuration data
     */
    public Xsd2CobAnnotator(final Xsd2CobConfig xsdConfig) {
        _xsdConfig = xsdConfig;
    }

    public Xsd2CobAnnotator(Map < String, String > complexTypeToJavaClassMap) {
        this(new Xsd2CobConfig(), complexTypeToJavaClassMap);
    }

    public Xsd2CobAnnotator(final Xsd2CobConfig xsdConfig,
            Map < String, String > complexTypeToJavaClassMap) {
        super(complexTypeToJavaClassMap);
        _xsdConfig = xsdConfig;
    }

    public void setUp() throws IOException {
        super.setUp();
    }

    /**
     * Create a set of Cobol annotation mapping the corresponding XML schema
     * element attributes.
     * 
     * @param schema the XML Schema being annotated
     * @param xsdElement the XML Schema element to annotate
     * @param level the current level in the elements hierarchy. This is used to
     *            create Cobol levels with the same depth as the input XML
     *            schema.
     * @throws XsdMappingException if annotation fails
     */
    public void processElement(final XmlSchema schema,
            final XmlSchemaElement xsdElement, final int level)
            throws XsdMappingException {

        if (_log.isDebugEnabled()) {
            _log.debug("setAttributes started for element  = "
                    + xsdElement.getName());
            _log.debug("   XmlSchemaElement QName          = "
                    + xsdElement.getQName());
            _log.debug("   XmlSchemaElement SchemaType     = "
                    + xsdElement.getSchemaType());
            _log.debug("   XmlSchemaElement SchemaTypeName = "
                    + xsdElement.getSchemaTypeName());
            _log.debug("   XmlSchemaElement MaxOccurs      = "
                    + xsdElement.getMaxOccurs());
            _log.debug("   XmlSchemaElement MinOccurs      = "
                    + xsdElement.getMinOccurs());
            _log.debug("   XmlSchemaElement RefName        = "
                    + xsdElement.getRefName());
            _log.debug("   XmlSchemaElement DefaultValue   = "
                    + xsdElement.getDefaultValue());
            _log.debug("   XmlSchemaElement FixedValue     = "
                    + xsdElement.getFixedValue());
        }

        String coxbPrefix = schema.getNamespaceContext().getPrefix(
                CobolMarkup.NS);
        Element elc = ElementFactory.createElement(CobolMarkup.NS, coxbPrefix
                + ":" + CobolMarkup.ELEMENT);

        /* Add cobol attributes valid for all types */
        elc.setAttribute(CobolMarkup.LEVEL_NUMBER, Integer.toString(level));
        elc.setAttribute(CobolMarkup.COBOL_NAME,
                getCobolName(xsdElement.getName()));

        if (_log.isDebugEnabled()) {
            _log.debug("   Cobol level          = " + level);
            _log.debug("   Cobol name           = "
                    + elc.getAttribute(CobolMarkup.COBOL_NAME));
        }
        /*
         * The semantic for maxOccurs is different for Cobol annotations than
         * for XML Schema. a maxOccurs of 1 is a one item array for Cobol which
         * is different from a simple item. If schema maxOccurs=1 we do not
         * insert a Cobol maxOccurs annotation at all.
         */
        /*
         * There is no natural mapping from XML schema arrays to Cobol arrays
         * with depending on clause. This means that all XML Schema arrays are
         * mapped to fixed size Cobol arrays. Since this would result in very
         * inefficient Cobol structures, we impose a limit on arrays sizes.
         */
        if (xsdElement.getMaxOccurs() > 1) {
            if (xsdElement.getMaxOccurs() > Short.MAX_VALUE) {
                elc.setAttribute(CobolMarkup.MAX_OCCURS,
                        toString(_xsdConfig.getMaxOccurs()));
                _log.warn("Max occurs for element " + xsdElement.getName()
                        + " has been set to default value "
                        + _xsdConfig.getMaxOccurs());
            } else {
                elc.setAttribute(CobolMarkup.MAX_OCCURS,
                        Long.toString(xsdElement.getMaxOccurs()));
            }

            elc.setAttribute(CobolMarkup.MIN_OCCURS,
                    Long.toString(xsdElement.getMinOccurs()));

            if (_log.isDebugEnabled()) {
                _log.debug("   Cobol minOccurs      = "
                        + elc.getAttribute(CobolMarkup.MIN_OCCURS));
                _log.debug("   Cobol maxOccurs      = "
                        + elc.getAttribute(CobolMarkup.MAX_OCCURS));
            }
        }

        /* Examine inner simple and complex types */
        XmlSchemaType xsdType = null;
        if (xsdElement.getSchemaType() != null) {
            xsdType = xsdElement.getSchemaType();
        } else if (xsdElement.getSchemaTypeName() != null) {
            QName name = new QName(schema.getTargetNamespace(), xsdElement
                    .getSchemaTypeName().getLocalPart());
            xsdType = schema.getTypeByName(name);
        }

        if (xsdType instanceof XmlSchemaSimpleType) {
            setSimpleTypeAttributes(schema, (XmlSchemaSimpleType) xsdType, elc);
        } else if (xsdType instanceof XmlSchemaComplexType) {
            setComplexTypeAttributes(schema, (XmlSchemaComplexType) xsdType,
                    elc, level);
        }

        /* Add the annotation to the XML schema object */
        annotate(schema, xsdElement, elc);

        if (_log.isDebugEnabled()) {
            _log.debug("setAttributes ended for element = "
                    + xsdElement.getName());
        }

    }

    /**
     * Create a set of cobol attributes for a simple XML schema type.
     * 
     * @param schema the XML Schema being annotated
     * @param xsdSimpleType the XML schema type
     * @param elc the DOM Element representing the Cobol annotation
     * @throws XsdMappingException if annotation fails
     */
    protected void setSimpleTypeAttributes(final XmlSchema schema,
            final XmlSchemaSimpleType xsdSimpleType, final Element elc)
            throws XsdMappingException {

        if (_log.isDebugEnabled()) {
            _log.debug("setSimpleTypeAttributes started for type = "
                    + xsdSimpleType.getName());
            _log.debug("   XmlSchemaType QName                   = "
                    + xsdSimpleType.getQName());
            _log.debug("   XmlSchemaType BaseSchemaType          = "
                    + xsdSimpleType.getBaseSchemaType());
            _log.debug("   XmlSchemaType DataType                = "
                    + xsdSimpleType.getDataType());
            _log.debug("   XmlSchemaType DeriveBy                = "
                    + xsdSimpleType.getDeriveBy());
        }
        /*
         * Somewhere in this simple type hierarchy there must be a primitive
         * type from which it is derived.
         */
        QName primitiveType = getPrimitiveType(schema, xsdSimpleType);

        /* From the primitive XML schema type we infer a candidate Cobol type */
        CobolType cobolType = getXsdCobolTypeMap().get(primitiveType);
        if (cobolType == null) {
            throw new XsdMappingException("Unsupported XML Schema type "
                    + xsdSimpleType.getQName());
        }
        elc.setAttribute(CobolMarkup.TYPE, cobolType.name());
        if (_log.isDebugEnabled()) {
            _log.debug("   Cobol type           = "
                    + elc.getAttribute(CobolMarkup.TYPE));
        }

        /*
         * Simple types can derive from xsd:list, in which case we need to map
         * them to arrays. Lists being unbounded, we need to artificially set a
         * maximum bound to the corresponding Cobol array.
         */
        if (xsdSimpleType.getContent() instanceof XmlSchemaSimpleTypeList) {
            elc.setAttribute(CobolMarkup.MIN_OCCURS, "1");
            elc.setAttribute(CobolMarkup.MAX_OCCURS,
                    toString(_xsdConfig.getMaxOccurs()));
            if (_log.isDebugEnabled()) {
                _log.debug("   Cobol minOccurs      = "
                        + elc.getAttribute(CobolMarkup.MIN_OCCURS));
                _log.debug("   Cobol maxOccurs      = "
                        + elc.getAttribute(CobolMarkup.MAX_OCCURS));
            }
        }

        /* We collect all facets of interest from the type restriction */
        XsdFacets facets = new XsdFacets();
        getFacets(schema, xsdSimpleType, facets);

        /* Based on the element type and facets we gather more attributes */
        switch (cobolType) {
        case ALPHANUMERIC_ITEM:
            setAlphaNumericAttributes(primitiveType, facets, elc);
            break;
        case BINARY_ITEM:
            setBinaryAttributes(primitiveType, facets, elc);
            break;
        case PACKED_DECIMAL_ITEM:
            setDecimalAttributes(primitiveType, facets, elc);
            break;
        case SINGLE_FLOAT_ITEM:
            setSingleFloatAttributes(primitiveType, elc);
            break;
        case DOUBLE_FLOAT_ITEM:
            setDoubleFloatAttributes(primitiveType, elc);
            break;
        case OCTET_STREAM_ITEM:
            setOctetStreamAttributes(primitiveType, facets, elc);
            break;
        default:
            throw new XsdMappingException("Cobol type inferred is invalid");
        }
        if (_log.isDebugEnabled()) {
            _log.debug("setSimpleTypeAttributes ended for type = "
                    + xsdSimpleType.getName());
        }
    }

    /**
     * COBOL Alphanumerics are bounded. They must have a fixed size. This method
     * tries to infer one.
     * 
     * @param primitiveType the XML Schema primitive type
     * @param facets the set of XML schema facets
     * @param elc the annotated element
     * @throws XsdMappingException if attributes cannot be set
     */
    protected void setAlphaNumericAttributes(final QName primitiveType,
            final XsdFacets facets, final Element elc)
            throws XsdMappingException {

        if (_log.isDebugEnabled()) {
            _log.debug("setAlphaNumericAttributes started for type = "
                    + primitiveType.getLocalPart());
        }

        /*
         * If a byte length cannot be inferred from a facet in the XML schema*
         * type hierarchy, use the default.
         */
        int byteLength = (facets.getLength() > 0) ? facets.getLength() : facets
                .getMaxLength();
        if (byteLength < 0) {
            byteLength = _xsdConfig.getAlphanumericLen();
            _log.warn("Byte length for element "
                    + elc.getAttribute(CobolMarkup.COBOL_NAME)
                    + " has been set to default value " + byteLength);
        }

        /*
         * TODO add analysis of pattern facet to refine type and picture
         * inference TODO see if there is a way to set isJustifiedRight
         */
        elc.setAttribute(CobolMarkup.PICTURE,
                "X(" + Integer.toString(byteLength) + ")");
        elc.setAttribute(CobolMarkup.USAGE, "DISPLAY");

        if (_log.isDebugEnabled()) {
            _log.debug("setAlphaNumericAttributes ended for type = "
                    + primitiveType.getLocalPart());
            _log.debug("   Cobol picture        = "
                    + elc.getAttribute(CobolMarkup.PICTURE));
            _log.debug("   Cobol usage          = "
                    + elc.getAttribute(CobolMarkup.USAGE));
        }
    }

    /**
     * COBOL octet stream data items are similar to alphanumerics.
     * 
     * @param primitiveType the XML Schema primitive type
     * @param facets the set of XML schema facets
     * @param elc the annotated element
     * @throws XsdMappingException if attributes cannot be set
     */
    protected void setOctetStreamAttributes(final QName primitiveType,
            final XsdFacets facets, final Element elc)
            throws XsdMappingException {

        if (_log.isDebugEnabled()) {
            _log.debug("setOctetStreamAttributes started for type = "
                    + primitiveType.getLocalPart());
        }
        /*
         * If a byte length cannot be inferred from a facet in the XML schema*
         * type hierarchy, use the default.
         */
        int byteLength = facets.getLength();
        if (byteLength < 0) {
            byteLength = _xsdConfig.getOctetStreamLen();
        }

        elc.setAttribute(CobolMarkup.PICTURE,
                "X(" + Integer.toString(byteLength) + ")");
        elc.setAttribute(CobolMarkup.USAGE, "DISPLAY");

        if (_log.isDebugEnabled()) {
            _log.debug("setOctetStreamAttributes ended for type = "
                    + primitiveType.getLocalPart());
            _log.debug("   Cobol picture        = "
                    + elc.getAttribute(CobolMarkup.PICTURE));
            _log.debug("   Cobol usage          = "
                    + elc.getAttribute(CobolMarkup.USAGE));
        }
    }

    /**
     * COBOL Binary numerics are signed or unsigned and have a fixed number of
     * digits. This method infers a number of digits and a sign.
     * 
     * @param primitiveType the XML Schema primitive type
     * @param facets the set of XML schema facets
     * @param elc the annotated element
     * @throws XsdMappingException if attributes cannot be set
     */
    protected void setBinaryAttributes(final QName primitiveType,
            final XsdFacets facets, final Element elc)
            throws XsdMappingException {

        if (_log.isDebugEnabled()) {
            _log.debug("setBinaryAttributes started for type = "
                    + primitiveType.getLocalPart());
        }
        /*
         * If total digits are not specified in the XML schema, infer a suitable
         * default from the XML schema primitive type.
         */
        int totalDigits = facets.getTotalDigits();
        if (totalDigits < 0) {
            totalDigits = _xsdConfig.getIntTotalDigits();
            if (primitiveType.getLocalPart().equals("boolean")) {
                totalDigits = _xsdConfig.getBoolTotalDigits();
            } else if (primitiveType.getLocalPart().equals("unsignedShort")) {
                totalDigits = _xsdConfig.getShortTotalDigits();
            } else if (primitiveType.getLocalPart().equals("unsignedLong")) {
                totalDigits = _xsdConfig.getLongTotalDigits();
            } else if (primitiveType.getLocalPart().equals("long")) {
                totalDigits = _xsdConfig.getLongTotalDigits();
            } else if (primitiveType.getLocalPart().equals("short")) {
                totalDigits = _xsdConfig.getShortTotalDigits();
            }
            /*
             * If a restriction on the number of digits is not explicitly
             * requested, use the cobol unlimited binary data type
             */
            elc.setAttribute(CobolMarkup.USAGE, "COMP-5");
        } else {
            elc.setAttribute(CobolMarkup.USAGE, "BINARY");
        }

        /* Determine if this is an unsigned numeric */
        boolean signed = true;
        if (primitiveType.getLocalPart().equals("boolean")
                || primitiveType.getLocalPart().equals("positiveInteger")
                || primitiveType.getLocalPart().equals("nonNegativeInteger")
                || primitiveType.getLocalPart().equals("unsignedShort")
                || primitiveType.getLocalPart().equals("unsignedLong")
                || primitiveType.getLocalPart().equals("unsignedInt")) {
            signed = false;
        }

        /*
         * TODO add analysis of pattern facet to refine type and picture
         * inference
         */
        elc.setAttribute(CobolMarkup.PICTURE, ((signed) ? "S" : "") + "9("
                + Integer.toString(totalDigits) + ")");
        elc.setAttribute(CobolMarkup.TOTAL_DIGITS,
                Integer.toString(totalDigits));
        elc.setAttribute(CobolMarkup.IS_SIGNED, Boolean.toString(signed));

        if (_log.isDebugEnabled()) {
            _log.debug("setBinaryAttributes ended for type = "
                    + primitiveType.getLocalPart());
            _log.debug("   Cobol picture        = "
                    + elc.getAttribute(CobolMarkup.PICTURE));
            _log.debug("   Cobol usage          = "
                    + elc.getAttribute(CobolMarkup.USAGE));
            _log.debug("   Cobol totalDigits    = "
                    + elc.getAttribute(CobolMarkup.TOTAL_DIGITS));
            _log.debug("   Cobol isSigned       = "
                    + elc.getAttribute(CobolMarkup.IS_SIGNED));
        }
    }

    /**
     * COBOL Decimal numerics are always signed and have a fixed number of total
     * digits and fraction digits. This method infers numbers of digits and
     * sign.
     * 
     * @param primitiveType the XML Schema primitive type
     * @param facets the set of XML schema facets
     * @param elc the annotated element
     * @throws XsdMappingException if attributes cannot be set
     */
    protected void setDecimalAttributes(final QName primitiveType,
            final XsdFacets facets, final Element elc)
            throws XsdMappingException {

        if (_log.isDebugEnabled()) {
            _log.debug("setDecimalAttributes started for type = "
                    + primitiveType.getLocalPart());
        }

        /*
         * If digits numbers are not specified in the XML schema, infer a
         * suitable default from the XML schema primitive type.
         */
        int totalDigits = facets.getTotalDigits();
        if (totalDigits < 0) {
            totalDigits = _xsdConfig.getDecTotalDigits();
        }
        int fractionDigits = facets.getFractionDigits();
        if (fractionDigits < 0) {
            fractionDigits = _xsdConfig.getDecFracDigits();
        }

        /* Consider decimals as always signed */
        boolean signed = true;

        elc.setAttribute(
                CobolMarkup.PICTURE,
                "S9("
                        + Integer.toString(totalDigits - fractionDigits)
                        + ((fractionDigits > 0) ? ")V9("
                                + Integer.toString(fractionDigits) : "") + ")");
        elc.setAttribute(CobolMarkup.TOTAL_DIGITS,
                Integer.toString(totalDigits));
        elc.setAttribute(CobolMarkup.FRACTION_DIGITS,
                Integer.toString(fractionDigits));
        elc.setAttribute(CobolMarkup.USAGE, "COMP-3");
        elc.setAttribute(CobolMarkup.IS_SIGNED, Boolean.toString(signed));

        if (_log.isDebugEnabled()) {
            _log.debug("setDecimalAttributes ended for type = "
                    + primitiveType.getLocalPart());
            _log.debug("   Cobol picture        = "
                    + elc.getAttribute(CobolMarkup.PICTURE));
            _log.debug("   Cobol usage          = "
                    + elc.getAttribute(CobolMarkup.USAGE));
            _log.debug("   Cobol totalDigits    = "
                    + elc.getAttribute(CobolMarkup.TOTAL_DIGITS));
            _log.debug("   Cobol fractionDigits = "
                    + elc.getAttribute(CobolMarkup.FRACTION_DIGITS));
            _log.debug("   Cobol isSigned       = "
                    + elc.getAttribute(CobolMarkup.IS_SIGNED));
        }
    }

    /**
     * COBOL single float numerics.
     * 
     * @param primitiveType the XML Schema primitive type
     * @param elc the annotated element
     * @throws XsdMappingException if attributes cannot be set
     */
    protected void setSingleFloatAttributes(final QName primitiveType,
            final Element elc) throws XsdMappingException {

        if (_log.isDebugEnabled()) {
            _log.debug("setSingleFloatAttributes started for type = "
                    + primitiveType.getLocalPart());
        }

        elc.setAttribute(CobolMarkup.USAGE, "COMP-1");

        if (_log.isDebugEnabled()) {
            _log.debug("setSingleFloatAttributes ended for type = "
                    + primitiveType.getLocalPart());
            _log.debug("   Cobol usage          = "
                    + elc.getAttribute(CobolMarkup.USAGE));
        }
    }

    /**
     * COBOL double float numerics.
     * 
     * @param primitiveType the XML Schema primitive type
     * @param elc the annotated element
     * @throws XsdMappingException if attributes cannot be set
     */
    protected void setDoubleFloatAttributes(final QName primitiveType,
            final Element elc) throws XsdMappingException {

        if (_log.isDebugEnabled()) {
            _log.debug("setDoubleFloatAttributes started for type = "
                    + primitiveType.getLocalPart());
        }

        elc.setAttribute(CobolMarkup.USAGE, "COMP-2");

        if (_log.isDebugEnabled()) {
            _log.debug("setDoubleFloatAttributes ended for type = "
                    + primitiveType.getLocalPart());
            _log.debug("   Cobol usage          = "
                    + elc.getAttribute(CobolMarkup.USAGE));
        }
    }

    /**
     * Inferring the XML schema primitive type involves a recursive search
     * because types can form a hierarchy and restrict each other.
     * 
     * @param schema the XML Schema being annotated
     * @param xsdSimpleType the type from which a primitive type should be
     *            inferred
     * @return the primitive type
     * @throws XsdMappingException if primitive type cannot be inferred
     */
    protected QName getPrimitiveType(final XmlSchema schema,
            final XmlSchemaSimpleType xsdSimpleType) throws XsdMappingException {

        if (_log.isDebugEnabled()) {
            _log.debug("getPrimitiveType started for type = "
                    + xsdSimpleType.getName());
        }

        QName typeName = xsdSimpleType.getQName();
        if (typeName != null
                && XsdConstants.XSD_NS.equals(typeName.getNamespaceURI())) {
            if (_log.isDebugEnabled()) {
                _log.debug("getPrimitiveType ended for type = "
                        + xsdSimpleType.getName());
                _log.debug("   PrimitiveType = " + typeName);
            }
            return typeName;
        }
        if (xsdSimpleType.getContent() != null) {
            if (xsdSimpleType.getContent() instanceof XmlSchemaSimpleTypeRestriction) {
                XmlSchemaSimpleTypeRestriction restriction = (XmlSchemaSimpleTypeRestriction) xsdSimpleType
                        .getContent();
                /*
                 * For an unknown reason, getBaseType() sometimes returns null.
                 * In such a case we have to locate the type using
                 * getBaseTypeName()
                 */
                if (restriction.getBaseType() == null) {
                    typeName = restriction.getBaseTypeName();
                    if (typeName != null) {
                        if (XsdConstants.XSD_NS.equals(typeName
                                .getNamespaceURI())) {
                            if (_log.isDebugEnabled()) {
                                _log.debug("getPrimitiveType ended for type = "
                                        + xsdSimpleType.getName());
                                _log.debug("   PrimitiveType = " + typeName);
                            }
                            return typeName;
                        }
                        /*
                         * Since restriction base type is not an XML Schema
                         * standard type, it must be defined in this schema. We
                         * don't support restrictions that are not simple types.
                         */
                        XmlSchemaType restrictionBaseType = schema
                                .getTypeByName(typeName);
                        if (restrictionBaseType != null
                                && restrictionBaseType instanceof XmlSchemaSimpleType) {
                            return getPrimitiveType(schema,
                                    (XmlSchemaSimpleType) restrictionBaseType);
                        }
                    }
                } else {
                    return getPrimitiveType(schema, restriction.getBaseType());
                }

            } else if (xsdSimpleType.getContent() instanceof XmlSchemaSimpleTypeList) {
                /* If this is a list, look for items type. */
                XmlSchemaSimpleTypeList listType = (XmlSchemaSimpleTypeList) xsdSimpleType
                        .getContent();
                return getPrimitiveType(schema, listType.getItemType());

            } else if (xsdSimpleType.getContent() instanceof XmlSchemaSimpleTypeUnion) {
                _log.warn(xsdSimpleType.getName()
                        + " is a union. Processing first type in the union.");
                XmlSchemaSimpleTypeUnion simpleUnion = (XmlSchemaSimpleTypeUnion) xsdSimpleType
                        .getContent();
                return getPrimitiveType(schema,
                        (XmlSchemaSimpleType) simpleUnion.getBaseTypes()
                                .getItem(0));
            }
        }
        throw new XsdMappingException("Cannot infer primitive type for "
                + typeName);
    }

    /**
     * Search for the all facets found in an XML schema type hierarchy. Since we
     * start from the most detailed type, the first facets encountered take
     * precedence over the ones we encounter higher in the hierarchy.
     * 
     * @param schema the XML Schema being annotated
     * @param xsdSimpleType the type from which facets should be extracted
     * @param facets the facets extracted so far
     * @throws XsdMappingException if facets cannot be located
     */
    @SuppressWarnings("unchecked")
    protected void getFacets(final XmlSchema schema,
            final XmlSchemaSimpleType xsdSimpleType, final XsdFacets facets)
            throws XsdMappingException {

        /* facets are found in types restrictions */
        if (xsdSimpleType.getContent() == null) {
            return;
        }
        if (_log.isDebugEnabled()) {
            _log.debug("getFacets started for type = "
                    + xsdSimpleType.getName());
        }

        if (xsdSimpleType.getContent() instanceof XmlSchemaSimpleTypeRestriction) {
            XmlSchemaSimpleTypeRestriction restriction = (XmlSchemaSimpleTypeRestriction) xsdSimpleType
                    .getContent();
            if (restriction.getFacets() != null) {
                XmlSchemaObjectCollection collection = restriction.getFacets();
                for (Iterator < XmlSchemaObject > i = collection.getIterator(); i
                        .hasNext();) {
                    XmlSchemaObject facet = i.next();
                    /*
                     * When a facet value is found, we keep it only if no
                     * previous type did set the same facet value
                     */
                    if (facet instanceof XmlSchemaLengthFacet) {
                        XmlSchemaLengthFacet xsef = (XmlSchemaLengthFacet) facet;
                        if (facets.getLength() == -1) {
                            facets.setLength(new Integer((String) xsef
                                    .getValue()));
                        }
                    } else if (facet instanceof XmlSchemaMaxLengthFacet) {
                        XmlSchemaMaxLengthFacet xsef = (XmlSchemaMaxLengthFacet) facet;
                        if (facets.getMaxLength() == -1) {
                            facets.setMaxLength(new Integer((String) xsef
                                    .getValue()));
                        }
                    } else if (facet instanceof XmlSchemaPatternFacet) {
                        XmlSchemaPatternFacet xsef = (XmlSchemaPatternFacet) facet;
                        if (facets.getPattern() == null) {
                            facets.setPattern((String) xsef.getValue());
                        }
                    } else if (facet instanceof XmlSchemaTotalDigitsFacet) {
                        XmlSchemaTotalDigitsFacet xsef = (XmlSchemaTotalDigitsFacet) facet;
                        if (facets.getTotalDigits() == -1) {
                            facets.setTotalDigits(new Integer((String) xsef
                                    .getValue()));
                        }
                    } else if (facet instanceof XmlSchemaFractionDigitsFacet) {
                        XmlSchemaFractionDigitsFacet xsef = (XmlSchemaFractionDigitsFacet) facet;
                        if (facets.getFractionDigits() == -1) {
                            facets.setFractionDigits(new Integer((String) xsef
                                    .getValue()));
                        }
                    }
                }
            }

            /*
             * If this type derives from another non-primitive one, continue the
             * search up the hierarchy chain.
             */
            if (restriction.getBaseType() == null) {
                QName typeName = restriction.getBaseTypeName();
                if (typeName != null
                        && !XsdConstants.XSD_NS.equals(typeName
                                .getNamespaceURI())) {
                    getFacets(schema,
                            (XmlSchemaSimpleType) schema
                                    .getTypeByName(typeName), facets);
                }
            } else {
                getFacets(schema, restriction.getBaseType(), facets);
            }
        }

        if (_log.isDebugEnabled()) {
            _log.debug("getFacets ended for type = " + xsdSimpleType.getName());
            _log.debug("   Length facet         = " + facets.getLength());
            _log.debug("   TotalDigits facet    = " + facets.getTotalDigits());
            _log.debug("   FractionDigits facet = "
                    + facets.getFractionDigits());
            _log.debug("   Pattern facet        = " + facets.getPattern());
        }
    }

    /**
     * Create a set of Cobol annotation mapping the corresponding XML schema
     * complex type attributes.
     * 
     * @param schema the XML Schema being annotated
     * @param xsdComplexType the XML Schema complex type to annotate
     * @param level the current level in the elements hierarchy. This is used to
     *            create Cobol levels with the same depth as the input XML
     *            schema.
     * @throws XsdMappingException if annotation fails
     */
    public void processComplexType(final XmlSchema schema,
            final XmlSchemaComplexType xsdComplexType, final int level)
            throws XsdMappingException {

        /*
         * If this complex type maps to a java class name, add this attribute to
         * the annotation
         */
        if (getComplexTypeToJavaClassMap() != null) {
            String javaClassName = getComplexTypeToJavaClassMap().get(
                    xsdComplexType.getName());
            if (javaClassName != null) {
                if (_log.isDebugEnabled()) {
                    _log.debug("   java class name = " + javaClassName);
                }
                /* Create a DOM document to hold annotation notes */
                String coxbPrefix = schema.getNamespaceContext().getPrefix(
                        CobolMarkup.NS);
                Element elc = ElementFactory.createElement(CobolMarkup.NS,
                        coxbPrefix + ":" + CobolMarkup.COMPLEX_TYPE);
                elc.setAttribute(CobolMarkup.JAVA_CLASS_NAME, javaClassName);
                annotate(schema, xsdComplexType, elc);
            }
        }
    }

    /**
     * Shortcut for integer to string.
     * 
     * @param i an integer
     * @return its string representation
     */
    protected String toString(final int i) {
        return Integer.toString(i);
    }

}
