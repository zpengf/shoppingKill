package com.zpf.shoppingKill.model.mapper;

import com.zpf.shoppingKill.model.entity.ItemKillSuccess;
import com.zpf.shoppingKill.model.entity.ItemKillSuccessExample;

import java.util.ArrayList;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ItemKillSuccessMapper {
    int countByExample(ItemKillSuccessExample example);

    int deleteByExample(ItemKillSuccessExample example);

    int deleteByPrimaryKey(String code);

    int insert(ItemKillSuccess record);

    int insertSelective(ItemKillSuccess record);

    List<ItemKillSuccess> selectByExample(ItemKillSuccessExample example);

    ItemKillSuccess selectByPrimaryKey(String code);

    int updateByExampleSelective(@Param("record") ItemKillSuccess record, @Param("example") ItemKillSuccessExample example);

    int updateByExample(@Param("record") ItemKillSuccess record, @Param("example") ItemKillSuccessExample example);

    int updateByPrimaryKeySelective(ItemKillSuccess record);

    int updateByPrimaryKey(ItemKillSuccess record);


    ItemKillSuccess queryByCode(@Param("code")String code);

    int countByKillUserId(@Param("killId") Integer killId, @Param("userId") Integer userId);

    void expireOrder(@Param("code")String code);

    ArrayList<ItemKillSuccess> selectAllPayOrder();
}