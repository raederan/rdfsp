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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.sparql.core.Quad;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

public class Reader1 implements StreamRDF {

    private Writer writer1;
    private Writer writer2;
    
    private Schema schema;
    //multimap to store pairs <resource,{Class1,...,ClassN}>
    private MultiMap mm_res_rcs;

    public Reader1(Schema _schema, MultiMap mm) {
        schema = _schema;
        mm_res_rcs = mm;
    }

    @Override
    public void start() {
        File file1 = new File("temp.nt");
        if(file1.exists()){
            file1.delete();
        }
        try {
            writer1 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("temp.nt"), "UTF-8"));
        } catch (Exception ex) {
            System.out.println("Error (Reader1): Failed to create the file temp.nt ");

        }
        
        File file2 = new File("instance.nt");
        if(file2.exists()){
            file2.delete();
        }
        try {
            writer2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("instance.nt"), "UTF-8"));
        } catch (Exception ex) {
            System.out.println("Error (Reader1): Failed to create the file instance.nt ");

        }
    }

    @Override
    public void triple(Triple triple) {
        Node s = triple.getSubject();
        Node p = triple.getPredicate();
        Node o = triple.getObject();

        //Discovery of resource classes and property classes
        if (p.isURI() && p.hasURI(RDF.type.getURI())) {
            if (s.isURI() && o.hasURI(RDFS.Class.getURI())) {
                // (rc,rdf:type,rdfs:Class)
                schema.addResourceClass(s.getURI(), s.getNameSpace(), s.getLocalName());
            } else if (s.isURI() && o.hasURI(RDF.Property.getURI())) {
                // (pc:rdf:type,rdfs:Property)
                schema.addPropertyClass(s.getURI(), s.getNameSpace(), s.getLocalName());
            } else if (s.isURI() && o.isURI()) {
                // (uri,rdf:type,uri)
                ResourceClass rc = schema.addResourceClass(o.getURI(), o.getNameSpace(), o.getLocalName());
                mm_res_rcs.put(s.getURI(), rc);
                this.writeTriple(s,p,o);
            }else if(s.isBlank() && o.isURI()){
                // (bnode,rdf:type,uri)
                ResourceClass rc = schema.addResourceClass(o.getURI(), o.getNameSpace(), o.getLocalName());
                String uri = "http://bnode/" + s.hashCode();
                mm_res_rcs.put(uri, rc);
                if (o.hasURI(RDFS.Datatype.getURI())) {
                    rc.setAsDatatype();
                }
            } else {
                System.out.println("Warning: Bad formed RDF triple.");
                System.out.println(this.getNodeLabel1(s) + " " + this.getNodeLabel1(p) + " " + this.getNodeLabel1(o));
            }
            return;
        }

        // Analysis of (pc,rdfs:domain,rc)
        if (p.hasURI(RDFS.domain.getURI())) {
            if (s.isURI() && o.isURI()) {
                PropertyClass pc = schema.addPropertyClass(s.getURI(), s.getNameSpace(), s.getLocalName());
                ResourceClass rc = schema.addResourceClass(o.getURI(), o.getNameSpace(), o.getLocalName());
                schema.addDomainDefinition(rc, pc);
            } else {
                System.out.println("Error: Bad formed RDF triple");
                System.out.println(getNodeLabel1(s) + " rdfs:domain " + getNodeLabel1(o));
            }
            return;
        }

        // Analysis of (pc,rdfs:range,rc)  
        if (p.hasURI(RDFS.range.getURI())) {
            if (s.isURI() && o.isURI()) {
                PropertyClass pc = schema.addPropertyClass(s.getURI(), s.getNameSpace(), s.getLocalName());
                ResourceClass rc = schema.addResourceClass(o.getURI(), o.getNameSpace(), o.getLocalName());
                schema.addRangeDefinition(pc,rc);
                if (this.isXsdDatatype(rc.getUri())) {
                    rc.setAsDatatype();
                }
            } else {
                System.out.println("Error: Bad formed RDF triple");
                System.out.println(getNodeLabel1(s) + " rdfs:range " + getNodeLabel1(o));
            }
            return;
        }
        
        if(p.isURI() && p.hasURI(RDFS.subClassOf.getURI())){
            // ToDo
            return;
        }

        if(p.isURI() && p.hasURI(RDFS.subPropertyOf.getURI())){
            // ToDo
            return;
        }
        
        if(p.isURI() && p.hasURI(RDFS.label.getURI())){
            // ToDo
            return;
        }        
        
        if(p.isURI() && p.hasURI(RDFS.comment.getURI())){
            // ToDo
            return;
        }
        
        if(p.isURI() && p.hasURI(RDFS.member.getURI())){
            // ToDo
            return;
        }
        
        if(p.isURI() && p.hasURI(RDFS.seeAlso.getURI())){
            // ToDo
            return;
        }
        
        if(p.isURI() && p.hasURI(RDFS.isDefinedBy.getURI())){
            // ToDo
            return;
        }
        
        // If triple is simple data, then write to instance file
        this.writeTriple(s,p,o);
        
    }
    
    private void writeTriple(Node s, Node p, Node o){
        String line;
        
        line = getNodeLabel2(s) + " " + getNodeLabel2(p) + " " + getNodeLabel2(o) + " .\n";
        try {
            writer1.write(line);
        } catch (IOException ex) {
            System.out.println("Error (Reader1): Failed writing line in file temp.nt");
        }
        
        line = getNodeLabel1(s) + " " + getNodeLabel1(p) + " " + getNodeLabel1(o) + " .\n";
        try {
            writer2.write(line);
        } catch (IOException ex) {
            System.out.println("Error (Reader1): Failed writing line in file instance.nt");
        }
        
    }

    private String getNodeLabel1(Node node) {
        if (node.isURI()) {
            return "<" + node.getURI() + ">";
        }
        if (node.isBlank()) {
            return "_:b" + node.hashCode();
        }
        if (node.isLiteral()) {
            return "\"" + node.getLiteralValue() + "\"^^<" + node.getLiteralDatatypeURI() + ">";
        }
        return "";
    }

    private String getNodeLabel2(Node node) {
        if (node.isURI()) {
            return "<" + node.getURI() + ">";
        }
        if (node.isBlank()) {
            return "<http://bnode/" + node.hashCode() + ">";
        }
        if (node.isLiteral()) {
            return "\"" + node.getLiteralValue() + "\"^^<" + node.getLiteralDatatypeURI() + ">";
        }
        return "";
    }

    private boolean isXsdDatatype(String uri) {
        ArrayList<String> datatypes = new ArrayList();
        datatypes.add(XSDDatatype.XSDanyURI.getURI());
        datatypes.add(XSDDatatype.XSDboolean.getURI());
        datatypes.add(XSDDatatype.XSDdate.getURI());
        datatypes.add(XSDDatatype.XSDstring.getURI());
        datatypes.add(XSDDatatype.XSDint.getURI());
        datatypes.add(XSDDatatype.XSDinteger.getURI());
        datatypes.add(XSDDatatype.XSDboolean.getURI());
        datatypes.add(XSDDatatype.XSDboolean.getURI());
        return datatypes.contains(uri);
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
        try {
            writer1.close();
        } catch (IOException ex) {
            System.out.println("Error (Reader1): Failed closing the file temp.nt");
        }
        try {
            writer2.close();
        } catch (IOException ex) {
            System.out.println("Error (Reader1): Failed closing the file instance.nt");
        }        
    }

}
