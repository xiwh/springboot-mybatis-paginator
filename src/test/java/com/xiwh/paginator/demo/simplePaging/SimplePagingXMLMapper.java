package com.xiwh.paginator.demo.simplePaging;

import com.xiwh.paginator.annotations.NormalPaginator;
import com.xiwh.paginator.demo.common.ATablePO;
import com.xiwh.paginator.demo.common.BTablePO;
import com.xiwh.paginator.wrappers.PagingRowBounds;
import com.xiwh.paginator.wrappers.SimplePage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper
@Component
public interface SimplePagingXMLMapper {
    @NormalPaginator
    List<ATablePO> customRowBoundsSelect(String aa, Integer bb, RowBounds rowBounds);

    @NormalPaginator
    List<ATablePO> customPagingRowBoundsSelect(String aa, Integer bb, PagingRowBounds rowBounds);

    @NormalPaginator(startOffset = 1, cache = true)
    SimplePage<BTablePO> select(String aa, Integer bb);
}
