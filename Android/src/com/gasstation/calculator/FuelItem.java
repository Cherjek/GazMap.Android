package com.gasstation.calculator;

import java.util.ArrayList;
import java.util.Date;

public class FuelItem {
	
	private long mId;
	private Date mDate;
	private double mFueled = 0.0;
	private double mPrice = 0.0;
	private double mDrived = 0.0;
	private double mTotal = 0.0;
	private double mExpense = 0.0;
	
	public FuelItem(long id, Date date, double fueled, double price, double drived) {
		mId = id;
		mDate = date;
		mFueled = fueled;
		mPrice = price;
		mDrived = drived;
		mTotal = mFueled * mPrice;
		mExpense = (mDrived < 0.001) ? 0.0 : ((mFueled / mDrived) * 100.0);		
	}
	
	public FuelItem(ArrayList<FuelItem> list) {
		int count = list.size();
		if (count == 0) {
			return;
		}
		int countWithExpense = 0;
		
		for (FuelItem item : list) {
			mFueled += item.mFueled;
			mDrived += item.mDrived;
			mTotal += item.mTotal;
			mPrice += item.mPrice;
			if (item.mExpense > 0.001) {
				mExpense += item.mExpense;
				countWithExpense ++;
			}
		}
		
		mPrice = mPrice / count;
		mExpense = mExpense / countWithExpense;
	}

	public long getId() {
		return mId;
	}

	public Date getDate() {
		return mDate;
	}

	public double getFueled() {
		return mFueled;
	}

	public double getPrice() {
		return mPrice;
	}

	public double getDrived() {
		return mDrived;
	}
	
	public double getTotal() {
		return mTotal;
	}
	
	public double getExpense() {
		return mExpense;
	}
	
	public double getOneKmPrice() {
		if (mTotal > 0.001 && mDrived > 0.001) {
			return mTotal / mDrived;
		}
		return 0.0;
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id=");
		sb.append(mId);
		sb.append("; date=");
		sb.append(ToString.date(mDate));
		sb.append("; price=");
		sb.append(mPrice);
		sb.append("; fueled=");
		sb.append(mFueled);
		sb.append("; drived=");
		sb.append(mDrived);
		sb.append("; total=");
		sb.append(mTotal);
		sb.append("; expense=");
		sb.append(mExpense);
		return sb.toString();
	}

	public void setPrice(double price) {
		mPrice = price;
		mTotal = mFueled * mPrice;
	}

	public void setFueled(double fueled) {
		mFueled = fueled;
		mTotal = mFueled * mPrice;
		mExpense = (mDrived < 0.001) ? 0.0 : ((mFueled / mDrived) * 100.0);
	}

	public void setDrived(double drived) {
		mDrived = drived;
		mExpense = (mDrived < 0.001) ? 0.0 : ((mFueled / mDrived) * 100.0);
	}
	
	
}
