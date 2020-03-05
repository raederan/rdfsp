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
import Extractor.Extractor;
import RDFSchema.Schema;
import Reader.SchemaReader;

public class RDFSProcessor {

    public static void main(String[] args) {
        //BasicConfigurator.configure(); //to avoid log4j warning
        System.out.println("rdfs-processor");
        System.out.println("Java app to analyze, normalize or extract the RDF Schema from an RDF file.");
        if (args.length == 2) {
            long itime = System.currentTimeMillis();
            String opt = String.valueOf(args[0]);
            String inputFileName = String.valueOf(args[1]);
            System.out.println("Input file name: " + inputFileName);
            if (opt.compareTo("-a") == 0) {
                System.out.println("Executing RDF schema analysis ...");
                SchemaReader reader = new SchemaReader();
                Schema schema = reader.run(inputFileName);
                System.out.println("Number of resource classes: " + schema.countResourceClasses());
                System.out.println("Number of property classes: " + schema.countProperyClasses());
                System.out.println("Number of datatype classes: " + schema.countDatatypes());
            } else if (opt.compareTo("-n") == 0) {
                System.out.println("Executing RDF schema normalization ...");
                SchemaReader reader = new SchemaReader();
                Schema schema = reader.run(inputFileName);
                schema.normalize();
                System.out.println("Number of resource classes: " + schema.countResourceClasses());
                System.out.println("Number of property classes: " + schema.countProperyClasses());
                System.out.println("Number of datatype classes: " + schema.countDatatypes());
                schema.write("schema.ttl");
                System.out.println("Output: schema.ttl");
            } else if (opt.compareTo("-d") == 0) {
                System.out.println("Executing RDF schema discovery ...");
                Extractor extractor = new Extractor();
                Schema schema = extractor.run(inputFileName);
                schema.normalize();
                System.out.println("Number of resource classes: " + schema.countResourceClasses());
                System.out.println("Number of property classes: " + schema.countProperyClasses());
                System.out.println("Number of datatype classes: " + schema.countDatatypes());
                schema.write("schema.ttl");
                System.out.println("Output: instance.nt, schema.ttl");
            } else {
                System.out.println("Invalid option");
            }
            long etime = System.currentTimeMillis() - itime;
            System.out.println("Execution time: " + etime + " ms \n");
        } else {
            System.out.println("Usage:");
            System.out.println("// RDF Schema analysis");
            System.out.println("java -jar rdfs-processor -a <RDFS_filename>");
            System.out.println("// RDF Schema normalization");
            System.out.println("java -jar rdfs-processor -n <RDFS_filename>");
            System.out.println("// RDF Schema discovery");
            System.out.println("java -jar rdfs-processor -d <RDF_filename>");
            System.out.println("Output: instance.nt, schema.ttl");
        }
    }

}
