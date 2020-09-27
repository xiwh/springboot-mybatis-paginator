package com.xiwh.paginator.demo.simplePaging;

import com.xiwh.paginator.Paginator;
import com.xiwh.paginator.TestApplication;
import com.xiwh.paginator.demo.common.ATablePO;
import com.xiwh.paginator.wrappers.PagingRowBounds;
import com.xiwh.paginator.wrappers.SimplePage;
import org.apache.ibatis.session.RowBounds;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;
import java.util.List;

@SuppressWarnings("ALL")
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestApplication.class)
class SimplePagingTests {

    @Autowired
    private WebApplicationContext wac ;
    @Autowired
    SimplePagingMapper mapper;

    private MockMvc mockMvc;

    @PostConstruct
    public void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void testBasicPaging() {
        Paginator.paginate(0,10);
        SimplePage<ATablePO> page = mapper.select(321,0);
        System.out.println(page);
        System.out.println(page.hasLast());
        System.out.println(page.hasNext());
        System.out.println(page.totalPage());
        System.out.println(page.total());
        System.out.println(page.size());
        System.out.println(page.list());
        System.out.println(page.toMap());
        for (ATablePO item:page) {
            item.setName(item.getId()+":"+item.getName());
        }

    }

    @Test
    void testOriginalPaging() {
        RowBounds rowBounds = new RowBounds(10,10);
        Object list = mapper.customRowBoundsSelect(321,0, rowBounds);
        System.out.println(list);
    }

    @Test
    void testAdvancedPaging() {
        PagingRowBounds rowBounds = new PagingRowBounds(3,10,1,false);
        List list = mapper.customPagingRowBoundsSelect(321,0, rowBounds);
        System.out.println(rowBounds);
        System.out.println(rowBounds.toNormalPage(SimplePage.class, list));
    }

    @Test
    void testCustomCountPaging() {
        Paginator.paginate(3,10);
        Object list = mapper.customCountSelect(321,0);
        System.out.println(list);
    }

    @Test
    void testCustomLimitPaging() {
        Paginator.paginate(0,10);
        Object list = mapper.customCountSelect(123,0);
        System.out.println(list);
    }

    @Test
    void testGetRequestPaging() throws Exception {
        System.out.println(mockMvc);
        mockMvc.perform(MockMvcRequestBuilders.get("/test1?page=3&size=10")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());;
//        System.out.println(mvcResult.getAsyncResult());
    }

}