package com.shivam.iitcampusproject.Filters;

import android.widget.Filter;

import com.shivam.iitcampusproject.Adapter.SellerShowProducts;
import com.shivam.iitcampusproject.Model.Products;

import java.util.ArrayList;

public class FilterProduct extends Filter {

    private SellerShowProducts adapter;
    private ArrayList<Products> filterList;

    public FilterProduct(SellerShowProducts adapter, ArrayList<Products> filterList) {
        this.adapter = adapter;
        this.filterList = filterList;
    }


    @Override
    protected FilterResults performFiltering(CharSequence charSequence) {
        FilterResults results = new FilterResults();
        if (charSequence !=null && charSequence.length() > 0){


            charSequence = charSequence.toString().toUpperCase();

            ArrayList<Products> filterModel = new ArrayList<>();
            for (int i=0; i<filterList.size(); i++){

                if (filterList.get(i).getProduct_name().toUpperCase().contains(charSequence) ||
                        filterList.get(i).getCategory().toUpperCase().contains(charSequence)){

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
        adapter.productList = (ArrayList<Products>) filterResults.values;
        adapter.notifyDataSetChanged();
    }
}
