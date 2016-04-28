package com.vaadin.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.shared.util.SharedUtil;

public class ApplicationConnectionURLGenerationTest {

    private static final String[] URIS = new String[] {
            "http://demo.vaadin.com/", //
            "https://demo.vaadin.com/",
            "http://demo.vaadin.com/foo",
            "http://demo.vaadin.com/foo?f",
            "http://demo.vaadin.com/foo?f=1",
            "http://demo.vaadin.com:1234/foo?a",
            "http://demo.vaadin.com:1234/foo#frag?fakeparam",
            // Jetspeed
            "http://localhost:8080/jetspeed/portal/_ns:Z3RlbXBsYXRlLXRvcDJfX3BhZ2UtdGVtcGxhdGVfX2RwLTFfX1AtMTJjNTRkYjdlYjUtMTAwMDJ8YzB8ZDF8aVVJREx8Zg__",
            // Liferay generated url
            "http://vaadin.com/directory?p_p_id=Directory_WAR_Directory&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_resource_id=UIDL&p_p_cacheability=cacheLevelPage&p_p_col_id=row-1&p_p_col_count=1",

    };
    private static final String[] URIS_WITH_ABCD_PARAM = new String[] {
            "http://demo.vaadin.com/?a=b&c=d",
            "https://demo.vaadin.com/?a=b&c=d",
            "http://demo.vaadin.com/foo?a=b&c=d",
            "http://demo.vaadin.com/foo?f&a=b&c=d",
            "http://demo.vaadin.com/foo?f=1&a=b&c=d",
            "http://demo.vaadin.com:1234/foo?a&a=b&c=d",
            "http://demo.vaadin.com:1234/foo?a=b&c=d#frag?fakeparam",
            "http://localhost:8080/jetspeed/portal/_ns:Z3RlbXBsYXRlLXRvcDJfX3BhZ2UtdGVtcGxhdGVfX2RwLTFfX1AtMTJjNTRkYjdlYjUtMTAwMDJ8YzB8ZDF8aVVJREx8Zg__?a=b&c=d",
            "http://vaadin.com/directory?p_p_id=Directory_WAR_Directory&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_resource_id=UIDL&p_p_cacheability=cacheLevelPage&p_p_col_id=row-1&p_p_col_count=1&a=b&c=d",

    };

    private static final String[] URIS_WITH_ABCD_PARAM_AND_FRAGMENT = new String[] {
            "http://demo.vaadin.com/?a=b&c=d#fragment",
            "https://demo.vaadin.com/?a=b&c=d#fragment",
            "http://demo.vaadin.com/foo?a=b&c=d#fragment",
            "http://demo.vaadin.com/foo?f&a=b&c=d#fragment",
            "http://demo.vaadin.com/foo?f=1&a=b&c=d#fragment",
            "http://demo.vaadin.com:1234/foo?a&a=b&c=d#fragment",
            "",
            "http://localhost:8080/jetspeed/portal/_ns:Z3RlbXBsYXRlLXRvcDJfX3BhZ2UtdGVtcGxhdGVfX2RwLTFfX1AtMTJjNTRkYjdlYjUtMTAwMDJ8YzB8ZDF8aVVJREx8Zg__?a=b&c=d#fragment",
            "http://vaadin.com/directory?p_p_id=Directory_WAR_Directory&p_p_lifecycle=2&p_p_state=normal&p_p_mode=view&p_p_resource_id=UIDL&p_p_cacheability=cacheLevelPage&p_p_col_id=row-1&p_p_col_count=1&a=b&c=d#fragment",

    };

    @Test
    public void testParameterAdding() {
        for (int i = 0; i < URIS.length; i++) {
            // Adding nothing
            assertEquals(URIS[i], SharedUtil.addGetParameters(URIS[i], ""));

            // Adding a=b&c=d
            assertEquals(URIS_WITH_ABCD_PARAM[i],
                    SharedUtil.addGetParameters(URIS[i], "a=b&c=d"));

            // Fragments
            if (URIS_WITH_ABCD_PARAM_AND_FRAGMENT[i].length() > 0) {
                assertEquals(URIS_WITH_ABCD_PARAM_AND_FRAGMENT[i],
                        SharedUtil.addGetParameters(URIS[i] + "#fragment",
                                "a=b&c=d"));

                // Empty fragment
                assertEquals(URIS_WITH_ABCD_PARAM_AND_FRAGMENT[i].replace(
                        "#fragment", "#"), SharedUtil.addGetParameters(URIS[i]
                        + "#", "a=b&c=d"));
            }
        }
    }
}
