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

import java.io.File;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.tdb.TDBLoader;
import org.apache.jena.tdb.store.DatasetGraphTDB;
import org.apache.jena.tdb.sys.TDBInternal;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;

public class RdfValidator {

    public boolean validate(String inputFileName) {
        // create default model
        Model model = ModelFactory.createDefaultModel();
        // use the FileManager to find the input file
        InputStream in = FileManager.get().open(inputFileName);
        if (in == null) {
            throw new IllegalArgumentException(
                    "File: " + inputFileName + " not found");
        }
        // read the model
        FileManager.get().readModel(model, inputFileName);

        int valid = 0;
        int invalid = 0;
        StmtIterator sit1 = model.listStatements();
        while (sit1.hasNext()) {
            Statement triple = sit1.next();
            
            //Validate that a triple satisfies the structure:
            //Subject = IRI or blank node
            //Predicate = IRI
           // Object = IRI, literal or blank node
            
            if (!triple.getSubject().isResource()) {
                System.out.println("Error: the subject must be a resource (IRI or blank node)");
                System.out.println(triple.toString());
                invalid++;
                continue;
            }
            if (!triple.getPredicate().isURIResource()) {
                System.out.println("Error: the predicate must be IRI");
                System.out.println(triple.toString());
                invalid++;
                continue;
            }
            if (!(triple.getObject().isResource() || triple.getObject().isLiteral())) {
                System.out.println("Error: the object must be either a resource (IRI or blank node) or a literal");
                System.out.println(triple.toString());
                invalid++;
                continue;
            }
            
            Resource pred = triple.getPredicate().asResource();
            //Validate RDF Schema terms
            if (pred.asResource().equals(RDF.type) || pred.asResource().equals(RDFS.domain) || pred.asResource().equals(RDFS.range)) {
                if (!triple.getObject().isURIResource()) {
                    System.out.println("Error: the object must be a URI resource");
                    System.out.println(triple.toString());
                    invalid++;
                    continue;

                }
            }
            valid++;
        }
        return invalid <= 0;
    }

}
