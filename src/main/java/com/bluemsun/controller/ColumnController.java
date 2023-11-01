package com.bluemsun.controller;

import com.bluemsun.entity.Blog;
import com.bluemsun.entity.Column;
import com.bluemsun.entity.JsonResponse;
import com.bluemsun.entity.Page;
import com.bluemsun.interceptor.TokenChecker;
import com.bluemsun.service.ColumnService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/column")
public class ColumnController
{
    @Resource
    ColumnService columnService;

    @PostMapping()
    @TokenChecker("admin")
    public JsonResponse add(@RequestBody Column column) {
        Boolean val = columnService.insertColumn(column);
        if (val) {
            return new JsonResponse(2000, "add successfully");
        }
        return new JsonResponse(2001, "fail to add");
    }

    @PutMapping()
    @TokenChecker("admin")
    public JsonResponse edit(@RequestBody Column column) {
        Boolean val = columnService.updateColumn(column);
        if (val) {
            return new JsonResponse(2000, "edit successfully");
        }
        return new JsonResponse(2001, "fail to edit");
    }

    @GetMapping()
    public JsonResponse get(@RequestParam Long columnId) {
        return new JsonResponse(2000
                , "get the column successfully"
                , columnService.getColumn(columnId));
    }

    @GetMapping("/all")
    public JsonResponse getAll() {
        return new JsonResponse(2000
                , "get all columns successfully"
                , columnService.getAllColumn());
    }

    @GetMapping("/blog/amount")
    public JsonResponse getBlogAmount(@RequestParam Long columnId) {
        return new JsonResponse(2000
                , "get the amount of blogs in column"
                , columnService.getBlogAmount(columnId));
    }

    @PostMapping("/blog/page")
    public JsonResponse getBlogsInColumnPage(HttpServletRequest request) throws IOException {
        InputStream is = request.getInputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(is);
        JsonNode columnIdNode = jsonNode.get("columnId");
        JsonNode pageNode = jsonNode.get("page");
        Long columnId = objectMapper.convertValue(columnIdNode, Long.class);
        Page<Blog> page = objectMapper.convertValue(pageNode, Page.class);
        page.init();
        columnService.getBlogsInColumnPage(columnId, page);
        return new JsonResponse(2000
                , "get the page successfully"
                , page.list);
    }
}
