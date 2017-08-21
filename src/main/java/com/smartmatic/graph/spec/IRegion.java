package com.smartmatic.graph.spec;

import javax.ejb.Local;

@Local
public interface IRegion {
        String[] getChildren(String parent);

        String getParent(String region);
}
