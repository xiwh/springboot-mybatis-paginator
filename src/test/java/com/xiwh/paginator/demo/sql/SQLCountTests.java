package com.xiwh.paginator.demo.sql;

import com.xiwh.paginator.TestApplication;
import com.xiwh.paginator.sqlGenerator.DataBaseType;
import com.xiwh.paginator.sqlGenerator.PaginatorSqlGenerator;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestApplication.class)
class SQLCountTests {

    @Test
    void countSql() {
        PaginatorSqlGenerator sqlGenerator = new PaginatorSqlGenerator("SELECT a.id FROM a GROUP BY a.id HAVING id != 3 ORDER BY a.id", DataBaseType.MYSQL);
        System.out.println("countSql1:\n"+sqlGenerator.toCountSql());
        System.out.println();
        sqlGenerator = new PaginatorSqlGenerator("SELECT a.id FROM a where id != 3 ORDER BY a.id", DataBaseType.MYSQL);
        System.out.println("countSql2:\r\n"+sqlGenerator.toCountSql());
        System.out.println();
        sqlGenerator = new PaginatorSqlGenerator("SELECT\n" +
                "\ta.id AS aid \n" +
                "FROM\n" +
                "\ta\n" +
                "LEFT JOIN b on a.id = b.id\n" +
                "WHERE b.id in (select max(id) FROM a)", DataBaseType.MYSQL);
        System.out.println("countSql3:\r\n"+sqlGenerator.toCountSql());
    }

    @Test
    void optimizedCountSql() {
        PaginatorSqlGenerator sqlGenerator = new PaginatorSqlGenerator(
                "select a.id,b.name,c.value FROM a\n" +
                        "left join b on b.id = a.id\n" +
                        "left join c on c.id = b.id\n" +
                        "group by b.name,c.value\n",
                DataBaseType.POSGRESQL
        );
        System.out.println("countSql1:\n"+sqlGenerator.toOptimizedCountSql());
        System.out.println();
        System.out.println("limitSql1:\n"+sqlGenerator.toLimitSql(0,100));
        System.out.println("---------------------");
        sqlGenerator = new PaginatorSqlGenerator(
                "select a.id,b.name,c.value FROM a\n " +
                        "left join b on b.id = a.id\n " +
                        "left join c on c.id = b.id\n " +
                        "group by a.id\n" +
                        "having id != 0",
                DataBaseType.MYSQL
        );
        System.out.println("countSql2:\n"+sqlGenerator.toOptimizedCountSql());
        System.out.println();
        System.out.println("limitSql2:\n"+sqlGenerator.toLimitSql(0,100));
        System.out.println("---------------------");
        sqlGenerator = new PaginatorSqlGenerator(
                "SELECT a.a1, a.a2, a.a3 FROM TABLE1 a, TABLE2  b WHERE a.a=t2.a",
                DataBaseType.ORACLE);
        System.out.println("countSql3:\n"+sqlGenerator.toOptimizedCountSql());
        System.out.println();
        System.out.println("limitSql3:\n"+sqlGenerator.toLimitSql(0,100));
    }
}
