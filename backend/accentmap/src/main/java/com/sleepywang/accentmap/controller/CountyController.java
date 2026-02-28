package com.sleepywang.accentmap.controller;

import com.sleepywang.accentmap.entity.County;
import com.sleepywang.accentmap.entity.Language;
import com.sleepywang.accentmap.service.CountyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
//尽量使用RESTful接口风格
@RestController
@RequestMapping("/county")
public class CountyController {
    @Autowired
    private CountyService countyService;
    @PostMapping("")
    public ResponseEntity<?> addCounty(@RequestBody County county)
    {
        countyService.addCounty(county);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("")
    public ResponseEntity<List<County>> getCounty(County county) throws IOException {
        return ResponseEntity.ok().body(countyService.getCounty(county));
    }
    @GetMapping("/{cId}")
    public ResponseEntity<County> getCountyByCId(@PathVariable int cId)
    {
        return ResponseEntity.ok().body(countyService.getCountyByCId(cId));
    }
    @PutMapping("")
    public ResponseEntity<?> updateCounty(@RequestBody County county)
    {
        System.out.println(county);
        countyService.updateCounty(county);
        return ResponseEntity.noContent().build();
    }
}
