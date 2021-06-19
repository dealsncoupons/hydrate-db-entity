package org.hydrate.apps.support;

public class ColumnInfo {

    public final String name;
    public final String column;
    public final Class<?> type;
    public final boolean isPk;
    public final boolean isFk;
    public final boolean isRelational;
    public final boolean isCollection;
    public final boolean isEmbedded;

    public ColumnInfo(String name, String column, Class<?> type, boolean isPk, boolean isFk, boolean isRelational, boolean isCollection, boolean isEmbedded) {
        this.name = name;
        this.column = column;
        this.type = type;
        this.isPk = isPk;
        this.isFk = isFk;
        this.isRelational = isRelational;
        this.isCollection = isCollection;
        this.isEmbedded = isEmbedded;
    }
}
