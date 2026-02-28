package com.sleepywang.accentmap.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sleepywang.accentmap.dao.CountyMapper;
import com.sleepywang.accentmap.entity.County;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPInputStream;

@Service
public class CountyService {
    @Autowired
    private CountyMapper countyMapper;
    public void addCounty(County county)
    {
        countyMapper.insert(county);
    }
    public List<County> getCounty(County county) throws IOException {
        LambdaQueryWrapper<County>wrapper=new LambdaQueryWrapper<>();
        wrapper
                .eq(county.getCName()!=null,County::getCName,county.getCName())
                .eq(county.getAdcode()!=null,County::getAdcode,county.getAdcode());
        List<County>counties = countyMapper.selectList(wrapper);
        for(County countyOfCounties: counties)
        {
            countyOfCounties.setPolygon(CountyService.decodeAndDecompressBoundary(countyOfCounties.getPolygon()));
            System.out.println(countyOfCounties.getPolygon());
        }
        return counties;
    }
    public County getCountyByCId(int cId)
    {
        return countyMapper.selectById(cId);
    }
    public void updateCounty(County county)
    {
        countyMapper.updateById(county);
    }
    public static String decodeAndDecompressBoundary(String base64EncodedData) throws IOException {
        // 1. 使用 Base64 解码
        byte[] compressedData = Base64.getDecoder().decode(base64EncodedData);

        // 2. 使用 GZIPInputStream 解压数据
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedData);
        GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
        InputStreamReader inputStreamReader = new InputStreamReader(gzipInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        // 3. 将解压后的数据读取并返回
        StringBuilder decompressedData = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            decompressedData.append(line);
        }

        return decompressedData.toString(); // 返回解压后的数据（GeoJSON）
    }
}
