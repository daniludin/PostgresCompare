package postgrescompare;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public  class UniqueConstraint {
    public String table;
    public String name;
    public List<String> columns = new ArrayList<>();
    public String toString() {
        return String.format("[%s] %s: %s", table, name, columns);
    }
}

