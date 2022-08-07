package com.shivam.iitcampusproject.Filters;

import android.widget.Filter;

import com.shivam.iitcampusproject.Adapter.SellerShowOrdersAdapter;
import com.shivam.iitcampusproject.Model.ShowShopOrders;

import java.util.ArrayList;

public class FilterOrders extends Filter {

    private SellerShowOrdersAdapter adapter;
    private ArrayList<ShowShopOrders> filterList;

    public FilterOrders(SellerShowOrdersAdapter adapter, ArrayList<ShowShopOrders> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }


    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if (charSequence !=null && charSequence.length() > 0){


            charSequence = charSequence.toString().toUpperCase();

            ArrayList<ShowShopOrders> filterModel = new ArrayList<>();
            for (int i=0; i<filterList.size(); i++){

                if (filterList.get(i).getOrderStatus().toUpperCase().contains(charSequence)){

                    filterModel.add(filterList.get(i));

                }
            }
            results.count = filterModel.size();
            results.values = filterModel;
        }
        else {
            results.count = filterList.size();
            results.values = filterList;
        }
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
        adapter.filterList = (ArrayList<ShowShopOrders>) filterResults.values;
        adapter.notifyDataSetChanged();
    }
}
