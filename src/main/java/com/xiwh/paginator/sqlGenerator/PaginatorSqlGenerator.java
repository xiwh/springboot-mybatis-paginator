package com.xiwh.paginator.sqlGenerator;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObject;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.*;
import com.alibaba.druid.sql.ast.statement.*;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.oracle.parser.OracleStatementParser;
import com.alibaba.druid.sql.dialect.postgresql.parser.PGSQLStatementParser;
import com.alibaba.druid.sql.dialect.sqlserver.parser.SQLServerStatementParser;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.xiwh.paginator.utils.StringUtils;

import java.util.Iterator;
import java.util.List;

public class PaginatorSqlGenerator {
    public static final String COUNT_ALIAS = "p_count";
    public static final String TEMP_VAR_ALIAS = "p_temp_v";
    public static final String TEMP_TABLE_ALIAS = "p_temp_t";
    public static final String TEMP_TABLE2_ALIAS = "p_temp_t2";
    public static final String TEMP_ROWNUM_ALIAS = "p_temp_rn";


    SQLSelect rawSelect;
    DataBaseType dataBaseType;

    /**
     * @param sql query SQL
     * @param dataBaseType
     */
    public PaginatorSqlGenerator(String sql, DataBaseType dataBaseType){
        rawSelect = parserSelectSql(sql, dataBaseType);
        this.dataBaseType = dataBaseType;
    }

    /**
     * Analysis of SQL structure
     * @param sql
     * @param dataBaseType
     * @return
     */
    private static SQLSelect parserSelectSql(String sql, DataBaseType dataBaseType){
        SQLStatementParser sqlStatementParser =  null;
        switch (dataBaseType){
            case MYSQL:
                sqlStatementParser = new MySqlStatementParser(sql);
                break;
            case ORACLE:
                sqlStatementParser = new OracleStatementParser(sql);
                break;
            case POSGRESQL:
                sqlStatementParser = new PGSQLStatementParser(sql);
                break;
            case SQLSERVER:
                sqlStatementParser = new SQLServerStatementParser(sql);
                break;
        }
        List<SQLStatement> statementList = sqlStatementParser.parseStatementList();
        SQLStatement sqlStatement = statementList.get(0);
        List<SQLObject> sqls = sqlStatement.getChildren();
        SQLSelect sqlSelect = (SQLSelect) sqls.get(0);
        return sqlSelect;
    }

    public String toLimitSql(int offset, int limit){
        SQLSelect sqlSelect = rawSelect.clone();
        SQLSelectQuery selectQuery = sqlSelect.getQuery();
        SQLSelectQueryBlock selectQueryBlock = (SQLSelectQueryBlock) selectQuery;
        // Fuckkkkkkkkk oracle
        if(dataBaseType == DataBaseType.ORACLE){
            return String.format(
                    "select * from ( select rownum %s,%s.* from ( %s ) %s where rownum <= %s ) %s where %s.%s > %s",
                    TEMP_ROWNUM_ALIAS,
                    TEMP_TABLE_ALIAS,
                    toSQLString(sqlSelect, dataBaseType),
                    TEMP_TABLE_ALIAS,
                    offset+limit,
                    TEMP_TABLE2_ALIAS,
                    TEMP_TABLE2_ALIAS,
                    TEMP_ROWNUM_ALIAS,
                    offset
            );
        }else if(dataBaseType == DataBaseType.SQLSERVER){
            String sql = toSQLString(sqlSelect, dataBaseType);
            return String.format("%s OFFSET %s ROWS FETCH NEXT %s ROWS ONLY", sql, offset, limit);
        }else {
            selectQueryBlock.limit(limit, offset);
            return toSQLString(sqlSelect, dataBaseType);
        }
    }


    /**
     * Convert query SQL to count SQL
     * @return counted SQL
     */
    public String toCountSql(){
        SQLSelect sqlSelect = rawSelect.clone();
        SQLSelectQuery selectQuery = sqlSelect.getQuery();
        SQLSelectQueryBlock selectQueryBlock = (SQLSelectQueryBlock) selectQuery;
        //Remove order by
        selectQueryBlock.setOrderBy(null);
        SQLSelectGroupByClause groupByClause = selectQueryBlock.getGroupBy();
        //Group by needs to be wrapped
        if(groupByClause!=null&&!groupByClause.getItems().isEmpty()){
            //Replace all select with count
            List<SQLSelectItem> selectItems = selectQueryBlock.getSelectList();
            selectItems.clear();
            selectItems.add(new SQLSelectItem(new SQLIntegerExpr(1),TEMP_VAR_ALIAS));
            //Wrapping group SQL
            return String.format(
                    "SELECT count(%s.%s) as %s FROM (%s) as %s",
                    TEMP_TABLE_ALIAS,
                    TEMP_VAR_ALIAS,
                    COUNT_ALIAS,
                    toSQLString(sqlSelect, dataBaseType),
                    TEMP_TABLE_ALIAS
            );
        }
        else{
            //Replace all select with count
            List<SQLSelectItem> selectItems = selectQueryBlock.getSelectList();
            selectItems.clear();
            selectItems.add(new SQLSelectItem(new SQLAggregateExpr("count", null, new SQLIntegerExpr(1)),COUNT_ALIAS));
            return toSQLString(sqlSelect, dataBaseType);
        }
    }

    /**
     * Convert query SQL to optimized count SQL
     * What are the optimization cases:
     * 1.Clean invalid order by
     * 2.Clean invalid select
     * @return Optimized count SQL
     */
    public String toOptimizedCountSql(){
        SQLSelect sqlSelect = rawSelect.clone();
        SQLSelectQuery selectQuery = sqlSelect.getQuery();
        SQLSelectQueryBlock selectQueryBlock = (SQLSelectQueryBlock) selectQuery;
        String havingStr = null;
        //Remove order by
        selectQueryBlock.setOrderBy(null);
        List<SQLSelectItem> selectItems = selectQueryBlock.getSelectList();
        SQLSelectGroupByClause groupByClause = selectQueryBlock.getGroupBy();
        boolean needWrapUp = false;
        //Has groupBy or having
        if(groupByClause!=null){
            SQLExpr having = groupByClause.getHaving();
            if(!groupByClause.getItems().isEmpty()) {
                needWrapUp = true;
            }
            // If there is "having", "select" will be cleaned up based on the "having" reference
            if(having!=null){
                havingStr = having.toString();
                Iterator<SQLSelectItem> iterator = selectItems.iterator();
                while (iterator.hasNext()) {
                    SQLSelectItem item = iterator.next();
                    // Get field name
                    String fieldName = item.getAlias();
                    if(StringUtils.isEmpty(fieldName)){
                        String fieldBody = item.toString();
                        int dotIndex = fieldBody.lastIndexOf('.');
                        if(dotIndex==-1){
                            fieldName = fieldBody;
                        }else{
                            fieldName = fieldBody.substring(dotIndex+1);
                        }
                    }
                    if (havingStr.contains(fieldName)) {
                        continue;
                    }
                    iterator.remove();
                }
                selectItems.add(new SQLSelectItem(new SQLIntegerExpr(1),TEMP_VAR_ALIAS));
            }
            //Replace the group without having with count(distinct xxx)
            else{
                selectItems.clear();
                selectItems.add(
                        new SQLSelectItem(
                                new SQLIntegerExpr(1),TEMP_VAR_ALIAS
                        )
                );
                selectQueryBlock.setGroupBy(null);
            }
        }else{
            // When there is no "having" or "group", directly replace "select"
            selectItems.clear();
            selectItems.add(new SQLSelectItem(new SQLAggregateExpr("count", null, new SQLIntegerExpr(1)),COUNT_ALIAS));
        }
        String newSql = toSQLString(sqlSelect, dataBaseType);
        //Wrapping group SQL
        if(needWrapUp){
            return String.format(
                    "SELECT count(%s.%s) as %s FROM (%s) as %s",
                    TEMP_TABLE_ALIAS,
                    TEMP_VAR_ALIAS,
                    COUNT_ALIAS,
                    newSql,
                    TEMP_TABLE_ALIAS
            );
        }else{
            return newSql;
        }
    }

    public static String toSQLString(SQLObject sqlObject, DataBaseType dataBaseType) {
        String sql = sqlObject.toString();
        return sql.replaceAll("[\\t\\n\\r]"," ");
    }

}
