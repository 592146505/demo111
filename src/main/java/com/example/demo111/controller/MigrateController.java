package com.example.demo111.controller;

import com.example.demo111.req.MigrateReq;
import com.example.demo111.resp.MigrateInfoResp;
import com.example.demo111.service.MigrateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

/**
 * @author zgl
 * @version v1.0
 * @since 2022/5/18 16:27
 */
@RequiredArgsConstructor
@RequestMapping
@RestController
public class MigrateController {

    private final MigrateService migrateService;

    @PostMapping(value = "/migrate", produces = MediaType.APPLICATION_JSON_VALUE)
    public String migrate(@RequestBody MigrateReq req) {
        return migrateService.migrate(req.getSourceDataSource(), req.getTargetDataSource(), req.getSize());
    }

    @GetMapping(value = "/info/{batchNo}")
    public MigrateInfoResp migrate(@PathVariable("batchNo") String batchNo) {
        return migrateService.getByBatchNo(batchNo);
    }
}
