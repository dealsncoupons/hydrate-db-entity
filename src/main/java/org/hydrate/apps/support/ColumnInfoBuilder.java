package org.hydrate.apps.support;

public class ColumnInfoBuilder {

    private String name;
    private String column;
    private Class<?> type;
    private String joinTable;
    private boolean isPk = false;
    private boolean isFk = false;
    private boolean isCompoundPk = false;
    private boolean isRelational = false;
    private boolean isCollection = false;
    private boolean isEmbedded = false;

    private ColumnInfoBuilder() {
    }

    public static ColumnInfoBuilder newBuilder() {
        return new ColumnInfoBuilder();
    }

    public ColumnInfoBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ColumnInfoBuilder column(String column) {
        this.column = column;
        return this;
    }

    public ColumnInfoBuilder type(Class<?> type) {
        this.type = type;
        return this;
    }

    public ColumnInfoBuilder joinTable(String joinTable) {
        this.joinTable = joinTable;
        return this;
    }

    public ColumnInfoBuilder isPk(boolean isPk) {
        this.isPk = isPk;
        return this;
    }

    public ColumnInfoBuilder isFk(boolean isFk) {
        this.isFk = isFk;
        return this;
    }

    public ColumnInfoBuilder isCompoundPk(boolean isCompoundPk) {
        this.isCompoundPk = isCompoundPk;
        return this;
    }

    public ColumnInfoBuilder isRelational(boolean isRelational) {
        this.isRelational = isRelational;
        return this;
    }

    public ColumnInfoBuilder isCollection(boolean isCollection) {
        this.isCollection = isCollection;
        return this;
    }

    public ColumnInfoBuilder isEmbedded(boolean isEmbedded) {
        this.isEmbedded = isEmbedded;
        return this;
    }

    public ColumnInfo build() {
        if (column == null) {
            column = name;
        }
        if (type == null) {
            type = String.class;
        }
        return new ColumnInfo(name, column, type, joinTable, isPk, isFk, isCompoundPk, isRelational, isCollection, isEmbedded);
    }
}
