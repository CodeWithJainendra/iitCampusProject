package com.shivam.iitcampusproject.Filters;

import android.widget.Filter;

import com.shivam.iitcampusproject.Adapter.ShopAdapter;
import com.shivam.iitcampusproject.Model.Shops;

import java.util.ArrayList;

public class FilterCity extends Filter {

    public ShopAdapter shopAdapter;
    public ArrayList<Shops> filterlist;

    public FilterCity(ShopAdapter shopAdapter, ArrayList<Shops> filterlist) {
        this.shopAdapter = shopAdapter;
        this.filterlist = filterlist;
    }

    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if (charSequence !=null && charSequence.length() > 0){


            charSequence = charSequence.toString().toUpperCase();

            ArrayList<Shops> filterModel = new ArrayList<>();
            for (int i=0; i<filterlist.size(); i++){

                if (filterlist.get(i).getCity().toUpperCase().contains(charSequence)){

                    filterModel.add(filterlist.get(i));

                }
            }
            results.count = filterModel.size();
            results.values = filterModel;
        }
        else {
            results.count = filterlist.size();
            results.values = filterlist;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        shopAdapter.shopsList = (ArrayList<Shops>) filterResults.values;
        shopAdapter.notifyDataSetChanged();
    }
}
