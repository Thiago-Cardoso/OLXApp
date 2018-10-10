package olx.tcardoso.olxapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import olx.tcardoso.olxapp.R;
import olx.tcardoso.olxapp.model.Advertisement;

public class AdapterAdvertisement  extends RecyclerView.Adapter<AdapterAdvertisement.MyViewHolder> {

    private List<Advertisement> advertisements;
    private Context context;

    public AdapterAdvertisement(List<Advertisement> advertisements, Context context) {
        this.advertisements = advertisements;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_advertisement, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Advertisement advertisement = advertisements.get(position);
        holder.title.setText(advertisement.getTitle());
        holder.value.setText(advertisement.getValue());

        //loading image using library picasso
        List<String> urlPicture = advertisement.getPhoto();
        String urlCover= urlPicture.get(0);

        Picasso.get().load(urlCover).into(holder.picture);
    }

    @Override
    public int getItemCount() {
        return advertisements.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView title;
        TextView value;
        ImageView picture;

        public MyViewHolder(View itemView){
            super(itemView);

            title = itemView.findViewById(R.id.textTitle);
            value = itemView.findViewById(R.id.textValue);
            picture = itemView.findViewById(R.id.imageAdvertisement);
        }
    }

}
