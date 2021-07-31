package com.fz.plugin.bean;

import javax.swing.*;

public class ComboBoxListModel extends DefaultComboBoxModel<ComboBoxModelBean> {
    @Override
    public ComboBoxModelBean getSelectedItem() {
        ComboBoxModelBean selectedJob = (ComboBoxModelBean) super.getSelectedItem();
        // do something with this job before returning...
        return selectedJob;
    }
}
