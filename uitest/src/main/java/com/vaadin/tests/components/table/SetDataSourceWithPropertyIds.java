package com.vaadin.tests.components.table;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CacheUpdateException;

public class SetDataSourceWithPropertyIds extends AbstractTestUI {

    @Override
    protected String getTestDescription() {
        return "It should be possible to set a dataSource without generating columns that were never intended to be visible.<br>"
                + "First initialization happens before the table is attached.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10419;
    }

    private static final String TABLE_NAME = "JOBS";
    private static final String[] PK_COLUMN_NAMES = new String[] { "JOB_ID" };
    private static final String SEQUENCE_NAME = "";
    private static final String VERSION_COLUMN_NAME = "";

    Table table = new Table();
    BeanItemContainer<JobsBean> jobContainer = new BeanItemContainer(
            JobsBean.class);
    Label label = new Label();

    @Override
    protected void setup(VaadinRequest request) {
        getLayout().setSpacing(true);
        getLayout().setMargin(new MarginInfo(true, false, false, false));

        Button button = new Button("Toggle editability");
        button.setId("button");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                refreshTable();
            }
        });

        label.setSizeUndefined();
        label.setId("label");

        table.setId("table");
        refreshTable();

        addComponent(button);
        addComponent(label);
        addComponent(table);
    }

    private void refreshTable() {
        // error only occurs when table is editable and already attached
        table.setEditable(table.getParent() == null || !table.isEditable());

        jobContainer.removeAllItems();
        jobContainer.addAll(getBeanList());
        try {
            table.setContainerDataSource(jobContainer);
            table.setVisibleColumns(new String[] { "jobId" });
            label.setValue("no Exception");
        } catch (CacheUpdateException e) {
            ArrayList<String> propertyIds = new ArrayList<String>();
            propertyIds.add("jobId");
            table.setContainerDataSource(jobContainer, propertyIds);
            label.setValue("Exception caught");
        }
    }

    private List<JobsBean> getBeanList() {

        List<JobsBean> list = new ArrayList<JobsBean>();
        JobsBean jobsBean = new JobsBean();
        jobsBean.setJobId("1");
        list.add(jobsBean);
        return list;
    }

    public class JobsBean<T> implements Serializable {

        private static final long serialVersionUID = 1932918476339138393L;
        protected String jobId;
        protected String jobTitle;
        protected Long minSalary;
        protected Long maxSalary;
        private T auxiliaryData;

        public T getAuxiliaryData() {
            return auxiliaryData;
        }

        public void setAuxiliaryData(final T pAuxiliaryData) {
            auxiliaryData = pAuxiliaryData;
        }

        public String getTableName() {
            return TABLE_NAME;
        }

        public String[] getPrimaryKeyColumnNames() {
            return PK_COLUMN_NAMES;
        }

        public String getSequenceName() {
            return SEQUENCE_NAME;
        }

        public String getVersionColumnName() {
            return VERSION_COLUMN_NAME;
        }

        public String getJobId() {
            return jobId;
        }

        public void setJobId(final String pJobId) {
            jobId = pJobId;
        }

        public String getJobTitle() {
            return jobTitle;
        }

        public void setJobTitle(final String pJobTitle) {
            jobTitle = pJobTitle;
        }

        public Long getMinSalary() {
            return minSalary;
        }

        public void setMinSalary(final Long pMinSalary) {
            minSalary = pMinSalary;
        }

        public Long getMaxSalary() {
            return maxSalary;
        }

        public void setMaxSalary(final Long pMaxSalary) {
            maxSalary = pMaxSalary;
        }

        @Override
        public boolean equals(Object pObject) {
            if (this == pObject) {
                return true;
            }
            if (!(pObject instanceof JobsBean)) {
                return false;
            }
            JobsBean other = (JobsBean) pObject;
            return getJobId().equals(other.getJobId());
        }

        @Override
        public int hashCode() {
            return getJobId().hashCode();
        }
    }

}
