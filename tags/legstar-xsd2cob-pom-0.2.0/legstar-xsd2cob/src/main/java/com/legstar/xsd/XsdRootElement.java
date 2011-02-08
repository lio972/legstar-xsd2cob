package com.legstar.xsd;

/**
 * Represent a root element to add to an XML schema.
 * <p/>
 * Both element and type are assumed to belong to the target XML schema
 * namespace.
 * 
 */
public class XsdRootElement {

    /** The root element name. */
    private String _elementName;

    /** The root element XML schema type. */
    private String _typeName;

    /**
     * No-arg constructor.
     */
    public XsdRootElement() {

    }

    /**
     * Deserialize from string.
     */
    public XsdRootElement(final String fromString) {
        String[] strings = fromString.split(":");
        if (strings.length > 0) {
            _elementName = strings[0];
        }
        if (strings.length > 1) {
            _typeName = strings[1];
        }
    }

    /**
     * @param elementName the lement name
     * @param typeName the element type name
     */
    public XsdRootElement(final String elementName, final String typeName) {
        _elementName = elementName;
        _typeName = typeName;
    }

    /**
     * @return the root element name
     */
    public String getElementName() {
        return _elementName;
    }

    /**
     * @param elementName the root element name to set
     */
    public void setElementName(final String elementName) {
        this._elementName = elementName;
    }

    /**
     * @return the root element XML schema type
     */
    public String getTypeName() {
        return _typeName;
    }

    /**
     * @param typeName the root element XML schema type to set
     */
    public void setTypeName(final String typeName) {
        this._typeName = typeName;
    }

    public String toString() {
        return String.format("%s:%s", getElementName(), getTypeName());
    }

}
