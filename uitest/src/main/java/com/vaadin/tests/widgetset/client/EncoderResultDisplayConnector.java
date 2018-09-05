package com.vaadin.tests.widgetset.client;

import java.util.Arrays;
import java.util.List;

import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.JsonEncoder;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.client.metadata.Type;
import com.vaadin.client.metadata.TypeData;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.shared.ui.Connect;
import com.vaadin.tests.serialization.EncodeResultDisplay.EncoderResultDisplayExtension;

import elemental.json.JsonValue;

@Connect(EncoderResultDisplayExtension.class)
public class EncoderResultDisplayConnector extends AbstractExtensionConnector {

    private ReportRpc reporter;

    public interface ReportRpc extends ServerRpc {
        public void report(String name, String encodedValue);
    }

    @Override
    protected void extend(ServerConnector target) {
        reporter = getRpcProxy(ReportRpc.class);

        reportEncode("My string");
        reportEncode(Character.valueOf('v'));
        reportEncode(Byte.valueOf((byte) 1));
        reportEncode(Integer.valueOf(3));
        reportEncode(Long.valueOf(Integer.MAX_VALUE + 1l));
        reportEncode(Float.valueOf((float) 1.1));
        reportEncode(Double.valueOf("2.2"));

        reportEncode(new String[] { "One", "Two" });
        reportEncode("List", Arrays.asList("Three", "Four"),
                new Type(List.class.getName(),
                        new Type[] { TypeData.getType(String.class) }));
        reportEncode(new SimpleTestBean(5));

        reportEncode(Void.class.getSimpleName(), null,
                TypeData.getType(Void.class));
    }

    private void reportEncode(Object value) {
        Type type = TypeData.getType(value.getClass());
        reportEncode(value.getClass().getSimpleName(), value, type);
    }

    private void reportEncode(String name, Object value, Type type) {
        JsonValue encodedValue = JsonEncoder.encode(value, type,
                getConnection());
        reporter.report(name, encodedValue.toJson());
    }

}
