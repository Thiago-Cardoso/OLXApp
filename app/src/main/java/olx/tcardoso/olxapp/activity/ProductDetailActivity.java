package olx.tcardoso.olxapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

import olx.tcardoso.olxapp.R;
import olx.tcardoso.olxapp.model.Advertisement;

public class ProductDetailActivity extends AppCompatActivity {

    private CarouselView carouselView;
    private TextView title;
    private TextView description;
    private TextView state;
    private TextView value;
    private Advertisement advertisementSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        //initialize components
        initializeComponents();

        //Configure toolbar

        //Recovery advertisement for show
        advertisementSelect =  (Advertisement) getIntent().getSerializableExtra("anuncioSelecionado");
        if(advertisementSelect != null){
            title.setText(advertisementSelect.getTitle());
            description.setText(advertisementSelect.getDescription());
            state.setText(advertisementSelect.getState());
            value.setText(advertisementSelect.getValue());

            ImageListener imageListener = new ImageListener() {
                @Override
                public void setImageForPosition(int position, ImageView imageView) {
                    String urlString = advertisementSelect.getPhoto().get(position);
                    Picasso.get().load(urlString).into(imageView); //load image
                }
            };

            carouselView.setPageCount(advertisementSelect.getPhoto().size());
            carouselView.setImageListener(imageListener);
        }

    }

    public void visualizePhone(View view){
        Intent i = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", advertisementSelect.getPhone(), null));
        startActivity(i);
    }

    private void initializeComponents(){
        carouselView = findViewById(R.id.carouselView);
        title = findViewById(R.id.textTitleDetail);
        description = findViewById(R.id.textDescriptionDetail);
        state = findViewById(R.id.textStateDetail);
        value = findViewById(R.id.textValueDetail);
    }

}
