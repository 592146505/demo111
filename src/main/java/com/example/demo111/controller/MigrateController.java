package com.example.demo111.controller;

import com.example.demo111.req.MigrateReq;
import com.example.demo111.service.MigrateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author roamer
 * @version v1.0
 * @since 2022/5/18 19:27
 */
@RequiredArgsConstructor
@RequestMapping
@RestController
public class MigrateController {

    private final MigrateService migrateService;

    @PostMapping("migrate")
    public String migrate(@RequestBody MigrateReq req) {
        return migrateService.migrate(req.getSourceDataSource(), req.getTargetDataSource());
    }
}
