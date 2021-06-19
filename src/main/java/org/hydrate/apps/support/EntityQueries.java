package org.hydrate.apps.support;

import java.util.Objects;
import java.util.stream.Collectors;

public class EntityQueries {

    public static String generateSaveQuery(EntityInfo info) {
        //insert into [TBL] ([columns]) values ([values]);
        StringBuilder query = new StringBuilder("insert into ");
        query.append(Objects.requireNonNull(info.getTableName())).append(" (");

        //non-id, non-collection column names
        String columns = info.getColumns().stream()
                .filter(field -> !field.isPk && !field.isCollection)
                .map(field -> field.column)
                .collect(Collectors.joining(","));
        query.append(columns).append(") values (");

        //column values placeholders
        int length = columns.split(",").length;
        for (int i = 0; i < length; i++) {
            query.append("?");
            if (i + 1 < length) {
                query.append(",");
            }
        }
        query.append(")");
        return query.toString();
    }

    public static String generateUpdateQuery(EntityInfo info) {
        //update [TBL] set ([column]=[value])+ where ([key]=[value])+;
        StringBuilder query = new StringBuilder("update ");
        query.append(Objects.requireNonNull(info.getTableName())).append(" set ");

        //non-id, non-collection column names
        String columns = info.getColumns().stream()
                .filter(field -> !field.isPk && !field.isCollection)
                .map(field -> String.format("%s=:%s", field.column, field.name))
                .collect(Collectors.joining(","));

        String pks = info.getColumns().stream()
                .filter(field -> field.isPk)
                .map(field -> String.format("%s=:%s", field.column, field.name))
                .collect(Collectors.joining(" and "));

        query.append(columns).append(" where ").append(pks);
        return query.toString();
    }

    public static String generateDeleteQuery(EntityInfo info) {
        //delete from [TBL] where ([key]=[value])+;
        StringBuilder query = new StringBuilder("delete from ");
        query.append(Objects.requireNonNull(info.getTableName())).append(" where ");

        String pks = info.getColumns().stream()
                .filter(field -> field.isPk)
                .map(field -> String.format("%s=:%s", field.column, field.name))
                .collect(Collectors.joining(" and "));

        query.append(pks);
        return query.toString();
    }

    public static String generateSelectOneQuery(EntityInfo info) {
        //select [alias].* from [TBL] [alias] where ([key]=[value])+;
        StringBuilder query = new StringBuilder("select A.* from ");
        query.append(Objects.requireNonNull(info.getTableName())).append(" A where ");

        String pks = info.getColumns().stream()
                .filter(field -> field.isPk)
                .map(field -> String.format("%s=:%s", field.column, field.name))
                .collect(Collectors.joining(" and "));

        query.append(pks);
        return query.toString();
    }

    public static String generateSelectListQuery(EntityInfo pkEntity, EntityInfo fkEntity) {
        // select [pkAlias].* from [pkTBL] [pkAlias]
        // inner join [fkTBL] [fkAlias] on [fkAlias].[fkColumn] = [pkAlias].[idColumn]
        // where ([fkAlias].[fkColumn]=[value])+;
        StringBuilder query = new StringBuilder("select A.* from ");
        query.append(Objects.requireNonNull(pkEntity.getTableName())).append(" A where ");

        String pks = fkEntity.getColumns().stream()
                .filter(field -> field.isPk)
                .map(field -> String.format("%s=:%s", field.column, field.name))
                .collect(Collectors.joining(" and "));

        query.append(pks);
        return query.toString();
    }
}
