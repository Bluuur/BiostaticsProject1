package com.zidongzh.mapper;

import com.zidongzh.pojo.Information;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author Zidong Zh
 * @date 2022/3/3
 */
public interface InformationMapper {

    List<Information> getAll();

    List<Information> getInfoByType(@Param("type") String type);

    void addGeneInfo(@Param("information") Information information);

    void addNonGeneInfo(@Param("information") Information information);

    List<Information> getGenes();

    List<Information> getNonGenes();

}
