package com.xiwh.paginator.demo.common;

import com.xiwh.paginator.demo.nplusonePaging.NPlusonePagingMapper;
import com.xiwh.paginator.demo.simplePaging.SimplePagingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@SuppressWarnings("ALL")
@RestController
class PagingController {

    @Autowired
    SimplePagingMapper simplePagingMapper;
    @Autowired
    NPlusonePagingMapper nPlusonePagingMapper;

    /**
     * http://127.0.0.1:8080/test1?page=1&size=20
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
    @RequestMapping("/test1")
    public Map requestPaging(HttpServletRequest request){
        System.out.println("[[["+Thread.currentThread().getName());
        Map map = simplePagingMapper.requestPaging("aa",123).toMap();
        System.out.println("]]]");
        return map;
    }

    /**
     * http://127.0.0.1:8080/test2?page=1&size=20
     * {
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
     *    }, {
     * 		"id": 11,
     * 		"name": "k",
     * 		"value": "v11"
     *    }, {
     * 		"id": 12,
     * 		"name": "l",
     * 		"value": "v12"
     *    }, {
     * 		"id": 13,
     * 		"name": "m",
     * 		"value": "v13"
     *    }, {
     * 		"id": 14,
     * 		"name": "n",
     * 		"value": "v14"
     *    }, {
     * 		"id": 15,
     * 		"name": "o",
     * 		"value": "v15"
     *    }, {
     * 		"id": 16,
     * 		"name": "p",
     * 		"value": "v16"
     *    }, {
     * 		"id": 17,
     * 		"name": "q",
     * 		"value": "v17"
     *    }, {
     * 		"id": 18,
     * 		"name": "r",
     * 		"value": "v18"
     *    }, {
     * 		"id": 19,
     * 		"name": "s",
     * 		"value": "v19"
     *    }, {
     * 		"id": 20,
     * 		"name": "t",
     * 		"value": "v20"
     *    }],
     * 	"size": 20
     * }
     */
    @RequestMapping("/test2")
    public Map requestNplusOnePaging(HttpServletRequest request){
        System.out.println("[[["+Thread.currentThread().getId());
        Map map =  nPlusonePagingMapper.requestPaging("aa",123).toMap();
        System.out.println("]]]");
        return map;
    }

}