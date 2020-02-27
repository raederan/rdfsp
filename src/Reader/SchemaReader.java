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
package Reader;

import RDFSchema.PropertyClass;
import RDFSchema.ResourceClass;
import RDFSchema.Schema;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

/**
 *
 * @author renzo
 */
public class SchemaReader {

    public SchemaReader() {

    }

    public Schema run(String input_schema_filename) {
        Schema schema = new Schema();
        Model model = ModelFactory.createDefaultModel();
        InputStream in = FileManager.get().open(input_schema_filename);
        if (in == null) {
            throw new IllegalArgumentException(
                    "File: " + input_schema_filename + " not found");
        }

        // read the model
        FileManager.get().readModel(model, input_schema_filename);
        StmtIterator sit = model.listStatements();
        while (sit.hasNext()) {
            Triple triple = sit.next().asTriple();
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
                } else {
                    System.out.println("Warning: The following RDF triple was not processed.");
                    System.out.println(this.getNodeString(s) + " " + this.getNodeString(p) + " " + this.getNodeString(o));
                }
                continue;
            }

            // Analysis of (pc,rdfs:domain,rc)
            if (p.hasURI(RDFS.domain.getURI())) {
                if (s.isURI() && o.isURI()) {
                    PropertyClass pc = schema.addPropertyClass(s.getURI(), s.getNameSpace(), s.getLocalName());
                    ResourceClass rc = schema.addResourceClass(o.getURI(), o.getNameSpace(), o.getLocalName());
                    schema.addDomainDefinition(rc, pc);
                } else {
                    System.out.println("Error: Bad formed RDF triple");
                    System.out.println(getNodeString(s) + " rdfs:domain " + getNodeString(o));
                }
                continue;
            }

            // Analysis of (pc,rdfs:range,rc)  
            if (p.hasURI(RDFS.range.getURI())) {
                if (s.isURI() && o.isURI()) {
                    PropertyClass pc = schema.addPropertyClass(s.getURI(), s.getNameSpace(), s.getLocalName());
                    ResourceClass rc = schema.addResourceClass(o.getURI(), o.getNameSpace(), o.getLocalName());
                    schema.addRangeDefinition(pc, rc);
                    if (this.isXsdDatatype(rc.getUri())) {
                        rc.setAsDatatype();
                    }
                } else {
                    System.out.println("Error: Bad formed RDF triple");
                    System.out.println(getNodeString(s) + " rdfs:range " + getNodeString(o));
                }
                continue;
            }

            System.out.println("Warning: The following RDF triple was not processed.");
            System.out.println(this.getNodeString(s) + " " + this.getNodeString(p) + " " + this.getNodeString(o));

        }

        return schema;
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

}
