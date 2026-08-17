package com.deer.wcs.base.model;

import java.util.List;

public class WareModel {
    private List<Node> nodeDataArray;
    private List<Link> linkDataArray;

    public List<Node> getNodeDataArray() {
        return nodeDataArray;
    }

    public void setNodeDataArray(List<Node> nodeDataArray) {
        this.nodeDataArray = nodeDataArray;
    }

    public List<Link> getLinkDataArray() {
        return linkDataArray;
    }

    public void setLinkDataArray(List<Link> linkDataArray) {
        this.linkDataArray = linkDataArray;
    }
}

