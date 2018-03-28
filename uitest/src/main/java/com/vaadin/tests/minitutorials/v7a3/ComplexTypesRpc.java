package com.vaadin.tests.minitutorials.v7a3;

import java.util.List;
import java.util.Map;

import com.vaadin.shared.Connector;
import com.vaadin.shared.communication.ClientRpc;

public interface ComplexTypesRpc extends ClientRpc {
    public void sendComplexTypes(List<String> list,
            Map<String, Integer> stringMap, Map<Integer, String> otherMap,
            Map<Connector, String> connectorMap, boolean[] bits,
            List<List<Double>> matrix, ComplexTypesBean bean);
}
