package com.bbkmobile.iqoo.dao;

import java.util.List;

import com.bbkmobile.iqoo.bean.Model;

public interface ModelInfoDao {

	List<Model> getModelInfo() throws Exception; // 获取所有机型

	Model findModelById(Short id) throws Exception;

	Model findModelByMdName(String md_name) throws Exception;
}
