package com.legstar.dom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Creates W3C DOM elements.
 * 
 */
public final class ElementFactory {

    /** Document used to create elements. */
    private static Document _doc;

    /**
     * Utility class.
     */
    private ElementFactory() {
    }

    /**
     * Create a dangling element (within its own document).
     * <p/>
     * This is a best effort method, if something goes wrong the output is null.
     * 
     * @param namespaceURI the namespace to tuse
     * @param qualifiedName the qualified (prefixed) element name
     * @return a DOM element or null if something goes wrong
     */
    public static synchronized Element createElement(final String namespaceURI,
            final String qualifiedName) {
        if (_doc == null) {
            try {
                _doc = DocumentFactory.create();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (_doc != null) {
            return _doc.createElementNS(namespaceURI, qualifiedName);
        }
        return null;
    }

}
