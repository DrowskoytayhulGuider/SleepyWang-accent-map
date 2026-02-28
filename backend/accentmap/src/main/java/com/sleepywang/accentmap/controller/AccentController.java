package com.sleepywang.accentmap.controller;

import com.baomidou.mybatisplus.extension.api.R;
import com.sleepywang.accentmap.dto.AccentIdDTO;
import com.sleepywang.accentmap.dto.FullAccentName;
import com.sleepywang.accentmap.entity.*;
import com.sleepywang.accentmap.service.AccentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/accent")
public class AccentController {
    @Autowired
    private AccentService accentService;
    @GetMapping("/languages")
    public ResponseEntity<List<Language>>getAllLanguages()
    {
        return ResponseEntity.ok(accentService.getAllLanguages());
    }
    @GetMapping("/dialects")
    public ResponseEntity<List<Dialect>>getAllDialects()
    {
        return ResponseEntity.ok(accentService.getAllDialects());
    }
    @GetMapping("/subdialects")
    public ResponseEntity<List<SubDialect>>getAllSubDialect()
    {
        return ResponseEntity.ok(accentService.getAllSubDialects());
    }
    @GetMapping("/accents")
    public ResponseEntity<List<Accent>>getAllAccent()
    {
        return ResponseEntity.ok(accentService.getAllAccents());
    }
    @GetMapping("/dialects/{lId}")
    public ResponseEntity<List<Dialect>>getDialectsByLId(@PathVariable int lId)
    {
        return ResponseEntity.ok(accentService.getDialectsByLId(lId));
    }
    @GetMapping("/subdialects/{dId}")
    public ResponseEntity<List<SubDialect>>getSubDialectsByLId(@PathVariable int dId)
    {
        return ResponseEntity.ok(accentService.getSubDialectsByDId(dId));
    }
    @GetMapping("/accents/{sId}")
    public ResponseEntity<List<Accent>>getAccentsByLId(@PathVariable int sId)
    {
        return ResponseEntity.ok(accentService.getAccentsBySId(sId));
    }
    @GetMapping("/counties")
    public ResponseEntity<List<County>>getCountiesByAccent(String language,String dialect,String subDialect,String accent) throws IOException {
        System.out.println(language+","+dialect+","+subDialect+","+accent);
        return ResponseEntity.ok(accentService.getCountiesByAccent(language,dialect,subDialect,accent));
    }
    @GetMapping("/accent/{adcode}")
    public ResponseEntity<List<FullAccentName>>getAccentByAdcode(@PathVariable String adcode)
    {
        return ResponseEntity.ok(accentService.getAccentByAdcode(adcode));
    }
    @PostMapping("/county/{adcode}/accent")
    public ResponseEntity<?>addNewAccentForCounty(@PathVariable String adcode,@RequestBody AccentIdDTO accentIdDTO)
    {
//        System.out.println(accentIdDTO);
        accentService.addNewAccentForCounty(adcode,accentIdDTO);
        return ResponseEntity.ok(accentIdDTO.getCdId());
    }
    @DeleteMapping("/county-dialect/{cdId}")
    public ResponseEntity<Void>deleteCountyDialect(@PathVariable int cdId)
    {
        accentService.deleteCountyDialect(cdId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/language")
    public ResponseEntity<?>addNewLanguage(@RequestBody Language language)
    {
        accentService.addLanguage(language);
        return ResponseEntity.ok().body(language.getLId());
    }
    @PutMapping("/language")
    public ResponseEntity<?>updateLanguage(@RequestBody Language language)
    {
        accentService.updateLanguage(language);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/language/{lId}")
    public ResponseEntity<?>deleteLanguage(@PathVariable int lId)
    {
        accentService.deleteLanguage(lId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/dialect")
    public ResponseEntity<?>addNewDialect(@RequestBody Dialect dialect)
    {
        accentService.addDialect(dialect);
        return ResponseEntity.ok().body(dialect.getDId());
    }
    @PutMapping("/dialect")
    public ResponseEntity<?>updateDialect(@RequestBody Dialect dialect)
    {
        accentService.updateDialect(dialect);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/dialect/{dId}")
    public ResponseEntity<?>deleteDialect(@PathVariable int dId)
    {
        accentService.deleteDialect(dId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/subdialect")
    public ResponseEntity<?>addNewSubDialect(@RequestBody SubDialect subDialect)
    {
        accentService.addSubDialect(subDialect);
        return ResponseEntity.ok().body(subDialect.getSId());
    }
    @PutMapping("/subdialect")
    public ResponseEntity<?>updateSubDialect(@RequestBody SubDialect subDialect)
    {
        accentService.updateSubDialect(subDialect);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/subdialect/{sId}")
    public ResponseEntity<?>deleteSubDialect(@PathVariable int sId)
    {
        accentService.deleteSubDialect(sId);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/accent")
    public ResponseEntity<?>addNewAccent(@RequestBody Accent accent)
    {
        accentService.addAccent(accent);
        return ResponseEntity.ok().body(accent.getAId());
    }
    @PutMapping("/accent")
    public ResponseEntity<?>updateAccent(@RequestBody Accent accent)
    {
        accentService.updateAccent(accent);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/accent/{aId}")
    public ResponseEntity<?>deleteAccent(@PathVariable int aId)
    {
        accentService.deleteAccent(aId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/language/{lId}")
    public ResponseEntity<Language>getLanguageByLId(@PathVariable int lId)
    {
        return ResponseEntity.ok().body(accentService.getLanguageByLId(lId));
    }
    @GetMapping("/dialect/{dId}")
    public ResponseEntity<Dialect>getDialectByLId(@PathVariable int dId)
    {
        return ResponseEntity.ok().body(accentService.getDialectByDId(dId));
    }
    @GetMapping("/subdialect/{sId}")
    public ResponseEntity<SubDialect>getSubDialectByLId(@PathVariable int sId)
    {
        return ResponseEntity.ok().body(accentService.getSubDialectBySId(sId));
    }
//    @GetMapping("/accent/{aId}")
//    public ResponseEntity<Accent>getAccentByLId(@PathVariable int aId)
//    {
//        return ResponseEntity.ok().body(accentService.getAccentByAId(aId));
//    }
}
