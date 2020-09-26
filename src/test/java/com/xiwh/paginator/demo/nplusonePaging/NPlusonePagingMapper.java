package com.xiwh.paginator.demo.nplusonePaging;

import com.xiwh.paginator.annotations.NPlusOnePaginator;
import com.xiwh.paginator.demo.common.ATablePO;
import com.xiwh.paginator.wrappers.NPlusOnePage;
import com.xiwh.paginator.wrappers.PagingRowBounds;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface NPlusonePagingMapper {
    @Select("SELECT * FROM a where id != ${bb} or id != #{aa}")
    @NPlusOnePaginator
    List<ATablePO> customRowBoundsSelect(String aa, Integer bb, RowBounds rowBounds);

    @Select("SELECT * FROM a where id != ${bb} or id != #{aa}")
    @NPlusOnePaginator
    List<ATablePO> customPagingRowBoundsSelect(String aa, Integer bb, PagingRowBounds rowBounds);

    @Select("SELECT * FROM a")
    @NPlusOnePaginator(startOffset = 1)
    NPlusOnePage<ATablePO> select();

    @Select("SELECT * FROM a where id != ${bb} or id != #{aa}")
    @NPlusOnePaginator(auto = true, startOffset = 1)
    NPlusOnePage<ATablePO> requestPaging(String aa, Integer bb);

}
