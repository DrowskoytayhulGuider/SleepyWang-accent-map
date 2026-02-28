package com.sleepywang.accentmap.controller;

import com.sleepywang.accentmap.entity.Marker;
import com.sleepywang.accentmap.service.MarkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/marker")
public class MarkerController {
    @Autowired
    private MarkerService markerService;
    @GetMapping("/markers")
    public ResponseEntity<List<Marker>> getAllMarkers()
    {
        return ResponseEntity.ok(markerService.getAllMarkers());
    }
    @GetMapping("/markers/accent")
    public ResponseEntity<List<Marker>> getMarkersByAccent(String language,String dialect,String subdialect,String accent)
    {
        System.out.println(language+" "+dialect+" "+subdialect+" "+accent);
        return ResponseEntity.ok(markerService.getMarkersByAccent(language,dialect,subdialect,accent));
    }
    @PostMapping("/marker")
    public ResponseEntity<?> addNewMarker(@RequestBody Marker marker)
    {
        System.out.println(marker);
        markerService.addNewMarker(marker);
        return ResponseEntity.ok(marker.getMId());
    }
    @DeleteMapping ("/marker/{mId}")
    public ResponseEntity<?>getAccentByAdcode(@PathVariable int mId)
    {
        markerService.deleteMarkerByMId(mId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping ("/marker")
    public ResponseEntity<?>updateMarker(@RequestBody Marker marker)
    {
        System.out.println(marker);
        markerService.updateMarker(marker);
        return ResponseEntity.noContent().build();
    }
}
