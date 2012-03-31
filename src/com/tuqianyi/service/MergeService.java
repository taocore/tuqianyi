package com.tuqianyi.service;

import java.util.logging.Logger;

import com.tuqianyi.Constants;

public class MergeService implements Constants{
	static Logger _log = Logger.getLogger(MergeService.class.getName());
	
	public int getAllowedItems(String version)
	{
		int allowedItemsCount = ALLOWED_ITEMS_FREE;
		if ("1".equals(version))
		{
			allowedItemsCount = ALLOWED_ITEMS_V1;
		}
		else if ("2".equals(version))
		{
			allowedItemsCount = ALLOWED_ITEMS_V2;
		}
		else if ("3".equals(version))
		{
			allowedItemsCount = ALLOWED_ITEMS_V3;
		}
		return allowedItemsCount;
	}
}
