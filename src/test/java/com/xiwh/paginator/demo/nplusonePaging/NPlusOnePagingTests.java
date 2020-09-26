package com.xiwh.paginator.demo.nplusonePaging;

import com.xiwh.paginator.Paginator;
import com.xiwh.paginator.TestApplication;
import com.xiwh.paginator.demo.simplePaging.SimplePagingMapper;
import com.xiwh.paginator.wrappers.PagingRowBounds;
import org.apache.ibatis.session.RowBounds;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@SuppressWarnings("ALL")
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestApplication.class)
class NPlusOnePagingTests {

    @Autowired
    NPlusonePagingMapper mapper;

    @Test
    void testOriginalPaging() {
        RowBounds rowBounds = new RowBounds(10,10);
        Object list = mapper.customRowBoundsSelect("\"AA'",0, rowBounds);
        System.out.println(list);
    }

    @Test
    void testOriginalPaging2() {
        PagingRowBounds rowBounds = new PagingRowBounds(1,10,1,false);
        Object list = mapper.customPagingRowBoundsSelect("\"AA'",0, rowBounds);
        System.out.println(rowBounds);
        System.out.println(list);
    }

    @Test
    void testBasicPaging() {
        Paginator.paginate(1,10);
        Object list = mapper.select();
        System.out.println(list);
    }


}