package in.assignmentsolutions.momskart.adapters;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.assignmentsolutions.momskart.R;
import in.assignmentsolutions.momskart.model.ProductModel;

public class ProductsListAdapter extends RecyclerView.Adapter < ProductsListAdapter.ProductHolder > {

    List<ProductModel> productModelList = null;

    @NonNull
    @Override
    public ProductsListAdapter.ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //productModelList = new ArrayList<>();

        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_item, parent, false);
        ProductHolder vh = new ProductHolder(mView);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
        holder.setTitle(productModelList.get(position).getTitle());
        holder.setDescription(productModelList.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        if (productModelList == null)
            return 0;
        return productModelList.size();
    }

    class ProductHolder extends RecyclerView.ViewHolder {

        TextView title, description;
        ImageView image;

        public ProductHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.id_product_title);
            description = itemView.findViewById(R.id.id_product_description);
            image = itemView.findViewById(R.id.id_product_img);
        }

        public void setTitle(String title) {
            this.title.setText(title);
        }

        public void setDescription(String description) {
            this.description.setText(description);
        }

        public void setImage(Bitmap image) {
            this.image.setImageBitmap(image);
        }
    }

    public void addData(List<ProductModel> list) {
        if(productModelList == null)
            productModelList = new ArrayList<>();
        for (ProductModel pm : list) {
            productModelList.add(pm);
        }
    }

}
