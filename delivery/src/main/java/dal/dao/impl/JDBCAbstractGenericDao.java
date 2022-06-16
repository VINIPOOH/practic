package dal.dao.impl;


import dal.conection.ConnectionProxy;
import dal.conection.pool.TransactionalManager;
import dal.dao.AbstractGenericDao;
import dal.dao.maper.EntityToPreparedStatementMapper;
import dal.dao.maper.ResultSetToEntityMapper;
import dal.exeption.DBRuntimeException;
import infrastructure.anotation.InjectByType;
import infrastructure.anotation.NeedConfig;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Implements widely used methods in DAO classes.
 *
 * @author Vendelovskyi Ivan
 * @version 1.0
 */
@NeedConfig
abstract class JDBCAbstractGenericDao<E> implements AbstractGenericDao<E> {

    private static final Logger log = LogManager.getLogger(JDBCAbstractGenericDao.class);
    @InjectByType
    ResourceBundle resourceBundleRequests;
    @InjectByType
    TransactionalManager connector;


    @Override
    public List<E> findAllByLongParam(long param, String query, ResultSetToEntityMapper<E> mapper) {
        try (ConnectionProxy connection = connector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, param);
            return mapPreparedStatementToEntitiesList(mapper, preparedStatement);
        } catch (SQLException e) {
            log.error("SQLException", e);
            throw new DBRuntimeException();
        }
    }

    @Override
    public List<E> findAllByLongParamPageable(long param, Integer offset, Integer limit, String query, ResultSetToEntityMapper<E> mapper) {
        try (ConnectionProxy connection = connector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, param);
            preparedStatement.setInt(2, offset);
            preparedStatement.setInt(3, limit);
            return mapPreparedStatementToEntitiesList(mapper, preparedStatement);
        } catch (SQLException e) {
            log.error("SQLException", e);
            throw new DBRuntimeException();
        }

    }

    /**
     * @param param param by which need count
     * @param query must return one long param which will be
     */
    @Override
    public long countAllByLongParam(long param, String query) {
        try (ConnectionProxy connection = connector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, param);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getLong(1);
            }
        } catch (SQLException e) {
            log.error("SQLException", e);
            throw new DBRuntimeException();
        }
    }

    private List<E> mapPreparedStatementToEntitiesList(ResultSetToEntityMapper<E> mapper, PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            List<E> result = new ArrayList<>();
            while (resultSet.next()) {
                result.add(mapper.map(resultSet));
            }
            return result;
        }
    }


    @Override
    public boolean save(E entity, String saveQuery, EntityToPreparedStatementMapper<E> mapper) {
        try (ConnectionProxy connection = connector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(saveQuery)) {

            mapper.map(entity, preparedStatement);
            return preparedStatement.executeUpdate() != 0;
        } catch (SQLException e) {
            log.error("SQLException", e);
            throw new DBRuntimeException();
        }
    }

    @Override
    public Optional<E> findById(long id, String query, ResultSetToEntityMapper<E> mapper) {
        try (ConnectionProxy connection = connector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapper.map(resultSet)) : Optional.empty();
            }
        } catch (SQLException e) {
            log.error("SQLException", e);
            throw new DBRuntimeException();
        }
    }

    @Override
    public boolean deleteById(long id, String query, ResultSetToEntityMapper<E> mapper) {
        try (ConnectionProxy connection = connector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setLong(1, id);
            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            log.error("SQLException", e);
            throw new DBRuntimeException();
        }
    }

    @Override
    public List<E> findAll(String query, ResultSetToEntityMapper<E> mapper) {
        try (ConnectionProxy connection = connector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            List<E> result;
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                result = new ArrayList<>();
                while (resultSet.next()) {
                    result.add(mapper.map(resultSet));
                }
            }
            return result;
        } catch (SQLException e) {
            log.error("SQLException", e);
            throw new DBRuntimeException();
        }
    }

    @Override
    public boolean update(E entity, String saveQuery, EntityToPreparedStatementMapper<E> mapper) {
        try (ConnectionProxy connection = connector.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(saveQuery)) {

            mapper.map(entity, preparedStatement);
            return preparedStatement.executeUpdate() == 1;
        } catch (SQLException e) {
            log.error("SQLException", e);
            throw new DBRuntimeException();
        }
    }

}
