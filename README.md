Springboot-Mybatis-Paginator
============
![Maven Central](https://img.shields.io/maven-central/v/com.github.xiwh/mybatis-paginator)
![GitHub](https://img.shields.io/github/license/xiwh/springboot-mybatis-paginator)

它是一款基于Springboot的高效易用易扩展的Mybatis全自动物理分页插件，提供基础分页、N+1分页、无侵入式物理分页、微侵入式易用分页等方式

Features
--------
* 即插即用，您甚至不需要添加任何配置，只需引入jar包即可直接使用
* 普通分页(基于Count聚合统计的传统管理后台分页方式)
* N+1分页(适用于移动端上拉加载的高效分页方式)
* 全自动化生成Count和Limit语句，支持主流数据库方言，并提供自动优化Count的可选项(默认开启)，支持包括GroupBy在内的Count语句自动生成，实现简单场景下的高效开发
* 提供完全自定义Limit和Count语句的方案，覆盖一些复杂SQL需要手动写的场景
* 提供Count结果缓存的可选项，适用对Count结果无强一致要求并且有一定性能要求的场景
* 提供微侵入和无侵入两种使用方式
* 提供完全自定义分页结构方案，可以做到快速无缝衔接到前端或已有项目中

Supported databases
--------
* `mysql`
* `mariadb`
* `oracle`
* `sqlserver`
* `posgresql`
* `sqlite`

Setup
--------

Gradle:
```groovy
compile 'com.github.xiwh:mybatis-paginator:1.0.0'
```
Maven:
```xml
<dependency>
  <groupId>com.github.xiwh</groupId>
  <artifactId>mybatis-paginator</artifactId>
  <version>1.0.0</version>
</dependency>
```

快速使用
--------
***普通分页***
```java
public interface SimplePagingMapper {
    @Select("SELECT * FROM a")
    @NormalPaginator()
    SimplePage<Bean> select();
    
    public static void main(String[] args){
        SimplePagingMapper mapper = xxx;
        Paginator.paginate(2,10);
        SimplePage<Bean> page = mapper.select();
        System.out.println(page);
        System.out.println(page.hasLast());
        System.out.println(page.hasNext());
        System.out.println(page.totalPage());
        System.out.println(page.total());
        System.out.println(page.size());
        System.out.println(page.list());
        System.out.println(page.toMap());
        for (Bean item:page) {
            item.setName(item.getId()+":"+item.getName());
        }
    }
}

```

***N+1分页***
```java
public interface NPlusOnePagingMapper {
    @Select("SELECT * FROM a")
    @NPlusOnePaginator
    NPlusOnePage<Bean> select();
    
    public static void main(String[] args){
        NPlusOnePagingMapper mapper = xxx;
        Paginator.paginate(2,10);
        NPlusOnePage<Bean> page = mapper.select();
        System.out.println(page);
        System.out.println(page.hasNext());
        System.out.println(page.toMap());
        for (Bean item:page) {
            item.setName(item.getId()+":"+item.getName());
        }
    }
}

```

进阶
--------
***无侵入物理分页***
```java
public interface SimplePagingMapper {
    @Select("SELECT * FROM a")
    @NormalPaginator(cache=true,cacheExpiryTime=3600)
    List<Bean> select(RowBounds rowBounds);
}
```
***无侵入物理分页进阶(取分页结构)***
```java
public interface SimplePagingMapper {
    @Select("SELECT * FROM a")
    @NormalPaginator(cache=true,cacheExpiryTime=3600)
    List<Bean> select(PagingRowBounds rowBounds);
    
    public static void main(String[] args){
        NPlusOnePagingMapper mapper = xxx;
        List<Bean> list = mapper.select();
        int page = 0;
        int size = 10;
        PagingRowBounds rowBounds = new PagingRowBounds(page,size);
        NormalPaginator page = rowBounds.toNormalPage(NormalPaginator.class, list);
    }
}
```
***设置起始页偏移量(用以兼容前端分页参数，通常前端分页从1开始)***
```java
public interface SimplePagingMapper {
    //第一种方式
    @Select("SELECT * FROM a")
    @NormalPaginator(pageOffset=1)
    List<Bean> select();

    //第二种方式(通过RowBounds 的传参方式，注解内pageOffset将不会生效)
    @Select("SELECT * FROM a")
    @NormalPaginator()
    List<Bean> selectRowBounds(PagingRowBounds rowBounds);
    public static void main(String[] args){
        NPlusOnePagingMapper mapper = xxx;
        List<Bean> list = mapper.select();
        int page = 0;
        int size = 10;
        int pageOffset = 1;
        boolean forceCounting = true;
        PagingRowBounds rowBounds = new PagingRowBounds(page,size,pageOffset, forceCounting);
        NormalPaginator page = rowBounds.toNormalPage(NormalPaginator.class, list);
    }
}
```
***通过当前GET请求自动注入分页参数***
```java
@Mapper
public interface SimplePagingMapper {
    @Select("SELECT * FROM a")
    @NormalPaginator(auto = true,pageOffset = 1)
    SimplePage<Bean> select();
}
@RestController
public class PagingController{
    @Autowired
    SimplePagingMapper simplePagingMapper;
    /**
     * http://127.0.0.1:8080/test?page=1&size=20
     * {
     * 	"total": 16,
     * 	"size": 10,
     * 	"total_page": 2,
     * 	"has_last": false,
     * 	"has_next": true,
     * 	"page": 1,
     * 	"list": [{
     * 		"id": 1,
     * 		"name": "a",
     * 		"value": "v1"
     *        }, {
     * 		"id": 2,
     * 		"name": "a",
     * 		"value": "v1"
     *    }, {
     * 		"id": 3,
     * 		"name": "a",
     * 		"value": "v1"
     *    }, {
     * 		"id": 4,
     * 		"name": "d",
     * 		"value": "v4"
     *    }, {
     * 		"id": 5,
     * 		"name": "e",
     * 		"value": "v5"
     *    }, {
     * 		"id": 6,
     * 		"name": "f",
     * 		"value": "v6"
     *    }, {
     * 		"id": 7,
     * 		"name": "g",
     * 		"value": "v7"
     *    }, {
     * 		"id": 8,
     * 		"name": "h",
     * 		"value": "v8"
     *    }, {
     * 		"id": 9,
     * 		"name": "i",
     * 		"value": "v9"
     *    }, {
     * 		"id": 10,
     * 		"name": "j",
     * 		"value": "v10"
     *    }]
     * }
     */
    @RequestMapping("/test")
    public Map test(HttpServletRequest request){
        //默认自动读取page 和 size参数，如不存在则取默认值, 参数key和默认值可在配置修改
        Map map = simplePagingMapper.select().toMap();
        return map;
    }
}
```
***启用Count结果缓存***
```java
public interface SimplePagingMapper {
    @Select("SELECT * FROM a")
    //缓存有效时间3600秒
    @NormalPaginator(cache=true,cacheExpiryTime=3600)
    SimplePage<Bean> select();
}
```

***自定义Count方法***
```java
public interface SimplePagingMapper {
    @Select("SELECT count(1) FROM a where id != ${bb} or id != #{aa}")
    Integer customCount(String aa, Integer bb);

    //与Count方法参数必须要完全一致！
    @Select("SELECT * FROM a where id != ${bb} or id != #{aa}")
    @NormalPaginator(countMethod = "customCount")
    SimplePage<Bean> customCountSelect(String aa, Integer bb);
}
```

***自定义Limit语句(兼容XML Mapper)***
```java
public interface SimplePagingMapper {
    @Select("SELECT count(1) FROM a where id != ${bb} or id != #{aa}")
    Integer customCount(String aa, Integer bb);

    //开启自定义limit后，必须同时自定义count方法
    //提供:offset :limit :end(与oracle等数据库配套使用)三个内置变量
    @Select("SELECT * FROM a WHERE id != ${bb} OR id != #{aa} LIMIT :offset,:limit")
    @NormalPaginator(customLimit = true, countMethod = "customCount")
    SimplePage<Bean> customLimitSelect(String aa, Integer bb);
}
```

***自定义分页返回数据结构***
```java
public class MyPagingResult<T> extends NormalPageWrapperBase<T> {

    private int total;
    private transient int startOffset;
    private transient int physicalPage;
    private int page;
    private int size;
    private int totalPage;
    private List<T> list;

    public boolean hasNext(){
        return list.size()>=size;
    }

    public boolean hasLast(){
        return physicalPage>0;
    }

    public int size(){
        return size;
    }

    public int page(){
        return page;
    }

    public int total(){
        return total;
    }

    public int totalPage(){
        return totalPage;
    }

    public List<T> list() {
        return list;
    }

    /**
     * Paged callback
     * @param list
     * @param count
     * @param startOffset
     * @param physicalPage
     * @param size
     */
    @Override
    public void onInit(List<T> list, int count, int startOffset, int physicalPage, int size) {
        this.list = list;
        this.total = count;
        this.physicalPage = physicalPage;
        this.startOffset = startOffset;
        this.page = physicalPage + startOffset;
        this.size = size;
        this.totalPage = total/size+(total%size==0?0:1);
    }
}
public interface SimplePagingMapper {
    @Select("SELECT * FROM a")
    @NormalPaginator()
    MyPagingResult<Bean> select();
}
```
配置
--------
```yaml
paginator:
  # 根据Get请求自动完成参数注入Key
  size-key: size
  page-key: page
  # 根据Get请求自动注入默认页大小
  default-size: 10
```
License
--------
    MIT License
    
    Copyright (c) 2020 xiwh
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.