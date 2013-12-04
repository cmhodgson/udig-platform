/*******************************************************************************
 * Copyright (c) 2010,2012 City of Vienna.
 *
 * This program and the accompanying materials are made available under the            
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0    
 * which accompanies this distribution.                                                
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at                                
 * http://www.eclipse.org/org/documents/edl-v10.php.                                   
 *                                                                                     
 * Contributors:                                                                       
 *    Aritz Davila (Axios) - initial implementation and documentation                  
 *    Mauricio Pazos (Axios) - initial implementation and documentation
 *******************************************************************************/
package org.locationtech.udig.tools.feature.split;

/**
 * Custom exception used on {@link SplitFeatureBuilder}. This exception is thrown if the
 * {@link SplitFeatureBuilder} cannot split the feature with the split line provided
 * 
 * @author Aritz Davila (www.axios.es)
 * @author Mauricio Pazos (www.axios.es)
 * @since 1.3.3
 */
public final class CannotSplitException extends SplitFeatureBuilderException {

    private static final long serialVersionUID = -1420893046862002184L;

    public CannotSplitException(String ex) {
        super(ex);
    }
}
