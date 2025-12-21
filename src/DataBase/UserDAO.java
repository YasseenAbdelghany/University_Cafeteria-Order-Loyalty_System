package DataBase;

import java.sql.Connection;

public class UserDAO  {
    DBconnection db ;
    Connection connection;
    public UserDAO() {
        db = new DBconnection();
        connection = db.getConnection();
    }

}
