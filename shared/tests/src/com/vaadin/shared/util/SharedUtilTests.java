package com.vaadin.shared.util;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SharedUtilTests {

    private SharedUtil sut;

    @Before
    public void setup() {
        sut = new SharedUtil();
    }

    @Test
    public void trailingSlashIsTrimmed() {
        assertThat(sut.trimTrailingSlashes("/path/"), is("/path"));
    }

    @Test
    public void noTrailingSlashForTrimming() {
        assertThat(sut.trimTrailingSlashes("/path"), is("/path"));
    }

    @Test
    public void trailingSlashesAreTrimmed() {
        assertThat(sut.trimTrailingSlashes("/path///"), is("/path"));
    }

    @Test
    public void emptyStringIsHandled() {
        assertThat(sut.trimTrailingSlashes(""), is(""));
    }

    @Test
    public void rootSlashIsTrimmed() {
        assertThat(sut.trimTrailingSlashes("/"), is(""));
    }

}
