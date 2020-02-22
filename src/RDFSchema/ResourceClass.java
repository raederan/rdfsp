/* 
 * Copyright 2020 Renzo Angles (http://renzoangles.com/)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package RDFSchema;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;

public class ResourceClass {

    private String uri;
    private String namespace;
    private String name;
    private Boolean datatype = false;
    //incoming property clases
    private ArrayList<PropertyClass> in_pcs = new ArrayList();
    //outgoing property classes
    private ArrayList<PropertyClass> out_pcs = new ArrayList();

    public ResourceClass(String _uri, String _namespace, String _name) {
        this.uri = _uri;
        this.namespace = _namespace;
        this.name = _name;
    }

    @Override
    public String toString(){
        return uri; 
    }
    
    public void setAsDatatype(){
        this.datatype = true;
    }
    
    public Boolean isClass(){
        return !datatype;    
    } 
    
    public Boolean isDatatype(){
        return datatype;
    }

    public String getUri() {
        return uri;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getName() {
        return name;
    }

    // Incoming Property Classes
    public void addIncomingPropClass(PropertyClass pc) {
        if (!in_pcs.contains(pc)) {
            in_pcs.add(pc);
        }
    }

    public boolean hasIncomingPropClass(PropertyClass pc) {
        return in_pcs.contains(pc);
    }

    public Iterator<PropertyClass> getIncomingPropClasses() {
        return in_pcs.iterator();
    }

    // Outgoing Property Classes
    public void addOutgoingPropClass(PropertyClass pc) {
        if (!out_pcs.contains(pc)) {
            out_pcs.add(pc);
        }
    }

    public boolean hasOutgoingPropClass(PropertyClass pc) {
        return out_pcs.contains(pc);
    }

    public Iterator<PropertyClass> getOutgoingPropClasses() {
        return out_pcs.iterator();
    }

    public boolean hasPropertyClass(PropertyClass pc) {
        return (in_pcs.contains(pc) || out_pcs.contains(pc));
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ResourceClass)) {
            return false;
        }
        ResourceClass rc = (ResourceClass) o;
        return this.uri.compareTo(rc.uri) == 0;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.uri);
        return hash;
    }

}
