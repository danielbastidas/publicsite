package com.smartmatic.graph.service;

import com.smartmatic.graph.spec.IRegion;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.HashMap;
import java.util.Map;

@Singleton
@Startup
public class RegionBean implements IRegion {
        
        private Map<String, String[]> regionsByParent;
        
        public RegionBean() {
                regionsByParent = new HashMap<String, String[]>();
                regionsByParent.put("Country", new String[]{"State1"});
                regionsByParent.put("State1", new String[]{"Municipality1", "Municipality2"});
                regionsByParent.put("Municipality1", new String[]{"Parish1"});
                regionsByParent.put("Parish1", new String[]{"PollingPlace1"});
                regionsByParent.put("PollingPlace1", new String[]{"Tally1", "Tally2"});
                regionsByParent.put("Municipality2", new String[]{"Tally3", "Tally4"});
        }
        
        public String[] getChildren(String parent) {
                return regionsByParent.get(parent);
        }
        
}
