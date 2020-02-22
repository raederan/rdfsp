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

public class PropertyClass {

    private String uri;
    private String namespace;
    private String name;
    private ArrayList<ResourceClass> domain_set = new ArrayList();
    private ArrayList<ResourceClass> range_set = new ArrayList();

    public PropertyClass(String _uri, String _namespace, String _name) {
        this.uri = _uri;
        this.namespace = _namespace;
        this.name = _name;
    }

    @Override
    public String toString() {
        return uri;
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

    public void addDomainResClass(ResourceClass rc) {
        if (!domain_set.contains(rc)) {
            domain_set.add(rc);
        }
    }

    public Iterator<ResourceClass> getDomain() {
        return domain_set.iterator();
    }

    public boolean hasEmptyDomain() {
        return domain_set.isEmpty();
    }

    public void addRangeResClass(ResourceClass rc) {
        if (!range_set.contains(rc)) {
            range_set.add(rc);
        }
    }

    public Iterator<ResourceClass> getRange() {
        return range_set.iterator();
    }

    public boolean hasEmptyRange() {
        return range_set.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PropertyClass)) {
            return false;
        }
        PropertyClass pc = (PropertyClass) o;
        return this.uri.compareTo(pc.uri) == 0;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.uri);
        return hash;
    }

}
