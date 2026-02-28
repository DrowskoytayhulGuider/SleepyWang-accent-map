package com.sleepywang.accentmap.service;

import com.sleepywang.accentmap.dao.MarkerMapper;
import com.sleepywang.accentmap.entity.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarkerService {
    @Autowired
    private MarkerMapper markerMapper;
    public List<Marker> getAllMarkers()
    {
        return markerMapper.getAllMarkers();
    }
    public void addNewMarker(Marker marker)
    {
        markerMapper.insertMark(marker);
    }
    public List<Marker> getMarkersByAccent(String language,String dialect,String subdialect,String accent)
    {
        return markerMapper.getMarkersByAccent(language,dialect,subdialect,accent);
    }
    public void deleteMarkerByMId(int mId)
    {
        markerMapper.deleteById(mId);
    }
    public void updateMarker(Marker marker)
    {
        markerMapper.updateMarker(marker);
    }
}
