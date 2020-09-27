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
        }else if(str.contains(":mariadb:")){
            return MARIADB;
        }else if(str.contains(":postgresql:")){
            return POSGRESQL;
        }else if(str.contains(":oracle:")){
            return ORACLE;
        }else if(str.contains(":sqlserver:")){
            return SQLSERVER;
        }else if(str.contains(":sqlite:")){
            return SQLITE;
        }
        return null;
    }
}
