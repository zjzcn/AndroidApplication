package ru.tcgeo.application.views.callback;

import ru.tcgeo.application.views.viewholder.AttributesHolder;

/**
 * Created by artem on 14.07.17.
 */

public abstract class AttributesCallback {
    public abstract void onClick();

    public abstract void onAddClick();

    public abstract void onFieldChanged(AttributesHolder holder);
}