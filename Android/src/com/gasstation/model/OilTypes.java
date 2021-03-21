package com.gasstation.model;

import java.util.ArrayList;

public final class OilTypes {
	
	public static long defaultType = 3;
	
	public static ArrayList<MainTab> GetOilTypes(boolean onlyOil) {
 		ArrayList<MainTab> items = new ArrayList<MainTab>();
    	items.add(new MainTab((long) 1, "Пропан"));
    	items.add(new MainTab((long) 2, "Метан"));
    	items.add(new MainTab((long) 3, "Пропан+Метан"));
    	if (!onlyOil) {
    		items.add(new MainTab((long) 4, "Сервис"));
    	}
		return items;
	}
	
	public static ArrayList<String> GetStrOilTypes(boolean onlyOil) {
		ArrayList<MainTab> items = GetOilTypes(onlyOil);
		ArrayList<String> result = new ArrayList<String>();
		for(MainTab mt : items) {
			result.add(mt.Name);
		}
		return result;
	}
	
	public static int GetIndexFromTypeId(Long typeId) {
		ArrayList<MainTab> items = OilTypes.GetOilTypes(false);
    	for(MainTab mt : items) {
    		if (mt.Id == typeId) {
    			return items.indexOf(mt);
    		}
    	}
    	return -1;
	}
	
	public static Long GetTypeIdFromIndex(int index) {
		ArrayList<MainTab> items = OilTypes.GetOilTypes(false);
    	return items.get(index).Id;
	}
}
