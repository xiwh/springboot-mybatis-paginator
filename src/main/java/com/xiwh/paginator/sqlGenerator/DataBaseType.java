package com.xiwh.paginator.sqlGenerator;

public enum DataBaseType {
    SQLITE,
    MYSQL,
    ORACLE,
    POSGRESQL,
    SQLSERVER,
    MARIADB;

    public String toDuirdDbType(){
        switch (this){
            case POSGRESQL:
                return "postgresql";
            case MYSQL:
                return "mysql";
            case ORACLE:
                return "oracle";
            case SQLSERVER:
                return "sqlserver";
            case MARIADB:
                return "mariadb";
            case SQLITE:
                return "sqlite";
        }
        return null;
    }

    public static DataBaseType findByName(String str){
        str = str.replaceAll("[^a-zA-Z]", "").toLowerCase();
        switch (str){
            case "mysql":
                return MYSQL;
            case "oracle":
                return ORACLE;
            case "posgresql":
                return POSGRESQL;
            case "pgsql":
                return POSGRESQL;
            case "sqlserver":
                return SQLSERVER;
            case "mariadb":
                return MARIADB;
            case "sqlite":
                return SQLITE;
        }
        return null;
    }
}
