package com.zpf.shoppingKill.model.mapper;

import com.zpf.shoppingKill.model.entity.ItemKill;
import com.zpf.shoppingKill.model.entity.ItemKillExample;
import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface ItemKillMapper {
    int countByExample(ItemKillExample example);

    int deleteByExample(ItemKillExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ItemKill record);

    int insertSelective(ItemKill record);

    List<ItemKill> selectByExample(ItemKillExample example);

    ItemKill selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") ItemKill record, @Param("example") ItemKillExample example);

    int updateByExample(@Param("record") ItemKill record, @Param("example") ItemKillExample example);

    int updateByPrimaryKeySelective(ItemKill record);

    int updateByPrimaryKey(ItemKill record);

    List<ItemKill> selectAll();

    ItemKill selectByItemId(@Param("id") Integer id);

    ItemKill selectByItemIdV2(@Param("id") Integer id);


    int updateKillItem(@Param("killId") Integer killId);


    int updateKillItemV2(@Param("killId") Integer killId);

}