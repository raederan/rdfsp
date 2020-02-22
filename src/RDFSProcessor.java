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
        if (args.length == 1) {
            String inputFileName = String.valueOf(args[0]);
            String outputDirectory = "./output";
            String outputFileName = outputDirectory + "/schema.ttl";
            System.out.println("Input file name: " + inputFileName);
            System.out.println("Output directory: " + outputDirectory);
            System.out.println("Begin");
            Extractor extractor = new Extractor();
            Schema schema = extractor.run(inputFileName,outputDirectory);
            schema.write(outputFileName);
            System.out.println("End");
        } else if (args.length == 2) {
            String inputFileName = String.valueOf(args[0]);
            String outputDirectory = String.valueOf(args[1]);
            String outputFileName = outputDirectory + "/schema.ttl";
            System.out.println("Input file name: " + inputFileName);
            System.out.println("Output directory: " + outputDirectory);
            System.out.println("Begin");
            Extractor extractor = new Extractor();
            Schema schema = extractor.run(inputFileName,outputDirectory);
            schema.write(outputFileName);
            System.out.println("End");
        } else {
            System.out.println("Usage:");
            System.out.println("java -jar RDFSExtractor <input_RDF_file> <output_directory>");
            System.out.println("The default output_directory is './output'.");
        }
    }    
    
}
