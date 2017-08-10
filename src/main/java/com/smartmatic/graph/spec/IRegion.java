package com.smartmatic.graph.spec;

import javax.ejb.Local;

@Local
public interface IRegion {
        public String[] getChildren(String parent);
}
