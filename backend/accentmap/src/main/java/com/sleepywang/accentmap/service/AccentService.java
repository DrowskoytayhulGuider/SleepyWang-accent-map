package com.sleepywang.accentmap.service;

import com.sleepywang.accentmap.dao.*;
import com.sleepywang.accentmap.dto.AccentIdDTO;
import com.sleepywang.accentmap.dto.FullAccentName;
import com.sleepywang.accentmap.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class AccentService {
    @Autowired
    private AccentMapper accentMapper;
    @Autowired
    private LanguageMapper languageMapper;
    @Autowired
    private DialectMapper dialectMapper;
    @Autowired
    private SubDialectMapper subDialectMapper;
    @Autowired
    private CountyMapper countyMapper;
    public List<Language> getAllLanguages()
    {
        return languageMapper.getAllLanguages();
    }
    public Dialect getDialectByDId(int dId)
    {
        Dialect dialect=dialectMapper.getDialectByDId(dId);
        dialect.setLanguage(languageMapper.getLanguageByLId(dialect.getLId()));
        return dialect;
    }
    public List<Dialect>getAllDialects()
    {
        List<Dialect>dialects=dialectMapper.getAllDialects();
        for(int i=0;i<dialects.size();++i)
        {
            dialects.get(i).setLanguage(languageMapper.getLanguageByLId(dialects.get(i).getLId()));
        }
        return dialects;
    }
    public SubDialect getSubDialectBySId(int sId)
    {
        SubDialect subDialect=subDialectMapper.getSubDialectBySId(sId);
        subDialect.setDialect(getDialectByDId(subDialect.getDId()));
        return subDialect;
    }
    public List<SubDialect>getAllSubDialects()
    {
        List<SubDialect>subDialects=subDialectMapper.getAllSubDialects();
        for(int i=0;i<subDialects.size();++i)
        {
            subDialects.get(i).setDialect(getDialectByDId(subDialects.get(i).getDId()));
        }
        return subDialects;
    }
    public List<Accent>getAllAccents()
    {
        List<Accent>accents=accentMapper.getAllAccents();
        for(int i=0;i<accents.size();++i)
        {
            accents.get(i).setSubDialect(getSubDialectBySId(accents.get(i).getSId()));
        }
        return accents;
    }
    public List<County>getCountiesByAccent(String language,String dialect,String subDialect,String accent) throws IOException {
        List<County>counties = countyMapper.getCountiesByAccent(language,dialect,subDialect,accent);
        for(County county: counties)
        {
            county.setPolygon(CountyService.decodeAndDecompressBoundary(county.getPolygon()));
            System.out.println(county.getPolygon());
        }
        return counties;
    }

    public List<Dialect> getDialectsByLId(int lId)
    {
        List<Dialect> dialects = dialectMapper.getDialectsByLId(lId);
        Language language=languageMapper.getLanguageByLId(lId);
        for (int i=0;i<dialects.size();++i)
        {
            dialects.get(i).setLanguage(language);
        }
        return dialects;
    }
    public List<SubDialect> getSubDialectsByDId(int dId)
    {
        List<SubDialect> subDialects = subDialectMapper.getSubDialectsByDId(dId);
        Dialect dialect=dialectMapper.getDialectByDId(dId);
        for (int i=0;i<subDialects.size();++i)
        {
            subDialects.get(i).setDialect(dialect);
        }
        return subDialects;
    }
    public List<Accent> getAccentsBySId(int sId)
    {
        List<Accent> accents = accentMapper.getAccentsBySId(sId);
        SubDialect subDialect=subDialectMapper.getSubDialectBySId(sId);
        for (int i=0;i<accents.size();++i)
        {
            accents.get(i).setSubDialect(subDialect);
        }
        return accents;
    }
    public List<FullAccentName> getAccentByAdcode(String adcode)
    {
        return accentMapper.getAccentByAdcode(adcode);
    }
    public void addNewAccentForCounty(String adcode, AccentIdDTO accentIdDTO)
    {
        accentMapper.insertNewAccentForCounty(adcode,accentIdDTO);
    }
    public void deleteCountyDialect(int cdId)
    {
        accentMapper.deleteCountyDialect(cdId);
    }
    public void deleteLanguage(int lId)
    {
        languageMapper.deleteById(lId);
    }
    public void updateLanguage(Language language)
    {
        languageMapper.updateById(language);
    }
    public void addLanguage(Language language)
    {
        languageMapper.insert(language);
    }

    public void deleteDialect(int dId)
    {
        dialectMapper.deleteById(dId);
    }
    public void updateDialect(Dialect dialect)
    {
        dialectMapper.updateById(dialect);
    }
    public void addDialect(Dialect dialect)
    {
        dialectMapper.insert(dialect);
    }

    public void deleteSubDialect(int sId)
    {
        subDialectMapper.deleteById(sId);
    }
    public void updateSubDialect(SubDialect subDialect)
    {
        subDialectMapper.updateById(subDialect);
    }
    public void addSubDialect(SubDialect subDialect)
    {
        subDialectMapper.insert(subDialect);
    }

    public void deleteAccent(int aId)
    {
        accentMapper.deleteById(aId);
    }
    public void updateAccent(Accent accent)
    {
        accentMapper.updateById(accent);
    }
    public void addAccent(Accent accent)
    {
        accentMapper.insert(accent);
    }
    public Language getLanguageByLId(int lId)
    {
        return languageMapper.getLanguageByLId(lId);
    }
    public Accent getAccentByAId(int aId)
    {
        return accentMapper.selectById(aId);
    }
}
