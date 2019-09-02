package com.lecso2.bamcompany.rwsectionindexer;

import java.util.Objects;

public class Model {

    private int id;
    private String text;

    public Model(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Model model = (Model) o;
        return id == model.id &&
                Objects.equals(text, model.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text);
    }
}
