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
package Extractor;

import RDFSchema.PropertyClass;
import RDFSchema.ResourceClass;
import RDFSchema.Schema;
import java.io.File;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.util.SplitIRI;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

public class Reader2 implements StreamRDF {

    int cnt = 0;

    Schema schema;
    //multimap to store pairs <resource,{Class1,...,ClassN}>
    MultiMap mm_res_rcs;

    public Reader2(Schema _schema, MultiMap mm) {
        schema = _schema;
        mm_res_rcs = mm;
    }

    @Override
    public void start() {
    }

    @Override
    public void triple(Triple triple) {
        cnt++;
        if (cnt % 1000 == 0) {
            System.out.print(".");
        }
        Node s = triple.getSubject();
        Node p = triple.getPredicate();
        Node o = triple.getObject();        
        //System.out.println(this.getNodeString(s) + " " + this.getNodeString(p) + " " + this.getNodeString(o));
        
        if(p.hasURI(RDF.type.getURI())){
            return;
        }
        
        // the domain of property p will be the classes associated to s
        // the range of property p will be the classes associated to o            
        PropertyClass pc = schema.addPropertyClass(p.getURI(), p.getNameSpace(), p.getLocalName());

        if (o.isURI()) {
            HashSet subj_classes = mm_res_rcs.get(s.getURI());
            HashSet obj_classes = mm_res_rcs.get(o.getURI());
            if (subj_classes != null && obj_classes != null) {
                Iterator<Object> subj_classes_it = subj_classes.iterator();
                while (subj_classes_it.hasNext()) {
                    ResourceClass rc1 = (ResourceClass) subj_classes_it.next();
                    Iterator<Object> obj_classes_it = obj_classes.iterator();
                    while (obj_classes_it.hasNext()) {
                        ResourceClass rc2 = (ResourceClass) obj_classes_it.next();
                        schema.addDomainRangeDefinition(rc1, pc, rc2);
                    }
                }
            }
            if (subj_classes != null && obj_classes == null) {
                Iterator<Object> subj_classes_it = subj_classes.iterator();
                while (subj_classes_it.hasNext()) {
                    ResourceClass rc1 = (ResourceClass) subj_classes_it.next();
                    schema.addDomainDefinition(rc1, pc);
                }
            }
            if (subj_classes == null && obj_classes != null) {
                Iterator<Object> obj_classes_it = obj_classes.iterator();
                while (obj_classes_it.hasNext()) {
                    ResourceClass rc2 = (ResourceClass) obj_classes_it.next();
                    schema.addRangeDefinition(pc, rc2);
                }
            }
            if (subj_classes == null && obj_classes == null) {
                ResourceClass rc = schema.addResourceClass(RDFS.Resource.getURI(), RDFS.Resource.getNameSpace(), RDFS.Resource.getLocalName());
                schema.addDomainRangeDefinition(rc, pc, rc); 
            }
        } else {
            String uri = o.getLiteralDatatypeURI();
            String ns = SplitIRI.namespace(o.getLiteralDatatypeURI());
            String name = SplitIRI.localname(o.getLiteralDatatypeURI());
            ResourceClass rc2 = schema.addResourceClass(uri, ns, name);
            rc2.setAsDatatype();
            HashSet subj_classes = mm_res_rcs.get(s.getURI());
            if (subj_classes != null) {
                Iterator<Object> subj_classes_it = subj_classes.iterator();
                while (subj_classes_it.hasNext()) {
                    ResourceClass rc1 = (ResourceClass) subj_classes_it.next();
                    schema.addDomainRangeDefinition(rc1, pc, rc2);
                }
            } else {
                ResourceClass rc1 = schema.addResourceClass(RDFS.Resource.getURI(), RDFS.Resource.getNameSpace(), RDFS.Resource.getLocalName());
                schema.addDomainRangeDefinition(rc1, pc, rc2);
            }
        }
    }
    
    private String getNodeString(Node node) {
        if (node.isURI()) {
            return node.getURI();
        }
        if (node.isBlank()) {
            return node.getBlankNodeLabel();
        }
        if (node.isLiteral()) {
            return node.getLiteral().toString();
        }
        return "";
    }    

    @Override
    public void quad(Quad quad) {
        //System.out.println("quad");
    }

    @Override
    public void base(String string) {
        //System.out.println("base");
    }

    @Override
    public void prefix(String string, String string1) {
        //System.out.println("prefix");
    }

    @Override
    public void finish() {
        /*
        File file = new File("temp.nt");
        if(file.exists()){
            file.delete();
        }*/
    }

}
