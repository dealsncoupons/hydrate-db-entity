package org.hydrate.apps.repo;

import org.h2.jdbc.JdbcSQLSyntaxErrorException;
import org.h2.jdbcx.JdbcConnectionPool;
import org.hydrate.apps.repo.exception.*;
import org.hydrate.apps.support.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenericRepo {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericRepo.class);
    public final JdbcConnectionPool cp = JdbcConnectionPool.create("jdbc:h2:~/.h2data/tasks_db", "sa", "sa");

    public GenericRepo() {
        try (Connection conn = cp.getConnection()) {
            String initSchema = String.join("", Files.readAllLines(Paths.get("src/main/resources/sql/init-schema.sql")));
            try (Statement st = conn.createStatement()) {
                st.execute(initSchema);
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException("Initialization failure. Cannot start application", e);
        }
    }

    private <E extends Entity, ID> E saveAndGetGeneratedId(E entity, Connection connection, LocalCache<ID> cache) throws SQLException {
        //save fk relationships first
        entity.info().getColumns().stream()
                .filter(field -> field.isRelational)
                .forEach(field -> {
                    if (!field.isCollection) {
                        Entity fkEntity = entity.get(field.name);
                        if (fkEntity != null) {
                            if (fkEntity.getKey() == null || !cache.hasItem(fkEntity.getKey())) {
                                try {
                                    Entity saved = saveAndGetGeneratedId(fkEntity, connection, cache);
                                    //set fk-value into fk-column
                                    entity.set(field.name, saved);
                                    //add item to local cache
                                    cache.putItem(saved.getKey(), saved);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    LOGGER.warn("Could not save relational entity - {}", field.name);
                                }
                            } else {
                                entity.set(field.name, cache.getItem(fkEntity.getKey()));
                            }
                        }
                    } else {
                        Collection<Entity> collection = entity.get(field.name);
                        if (!collection.isEmpty()) {
                            Collection<Entity> savedCollection = collection.stream().map(collectionEntity -> {
                                try {
                                    if (collectionEntity.getKey() == null || !cache.hasItem(collectionEntity.getKey())) {
                                        Entity saved = saveAndGetGeneratedId(collectionEntity, connection, cache);
                                        //add item to local cache
                                        cache.putItem(saved.getKey(), saved);
                                        return saved;
                                    } else {
                                        return cache.getItem(collectionEntity.getKey());
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    LOGGER.warn("Could not save relational entity in collection - {}", field.name);
                                    return null;
                                }
                            }).collect(Collectors.toCollection(ArrayList::new));
                            //set fk-value into fk-column
                            entity.set(field.name, savedCollection);
                        }
                    }
                });

        //get insertion columns
        List<String> columns = entity.info().getColumns().stream()
                .filter(field -> !field.isPk && !field.isCollection)
                .flatMap(field -> field.isEmbedded?
                        EntityMetadata.entityInfo(field.type).getColumns().stream() :
                        Stream.of(field))
                .map(field -> field.column)
                .collect(Collectors.toList());

        //get pk columns
        List<String> pks = entity.info().getColumns().stream()
                .filter(field -> field.isPk)
                .map(field -> field.column)
                .collect(Collectors.toList());

        String query = EntityQueries.generateSaveQuery(entity.info());

        Map<String, Object> parameters = entity.extractValues();
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            //prepare the statement
            for (int idx = 0; idx < columns.size(); idx++) {
                ps.setObject(idx + 1, parameters.get(columns.get(idx)));
            }
            //execute query
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        for (String pk : pks) {
                            Object pkValue = keys.getObject(pk);
                            entity.set(pk, pkValue);
                        }
                        return entity;
                    } else {
                        throw new RecordKeyNotGenerated("Missing generated record id");
                    }
                }
            } else {
                throw new RecordNotCreated("No record created");
            }
        }
    }

    public <E extends Entity> E save(E entity) {
        Connection connection = null;
        try {
            connection = cp.getConnection();
            connection.setAutoCommit(false);
            try {
                E result = saveAndGetGeneratedId(entity, connection, new LocalCache<>());
                connection.commit();
                return result;
            } catch (Exception e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new RollbackFailed("Save operation rollback failed", ex);
                }
                if (e instanceof JdbcSQLSyntaxErrorException) {
                    throw new SaveNotSuccessful(((JdbcSQLSyntaxErrorException) e).getOriginalMessage(), e);
                } else {
                    throw new SaveNotSuccessful("Save operation failed", e);
                }
            }
        } catch (SQLException e) {
            throw new ConnectionProblem(((JdbcSQLSyntaxErrorException) e).getOriginalMessage(), e);
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public <E extends Entity> E update(E entity) {
        Connection connection = null;
        try {
            connection = cp.getConnection();
            connection.setAutoCommit(false);
            try {
                E result = update(entity, connection, new LocalCache<>());
                connection.commit();
                return result;
            } catch (Exception e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new RollbackFailed("Update operation rollback failed", ex);
                }
                if (e instanceof JdbcSQLSyntaxErrorException) {
                    throw new UpdateNotSuccessful(((JdbcSQLSyntaxErrorException) e).getOriginalMessage(), e);
                } else {
                    throw new UpdateNotSuccessful("Update operation failed", e);
                }
            }
        } catch (SQLException e) {
            throw new ConnectionProblem(((JdbcSQLSyntaxErrorException) e).getOriginalMessage(), e);
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private <E extends Entity, ID> E update(E entity, Connection connection, LocalCache<ID> cache) throws SQLException {
        String queryWithPlaceholders = EntityQueries.generateUpdateQuery(entity.info());
        Map<String, Object> parameters = entity.extractValues();
        //create query params
        StringBuilder queryBuilder = new StringBuilder();

        Map<String, Object> queryParams = new LinkedHashMap<>();
        Matcher matcher = Pattern.compile("([\\w]+?)=(:)([\\w]+)").matcher(queryWithPlaceholders);
        int lastIndex = 0;
        while (matcher.find()) {
            queryBuilder.append(queryWithPlaceholders, lastIndex, matcher.start(2));
            queryBuilder.append("?");
            queryParams.put(matcher.group(1), parameters.get(matcher.group(1)));
            lastIndex = matcher.end(3);
        }
        queryBuilder.append(queryWithPlaceholders.substring(lastIndex));

        try (PreparedStatement ps = connection.prepareStatement(queryBuilder.toString())) {
            //prepare the statement
            int idx = 1;
            for (String param : queryParams.keySet()) {
                ps.setObject(idx++, queryParams.get(param));
            }
            //execute query
            int rowsAffected = ps.executeUpdate();
            LOGGER.warn("{} record(s) updated", rowsAffected);
            return entity;
        }
    }

    public <E extends Entity> E delete(E entity) {
        Connection connection = null;
        try {
            connection = cp.getConnection();
            connection.setAutoCommit(false);
            try {
                E result = delete(entity, connection, new LocalCache<>());
                connection.commit();
                return result;
            } catch (Exception e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new RollbackFailed("Delete operation rollback failed", ex);
                }
                if (e instanceof JdbcSQLSyntaxErrorException) {
                    throw new DeleteNotSuccessful(((JdbcSQLSyntaxErrorException) e).getOriginalMessage(), e);
                } else {
                    throw new DeleteNotSuccessful("Delete operation failed", e);
                }
            }
        } catch (SQLException e) {
            throw new ConnectionProblem(((JdbcSQLSyntaxErrorException) e).getOriginalMessage(), e);
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private <E extends Entity, ID> E delete(E entity, Connection connection, LocalCache<ID> cache) throws SQLException {
        String queryWithPlaceholders = EntityQueries.generateDeleteQuery(entity.info());
        Map<String, Object> parameters = entity.extractValues();
        //create query params
        StringBuilder queryBuilder = new StringBuilder();

        Map<String, Object> queryParams = new LinkedHashMap<>();
        Matcher matcher = Pattern.compile("([\\w]+?)=(:)([\\w]+)").matcher(queryWithPlaceholders);
        int lastIndex = 0;
        while (matcher.find()) {
            queryBuilder.append(queryWithPlaceholders, lastIndex, matcher.start(2));
            queryBuilder.append("?");
            queryParams.put(matcher.group(1), parameters.get(matcher.group(1)));
            lastIndex = matcher.end(3);
        }
        queryBuilder.append(queryWithPlaceholders.substring(lastIndex));

        try (PreparedStatement ps = connection.prepareStatement(queryBuilder.toString())) {
            //prepare the statement
            int idx = 1;
            for (String param : queryParams.keySet()) {
                ps.setObject(idx++, queryParams.get(param));
            }
            //execute query
            int rowsAffected = ps.executeUpdate();
            LOGGER.info("{} record(s) deleted", rowsAffected);
            return entity;
        }
    }

    public <E extends Entity> E select(EntityInfo info, Map<String, Object> matchColumns) {
        Connection connection = null;
        try {
            connection = cp.getConnection();
            connection.setAutoCommit(false);
            try {
                E result = selectOne(info, matchColumns, connection, new LocalCache<>());
                connection.commit();
                return result;
            } catch (Exception e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new RollbackFailed("Select single operation rollback failed", ex);
                }
                if (e instanceof JdbcSQLSyntaxErrorException) {
                    throw new DeleteNotSuccessful(((JdbcSQLSyntaxErrorException) e).getOriginalMessage(), e);
                } else {
                    throw new DeleteNotSuccessful("Select single operation failed", e);
                }
            }
        } catch (SQLException e) {
            throw new ConnectionProblem(((JdbcSQLSyntaxErrorException) e).getOriginalMessage(), e);
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public <E extends Entity> Collection<E> selectList(EntityInfo pkEntity, EntityInfo fkEntity, Map<String, Object> matchColumns) {
        Connection connection = null;
        try {
            connection = cp.getConnection();
            connection.setAutoCommit(false);
            try {
                Collection<E> result = selectList(pkEntity, fkEntity, matchColumns, connection, new LocalCache<>());
                connection.commit();
                return result;
            } catch (Exception e) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    throw new RollbackFailed("Select collection operation rollback failed", ex);
                }
                if (e instanceof JdbcSQLSyntaxErrorException) {
                    throw new DeleteNotSuccessful(((JdbcSQLSyntaxErrorException) e).getOriginalMessage(), e);
                } else {
                    throw new DeleteNotSuccessful("Select collection operation failed", e);
                }
            }
        } catch (SQLException e) {
            throw new ConnectionProblem(((JdbcSQLSyntaxErrorException) e).getOriginalMessage(), e);
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private <E extends Entity, ID> E selectOne(EntityInfo info, Map<String, Object> parameters, Connection connection, LocalCache<ID> cache) throws SQLException {
        String queryWithPlaceholders = EntityQueries.generateSelectOneQuery(info);
        //create query params
        StringBuilder queryBuilder = new StringBuilder();

        Map<String, Object> queryParams = new LinkedHashMap<>();
        Matcher matcher = Pattern.compile("([\\w]+?)=(:)([\\w]+)").matcher(queryWithPlaceholders);
        int lastIndex = 0;
        while (matcher.find()) {
            queryBuilder.append(queryWithPlaceholders, lastIndex, matcher.start(2));
            queryBuilder.append("?");
            queryParams.put(matcher.group(1), parameters.get(matcher.group(1)));
            lastIndex = matcher.end(3);
        }
        queryBuilder.append(queryWithPlaceholders.substring(lastIndex));

        try (PreparedStatement ps = connection.prepareStatement(queryBuilder.toString())) {
            //prepare the statement
            int idx = 1;
            for (String param : queryParams.keySet()) {
                ps.setObject(idx++, queryParams.get(param));
            }
            //execute query
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Entity entity = info.newInstance();
                    try {
                        return (E) selectOne(entity, rs, connection, cache);
                    } catch (Exception e) {
                        throw new SelectEntityFailure("Select operation failed", e);
                    }
                } else {
                    throw new EntityNotFound("No entity was found based on the supplied criteria");
                }
            }
        }
    }

    private <E extends Entity, ID> E selectOne(E entity, ResultSet rs, Connection connection, LocalCache<ID> cache) throws SQLException {
        for (ColumnInfo field : entity.info().getColumns()) {
            if (field.isRelational) {
                if (field.isCollection) {
                    Map<String, Object> parameters = new HashMap<>();
                    entity.info().getColumns().stream().filter(column -> column.isPk)
                            .forEach(column -> parameters.put(column.column, entity.get(column.name)));
                    EntityInfo pkEntityInfo = EntityMetadata.entityInfo(field.type);
                    Collection<Entity> entityCollection = selectList(pkEntityInfo, entity.info(), parameters, connection, cache);
                    entity.set(field.name, entityCollection);
                } else {
                    Object relationalId = rs.getObject(field.column);
                    if (relationalId != null) {
                        if (cache.hasItem((ID) relationalId)) {
                            return (E) cache.getItem((ID) relationalId);
                        }
                        Map<String, Object> parameters = new HashMap<>();
                        EntityInfo relationEntityInfo = EntityMetadata.entityInfo(field.type);
                        relationEntityInfo.getColumns().stream().filter(column -> column.isPk)
                                .forEach(column -> {
                                    parameters.put(column.column, relationalId);
                                });
                        Entity relationalEntity = selectOne(EntityMetadata.entityInfo(field.type), parameters, connection, cache);
                        entity.set(field.name, relationalEntity);
                    }
                }
            } else if (field.isEmbedded) {
                EntityInfo embeddedEntityInfo = EntityMetadata.entityInfo(field.type);
                Entity embeddedEntity = embeddedEntityInfo.newInstance();
                for(ColumnInfo column : embeddedEntityInfo.getColumns()){
                    embeddedEntity.set(column.name, rs.getObject(column.column, column.type));
                }
                entity.set(field.name, embeddedEntity);
            } else {
                Object value = rs.getObject(field.column, field.type);
                if (field.isPk) {
                    cache.putItem((ID) value, entity);
                }
                entity.set(field.name, value);
            }
        }
        return entity;
    }

    private <E extends Entity, ID> Collection<E> selectList(EntityInfo pkEntityInfo, EntityInfo fkEntityInfo, Map<String, Object> parameters, Connection connection, LocalCache<ID> cache) throws SQLException {
        String queryWithPlaceholders = EntityQueries.generateSelectListQuery(pkEntityInfo, fkEntityInfo);
        //create query params
        StringBuilder queryBuilder = new StringBuilder();

        Map<String, Object> queryParams = new LinkedHashMap<>();
        Matcher matcher = Pattern.compile("([\\w]+?)=(:)([\\w]+)").matcher(queryWithPlaceholders);
        int lastIndex = 0;
        while (matcher.find()) {
            queryBuilder.append(queryWithPlaceholders, lastIndex, matcher.start(2));
            queryBuilder.append("?");
            queryParams.put(matcher.group(1), parameters.get(matcher.group(1)));
            lastIndex = matcher.end(3);
        }
        queryBuilder.append(queryWithPlaceholders.substring(lastIndex));

        try (PreparedStatement ps = connection.prepareStatement(queryBuilder.toString())) {
            //prepare the statement
            int idx = 1;
            for (String param : queryParams.keySet()) {
                ps.setObject(idx++, queryParams.get(param));
            }
            //execute query
            try (ResultSet rs = ps.executeQuery()) {
                Collection<E> collection = new ArrayList<>();
                while (rs.next()) {
                    Entity entity = fkEntityInfo.newInstance();
                    try {
                        E result = (E) selectOne(entity, rs, connection, cache);
                        collection.add(result);
                    } catch (Exception e) {
                        throw new SelectEntityFailure("Select operation failed", e);
                    }
                }
                return collection;
            }
        }
    }
}
