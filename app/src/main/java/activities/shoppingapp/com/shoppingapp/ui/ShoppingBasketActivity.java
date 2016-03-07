package activities.shoppingapp.com.shoppingapp.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import activities.shoppingapp.com.shoppingapp.R;
import activities.shoppingapp.com.shoppingapp.adapters.BasketRecyclerViewAdapter;
import activities.shoppingapp.com.shoppingapp.common.Constant;
import activities.shoppingapp.com.shoppingapp.db.HandbagUtil;
import activities.shoppingapp.com.shoppingapp.models.HandBag;

public class ShoppingBasketActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private BasketRecyclerViewAdapter mAdapter;
    private ArrayList<HandBag> bagArrayList;
    private boolean isAutosyncOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_shopping_basket);
        inflateXml();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        MenuItem item = menu.findItem(R.id.syncSwitch);
        item.setActionView(R.layout.on_off_switch);
        return true;
    }

    /**
     * Inflating view and intializing other objects
     */
    private void inflateXml() {
        mRecyclerView = (RecyclerView) findViewById(R.id.items_recyclerview);
        mRecyclerView.setHasFixedSize(true);
        getdataFromIntent();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }

    /**
     * Checking the value for autoSync mode is true or false
     */
    private void getdataFromIntent() {
        isAutosyncOn = getSharedPreferences(Constant.TAG, MODE_PRIVATE).getBoolean(Constant.AUTO_SYNC_STATUS, false);
        if (isAutosyncOn) {
            new getHandBagsFromDb().execute();
        } else {
            bagArrayList = (ArrayList<HandBag>) getIntent().
                    getSerializableExtra(Constant.SELECTED_ITEMS);
            if (bagArrayList != null) {
                mAdapter = new BasketRecyclerViewAdapter(bagArrayList, ShoppingBasketActivity.this, isAutosyncOn);
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }


    /**
     * getting all the product added to cart from database
     */
    private class getHandBagsFromDb extends AsyncTask<Void, Void, ArrayList<HandBag>> {
        @Override
        protected ArrayList<HandBag> doInBackground(Void... params) {
            bagArrayList = HandbagUtil.getProducts(ShoppingBasketActivity.this);

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<HandBag> aVoid) {
            super.onPostExecute(aVoid);
            mAdapter = new BasketRecyclerViewAdapter(bagArrayList, ShoppingBasketActivity.this, isAutosyncOn);
            mRecyclerView.setAdapter(mAdapter);
        }
    }
}
