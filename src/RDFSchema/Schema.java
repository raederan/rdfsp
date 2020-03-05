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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.jena.vocabulary.RDFS;

public class Schema {

    private ArrayList<ResourceClass> classes = new ArrayList();
    private ArrayList<PropertyClass> properties = new ArrayList();
    private int id = 1;

    public void Schema() {

    }
    
    public int countResourceClasses(){
        int cnt = 0;
        Iterator<ResourceClass> it = classes.iterator();
        while(it.hasNext()){
            ResourceClass rc = it.next();
            if(rc.isClass()){
                cnt++;
            }
        }
        return cnt;
    }
    
    public int countDatatypes(){
        int cnt = 0;
        Iterator<ResourceClass> it = classes.iterator();
        while(it.hasNext()){
            ResourceClass rc = it.next();
            if(rc.isDatatype()){
                cnt++;
            }
        }
        return cnt;        
    }

    public int countProperyClasses(){
        return properties.size();
    }
    
    public ResourceClass addResourceClass(String uri, String namespace, String name) {
        ResourceClass rc = new ResourceClass(uri, namespace, name);
        if(classes.contains(rc)){
           return classes.get(classes.indexOf(rc));
        }
        classes.add(rc);
        
        return rc;
    }

    public boolean hasResourceClass(String uri){
        Iterator<ResourceClass> it = classes.iterator();
        while(it.hasNext()){
            if(it.next().getUri().compareTo(uri)==0){
                return true;
            }
        }
        return false;
    }

    public Iterator<ResourceClass> getResourceClasses() {
        return classes.iterator();
    }
    
    public PropertyClass addPropertyClass(String uri, String namespace, String name) {
        PropertyClass pc = new PropertyClass(uri, namespace, name);
        if(properties.contains(pc)){
            return properties.get(properties.indexOf(pc));
        }
        properties.add(pc);
        return pc;
    }
    
    public Iterator<PropertyClass> getPropertyClasses() {
        return properties.iterator();
    }
    
    public void addDomainDefinition(ResourceClass domain_rc, PropertyClass pc){
        domain_rc.addOutgoingPropClass(pc);
        pc.addDomainResClass(domain_rc);
    }
    
    public void addRangeDefinition(PropertyClass pc, ResourceClass range_rc){
        range_rc.addIncomingPropClass(pc);
        pc.addRangeResClass(range_rc);
    }

    public void addDomainRangeDefinition(ResourceClass domain_rc, PropertyClass pc, ResourceClass range_rc){
        this.addDomainDefinition(domain_rc, pc);
        this.addRangeDefinition(pc, range_rc);
    }
    
    //method to normalize the RDF schema (i.e. complete empty domain / range properties)
    public void normalize(){
        Iterator<PropertyClass> it = properties.iterator();
        while(it.hasNext()){
            PropertyClass pc = it.next();
            if(pc.hasEmptyDomain()){
                ResourceClass rc = this.addResourceClass(RDFS.Resource.getURI(), RDFS.Resource.getNameSpace(), RDFS.Resource.getLocalName());
                pc.addDomainResClass(rc);
            }
            if(pc.hasEmptyRange()){
                ResourceClass rc = this.addResourceClass(RDFS.Resource.getURI(), RDFS.Resource.getNameSpace(), RDFS.Resource.getLocalName());
                pc.addRangeResClass(rc);
            }
        }
        
    }
    
    // method to write a file with the output RDF schema 
    public void write(String outputFileName) {
        String line;
        try {
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName), "UTF-8"));
            line = "@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .\n";
            writer.write(line);
            line = "@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .\n";
            writer.write(line);
            Iterator<ResourceClass> rit = classes.iterator();
            while (rit.hasNext()) {
                ResourceClass rc = rit.next();
                line = "<" + rc.getUri() + "> rdf:type rdfs:Class .\n";
                writer.write(line);
            }
            Iterator<PropertyClass> pit = properties.iterator();
            while (pit.hasNext()) {
                PropertyClass pc = pit.next();
                line = "<" + pc.getUri() + "> rdf:type rdf:Property .\n";
                writer.write(line);
                // show domain classes
                Iterator<ResourceClass> it;
                it = pc.getDomain();
                while (it.hasNext()) {
                    line = "<" + pc.getUri() + "> rdfs:domain <" + it.next().getUri() + "> .\n";
                    writer.write(line);
                }
                // show range classes
                it = pc.getRange();
                while (it.hasNext()) {
                    line ="<" + pc.getUri() + "> rdfs:range <" + it.next().getUri() + "> .\n";
                    writer.write(line);
                }
            }
            writer.close();

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }
    
}
