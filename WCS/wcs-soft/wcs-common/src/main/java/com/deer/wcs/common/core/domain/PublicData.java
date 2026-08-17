package com.deer.wcs.common.core.domain;

import java.util.List;

public class PublicData {
    private String value;
    private String label;

    public PublicData(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


}
