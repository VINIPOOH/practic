package dal.dao.maper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Declare method for mapping an {@link ResultSet} into entity
 *
 * @author Vendelovskyi Ivan
 * @version 1.0
 */
@FunctionalInterface
public interface ResultSetToEntityMapper<E> {
    E map(ResultSet resultSet) throws SQLException;
}
