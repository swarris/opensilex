//******************************************************************************
//                          ProvenanceGetDTO.java
// OpenSILEX - Licence AGPL V3.0 - https://www.gnu.org/licenses/agpl-3.0.en.html
// Copyright © INRAE 2020
// Contact: alice.boizet@inrae.fr, anne.tireau@inrae.fr, pascal.neveu@inrae.fr
//******************************************************************************
package org.opensilex.core.provenance.api;

import java.net.URI;

/**
 * Provenance Get DTO
 * @author Alice Boizet
 */
public class ProvenanceGetDTO extends ProvenanceCreationDTO {
    
    protected URI uri;

    public URI getUri() {
        return uri;
    }

    public void setUri(URI uri) {
        this.uri = uri;
    }
    
}
