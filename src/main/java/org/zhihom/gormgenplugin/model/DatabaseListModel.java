package org.zhihom.gormgenplugin.model;

import javax.swing.*;
import java.util.List;

public class DatabaseListModel<E> extends AbstractListModel<E> {

    private List<E> models;

    public DatabaseListModel(List<E> models) {
        super();
        this.models = models;
    }

    @Override
    public int getSize() {
        return this.models.size();
    }

    @Override
    public E getElementAt(int index) {
        if (index < 0 || index >= getSize()) {
            return null;
        }
        return models.get(index);
    }

    public void addElement(E element) {
        int index = models.size();
        models.add(element);
        fireIntervalAdded(this, index, index);
    }

}
