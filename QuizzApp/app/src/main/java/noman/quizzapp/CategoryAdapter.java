package noman.quizzapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import noman.quizzapp.models.CategoryModel;

public class CategoryAdapter extends BaseAdapter {
    public CategoryAdapter(List<CategoryModel> categoryList) {
        this.categoryList = categoryList;
    }

    private List<CategoryModel> categoryList;
    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView==null)
        {
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.category_items,parent,false);
        }
        else
        {
            view=convertView;
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SplashActivity.selected_cat_index=position;
                Intent intent=new Intent(parent.getContext(),SetsActivity.class);

                parent.getContext().startActivity(intent);
            }
        });
        ((TextView) view.findViewById(R.id.catName)).setText(categoryList.get(position).getId());

        return view;
    }
}
