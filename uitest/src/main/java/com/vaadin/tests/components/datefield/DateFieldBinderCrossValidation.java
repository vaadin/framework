package com.vaadin.tests.components.datefield;

import java.time.LocalDate;
import java.util.Objects;

import com.gargoylesoftware.htmlunit.javascript.host.html.FormField;

import com.vaadin.annotations.Widgetset;
import com.vaadin.data.Binder;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ErrorLevel;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class DateFieldBinderCrossValidation extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        Label label = new Label();
        label.setId("status");

        Binder<FromToModel> fromToModelBinder = new Binder<>();

        DateField fromField = new DateField();
        fromField.setId("from-field");
        fromField.setDateFormat("yyyy/MM/dd");
        final Binder.Binding<FromToModel, LocalDate> fromBinding = fromToModelBinder
                .forField(fromField).asRequired()
                .bind(FromToModel::getFromDate, FromToModel::setFromDate);
        DateField toField = new DateField();
        toField.setId("to-field");
        toField.setDateFormat("yyyy/MM/dd");
        final Binder.Binding<FromToModel, LocalDate> toBinding = fromToModelBinder
                .forField(toField).asRequired()
                .bind(FromToModel::getToDate, FromToModel::setToDate);

        fromField.addValueChangeListener(e -> {
            toField.setRangeStart(e.getValue());
            if (toField.getValue() != null) {
                toBinding.validate();
            }
            label.setValue("from field is " + fromField.getErrorMessage()
                    + ". To field is " + toField.getErrorMessage());
        });
        toField.addValueChangeListener(e -> {
            fromField.setRangeEnd(e.getValue());
            if (fromField.getValue() != null) {
                fromBinding.validate();
            }
            label.setValue("from field is " + fromField.getErrorMessage()
                    + ". To field is " + toField.getErrorMessage());
        });

        horizontalLayout.addComponents(fromField, toField, label);

        addComponents(horizontalLayout);
    }

    private static class FromToModel {

        private LocalDate fromDate;
        private LocalDate toDate;

        public LocalDate getFromDate() {
            return fromDate;
        }

        public void setFromDate(LocalDate fromDate) {
            this.fromDate = fromDate;
        }

        public LocalDate getToDate() {
            return toDate;
        }

        public void setToDate(LocalDate toDate) {
            this.toDate = toDate;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof FromToModel))
                return false;
            FromToModel that = (FromToModel) o;
            return Objects.equals(getFromDate(), that.getFromDate())
                    && Objects.equals(getToDate(), that.getToDate());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getFromDate(), getToDate());
        }
    }
}
