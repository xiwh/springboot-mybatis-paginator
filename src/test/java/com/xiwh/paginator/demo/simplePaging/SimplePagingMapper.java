package com.xiwh.paginator.demo.simplePaging;

import com.xiwh.paginator.annotations.NormalPaginator;
import com.xiwh.paginator.demo.common.ATablePO;
import com.xiwh.paginator.wrappers.PagingRowBounds;
import com.xiwh.paginator.wrappers.SimplePage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
public interface SimplePagingMapper {
    @Select("SELECT * FROM a where id != ${bb} or id != #{aa} order by id")
    @NormalPaginator
    List<ATablePO> customRowBoundsSelect(Integer aa, Integer bb, RowBounds rowBounds);

    @Select("SELECT * FROM a where id != ${bb} or id != #{aa} order by id")
    @NormalPaginator
    List<ATablePO> customPagingRowBoundsSelect(Integer aa, Integer bb, PagingRowBounds rowBounds);

    @Select("SELECT * FROM a order by id")
    @NormalPaginator()
    SimplePage<ATablePO> select(Integer aa, Integer bb);

    @Select("SELECT * FROM a where id != ${bb} or id != #{aa} order by id")
    @NormalPaginator(auto = true, startOffset = 1, cacheExpiryTime = 300)
    SimplePage<ATablePO> requestPaging(Integer aa, Integer bb);

    @Select("SELECT * FROM a where id != ${bb} or id != #{aa} order by id")
    @NormalPaginator(countMethod = "customCount")
    SimplePage<ATablePO> customCountSelect(Integer aa, Integer bb);

    @Select("SELECT count(1) FROM a where id != ${bb} or id != #{aa}")
    Integer customCount(Integer aa, Integer bb);

    @Select("SELECT * FROM a where id != ${bb} or id != #{aa} limit :offset,:limit")
    @NormalPaginator(customLimit = true, countMethod = "customCount")
    SimplePage<ATablePO> customLimitSelect(Integer aa, Integer bb);
}
