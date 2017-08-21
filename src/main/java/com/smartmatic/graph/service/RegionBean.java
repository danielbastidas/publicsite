package com.smartmatic.graph.service;

import com.smartmatic.graph.spec.IRegion;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        @Override
        public String[] getChildren(String parent) {
                return regionsByParent.get(parent);
        }

        @Override
        public String getParent(String region) {

                String parentRegion = null;
                List<String> regions = regionsByParent.entrySet().stream().filter(entry ->
                        Arrays.asList(entry.getValue()).contains(region)).
                        map(Map.Entry::getKey).collect(Collectors.toList());

                if (regions != null && regions.size() >= 1) {
                        parentRegion = regions.get(0);
                }

                return parentRegion;
        }

}
