package org.ogema.model.server;

import org.ogema.core.model.Resource;
import org.ogema.core.model.simple.BooleanResource;
import org.ogema.core.model.simple.IntegerResource;
import org.ogema.core.model.simple.StringResource;

/**
 *
 * @author jlapp
 */
public interface ConnectedGateway extends Resource {
    
    StringResource name();
    
    IntegerResource port();
    
    BooleanResource connected();
    
}
