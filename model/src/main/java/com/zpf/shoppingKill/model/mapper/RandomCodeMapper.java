package com.zpf.shoppingKill.model.mapper;

import com.zpf.shoppingKill.model.entity.RandomCode;
import com.zpf.shoppingKill.model.entity.RandomCodeExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RandomCodeMapper {
    int countByExample(RandomCodeExample example);

    int deleteByExample(RandomCodeExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(RandomCode record);

    int insertSelective(RandomCode record);

    List<RandomCode> selectByExample(RandomCodeExample example);

    RandomCode selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") RandomCode record, @Param("example") RandomCodeExample example);

    int updateByExample(@Param("record") RandomCode record, @Param("example") RandomCodeExample example);

    int updateByPrimaryKeySelective(RandomCode record);

    int updateByPrimaryKey(RandomCode record);
}