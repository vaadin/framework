package com.vaadin.sass;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.vaadin.sass.testcases.css.Comments;
import com.vaadin.sass.testcases.css.Media;
import com.vaadin.sass.testcases.css.Properties;
import com.vaadin.sass.testcases.css.Reindeer;
import com.vaadin.sass.testcases.css.Selectors;

@RunWith(Suite.class)
@SuiteClasses({ Selectors.class, Properties.class, Reindeer.class, Media.class,
        Comments.class })
public class CssTestSuite {
}
