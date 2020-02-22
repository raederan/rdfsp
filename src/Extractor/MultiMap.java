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

import RDFSchema.ResourceClass;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class MultiMap {
    private HashMap<Object, HashSet<Object>> hm = new HashMap();    
    
    public MultiMap(){
    }
    
    public boolean isEmpty(){
        return hm.isEmpty();
    }
    
    public boolean containsKey(Object key){
        return hm.containsKey(key);
    }    
    
    public void put(Object key, Object value){
        HashSet<Object> values = hm.get(key);
        if(values == null){
            HashSet<Object> hset = new HashSet();
            hset.add(value);
            hm.put(key, hset);
        }else{
            values.add(value);
        }
    }
    
    public HashSet get(Object key){
        return hm.get(key);
    }
    
    public void show(){
        for (Object key : hm.keySet()) {
            System.out.println("key: " + key.toString());
            System.out.println("Values:");
            HashSet set = hm.get(key);
            Iterator<Object> it = set.iterator();
            while(it.hasNext()){
                System.out.print(it.next().toString());
            }
            System.out.println("\n");
        }
    }

    
}
