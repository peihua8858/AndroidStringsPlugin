package com.fz.plugin.bean;

import org.jetbrains.annotations.NotNull;

import javax.swing.tree.DefaultMutableTreeNode;

public class MutableTreeNode extends DefaultMutableTreeNode implements Comparable<MutableTreeNode> {
    public MutableTreeNode() {
    }

    public MutableTreeNode(Object userObject) {
        super(userObject);
    }

    public MutableTreeNode(Object userObject, boolean allowsChildren) {
        super(userObject, allowsChildren);
    }

    @Override
    public int compareTo(@NotNull MutableTreeNode o) {
        if (this.userObject instanceof String && o.userObject instanceof String) {
            String o1 = (String) this.userObject;
            String o2 = (String) o.userObject;
            return o1.compareTo(o2);
        }
        return 0;
    }
}
