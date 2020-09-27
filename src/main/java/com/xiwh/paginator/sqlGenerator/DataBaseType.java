package com.xiwh.paginator.sqlGenerator;

public enum DataBaseType {
    SQLITE,
    MYSQL,
    ORACLE,
    POSGRESQL,
    SQLSERVER,
    MARIADB;

    public String toDruidDbType(){
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

    public static DataBaseType findByURL(String str){
        str = str.toLowerCase();
        if(str.contains(":mysql:")){
            return MYSQL;
        }if(str.contains(":sqlserver:")){
            return SQLSERVER;
        }
        return null;
    }
}
