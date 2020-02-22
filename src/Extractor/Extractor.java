package Extractor;

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
import RDFSchema.Schema;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.util.FileManager;

public class Extractor {

    public Extractor() {

    }

    public Schema run(String inputFileName) {
        return this.run(inputFileName, "./output");
    }
    
    public Schema run(String inputFileName, String outputDirectory) {
        Schema schema = new Schema();
        MultiMap mm_res_rcs = new MultiMap();
        try {
            File directory = new File(outputDirectory);
            if (!directory.exists()) {
                directory.mkdir();
            }

            // Exec phase 1
            InputStream in1 = FileManager.get().open(inputFileName);
            Reader1 reader1 = new Reader1(schema, mm_res_rcs, outputDirectory);
            RDFDataMgr.parse(reader1, in1, Lang.TTL);
            if (in1 == null) {
                throw new IllegalArgumentException("Error (Extractor): File " + inputFileName + " not found");
            }
            in1.close();

            // Exec phase 2
            String instance_file = outputDirectory + "/instance.nt";
            InputStream in2 = FileManager.get().open(instance_file);
            Reader2 reader2 = new Reader2(schema, mm_res_rcs);
            RDFDataMgr.parse(reader2, in2, Lang.NT);
            if (in2 == null) {
                throw new IllegalArgumentException("Error (Extractor): File " + inputFileName + " not found");
            }
            in2.close();

            schema.normalize();

        } catch (Exception ex) {
            System.out.println("Error Extractor.run():" + ex.getMessage());
        }

        return schema;
    }

    private String getNodeString(RDFNode node) {
        if (node.isURIResource()) {
            return node.asResource().getURI();
        }
        if (node.isAnon()) {
            return node.asResource().getId().toString();
        }
        if (node.isLiteral()) {
            return node.asLiteral().getString();
        }
        return "Error";
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
