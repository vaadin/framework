package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;

public class ComboBoxPasteFilter extends AbstractTestUI {

    private static final String WORDS = "loutish offer popcorn bitter buzz "
            + "change mass boy erect aquatic donkey gentle colorful zippy "
            + "soup pocket bathe fear supreme pan present knife quartz shy "
            + "conscious tested thumb snow evasive reason dusty bridge giddy "
            + "smooth bomb endurable tiger red gun fix regret quizzical income "
            + "careless owe sleet loss silent serious play consider messy "
            + "retire reduce shaky shiny low suggest preach bleach drunk "
            + "talk instruct peck hungry improve meat chop title encourage "
            + "marry island romantic fabulous kneel guarantee dock complain "
            + "mate tour intend geese hole swing mine superb level slip "
            + "spoon sky live nine open playground guard possible hate "
            + "spotless apparatus bow";

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox<String> box = new ComboBox<String>("Paste a word from below");
        Collection<String> massiveData = massiveData();
        box.setDataProvider(new ListDataProvider<String>(massiveData));
        addComponent(box);

        Label label = new Label(WORDS);
        label.setSizeFull();
        addComponent(label);
    }

    private Collection<String> massiveData() {
        return find(WORDS, "([\\w]+)");
    }

    public List<String> find(String text, String patternStr) {
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(text);
        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }

    @Override
    protected Integer getTicketNumber() {
        return 11779;
    }

    @Override
    protected String getTestDescription() {
        return "ComboBox should filter regardless of whether the value is "
                + "pasted with keyboard or with mouse";
    }
}
