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

public class RDFSProcessor {
    
    public static void main(String[] args) {
        //BasicConfigurator.configure(); //to avoid log4j warning
        System.out.println("rdfs-processor");
        System.out.println("Java app to extract the RDF Schema from an RDF file.");
        if (args.length == 1) {
            long itime = System.currentTimeMillis();
            String inputFileName = String.valueOf(args[0]);
            System.out.println("Input file name: " + inputFileName);
            System.out.println("Begin");
            Extractor extractor = new Extractor();
            Schema schema = extractor.run(inputFileName);
            schema.write("schema.ttl");
            System.out.println("End");
            System.out.println("Output: instance.nt, schema.ttl");
            long etime = System.currentTimeMillis() - itime;
            System.out.println("Execution time: " + etime + " ms \n");            
        } else {
            System.out.println("Usage:");
            System.out.println("java -jar rdfs-processor <RDF_filename>");
            System.out.println("Output: instance.nt, schema.ttl");
        }
    }    
    
}
