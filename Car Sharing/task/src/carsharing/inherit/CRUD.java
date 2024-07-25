package carsharing.inherit;

import java.sql.SQLException;
import java.util.List;

public interface CRUD<T> {
    public boolean create(T t) throws SQLException;
    public T read(int id) throws SQLException;
    public List<T> readAll() throws SQLException;
    public boolean update(T t) throws SQLException;
}
